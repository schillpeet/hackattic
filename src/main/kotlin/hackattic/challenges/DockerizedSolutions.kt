package hackattic.challenges

import com.fasterxml.jackson.annotation.JsonProperty
import hackattic.HackatticClient
import tools.jackson.module.kotlin.jacksonObjectMapper

private data class DockerizedSolutionsProblem(
    val credentials: Credentials,
    @field:JsonProperty("ignition_key") val ignitionKey: String,
    @field:JsonProperty("trigger_token") val triggerToken: String,
)
private data class Credentials(
    val user: String,
    val password: String,
)

private data class DockerizedSolutionsTrigger(
    @field:JsonProperty("registry_host") val registryHost: String,
)

private data class DockerizedSolutionsSolution(
    val secret: String,
)

data class RepositoryCatalog(val repositories: List<String>)
data class RepositoryTags(val name: String, val tags: List<String>)

class DockerizedSolutions(
    val challengeName: String,
    val hackatticClient: HackatticClient,
    val host: String
) : ITask {
    fun triggerUrl(token: String) = "_/push/$token"

    override fun run(playground: Boolean) {
        // get credentials
        val mapper = jacksonObjectMapper()
        val problem = hackatticClient.getProblem(challengeName)
        val dsProblem = mapper.readValue(problem, DockerizedSolutionsProblem::class.java)
            .also { println(it) }

        // set credentials
        val processCred = ProcessBuilder(
            "htpasswd",
            "-bBc",
            "${System.getProperty("user.dir")}/challenge_work/docker/registry-auth/htpasswd",
            dsProblem.credentials.user,
            dsProblem.credentials.password
        ).start()
        processCred.waitFor()


        // clean registry
        ProcessBuilder("docker", "stop", "registry").start().waitFor()
        ProcessBuilder("docker", "rm", "registry").start().waitFor()
            .also { println("clean state: ready for registry") }

        val command = listOf(
            "docker", "run", "-d",
            "-p", "5001:5000",
            "--name", "registry",
            "-v", "${System.getProperty("user.dir")}/challenge_work/docker/registry-auth:/auth",
            "-v", "${System.getProperty("user.dir")}/challenge_work/docker/registry-data:/var/lib/registry",
            "-e", "REGISTRY_AUTH=htpasswd",
            "-e", "REGISTRY_AUTH_HTPASSWD_REALM=Registry Realm",
            "-e", "REGISTRY_AUTH_HTPASSWD_PATH=/auth/htpasswd",
            "registry:2"
        )
        val process = ProcessBuilder(command).inheritIO().start()
        process
            .also { println("Process started") }


        // trigger hackattic server
        val myHost = mapper.writeValueAsString(DockerizedSolutionsTrigger(host))
        hackatticClient.triggerEndpoint(triggerUrl(dsProblem.triggerToken), myHost)

        // login with credentials
        ProcessBuilder(
            "docker", "login", "localhost:5001",
            "-u", dsProblem.credentials.user, "-p", dsProblem.credentials.password
            ).start()

        // get repository name
        val psFetchRepoName = ProcessBuilder("curl", "-u",
            "${dsProblem.credentials.user}:${dsProblem.credentials.password}",
            "http://localhost:5001/v2/_catalog"
            ).start()
        val inputRepoName = psFetchRepoName.inputStream.bufferedReader().readText()

        val repoName = mapper.readValue(inputRepoName, RepositoryCatalog::class.java)
            .repositories.first()
            .also { println("repoName: $it") }

        // get tags of repository: curl -u username:password http://localhost:5001/v2/hack/tags/list
        val psFetchTags = ProcessBuilder("curl", "-u",
            "${dsProblem.credentials.user}:${dsProblem.credentials.password}",
            "http://localhost:5001/v2/hack/tags/list"
        ).start()
        val inputTags = psFetchTags.inputStream.bufferedReader().readText()
        val tags: List<String> = mapper.readValue(inputTags, RepositoryTags::class.java).tags
            .also { println("tags: $it") }

        tags.forEach { tag ->
            // pull images
            ProcessBuilder("docker", "pull", "localhost:5001/$repoName:$tag").start().waitFor()

            // run container
            val psContainer = ProcessBuilder("docker", "run", "--rm",
                "--platform", "linux/amd64", "localhost:5001/$repoName:$tag").start()

            // check if output contains "IGNITION_KEY wasn't set, bailing out"
            val check = psContainer.inputStream.bufferedReader().readText()
            if (check.contains("IGNITION_KEY wasn't set, bailing out")) {
                // set ignition key and receive the solution
                val psSecret = ProcessBuilder(
                    "docker", "run", "--rm",
                    "--platform", "linux/amd64",
                    "-e", "IGNITION_KEY=${dsProblem.ignitionKey}",
                    "localhost:5001/$repoName:$tag"
                    ).start()
                val secretKey = psSecret.inputStream.bufferedReader().readText().trim()
                    .also { println("secretKey: $it") }
                val solution = mapper.writeValueAsString(DockerizedSolutionsSolution(secretKey))
                hackatticClient.submitSolution(challengeName, solution, playground)
            }
        }
    }
}
package hackattic.challenges

import com.fasterxml.jackson.annotation.JsonProperty
import hackattic.HackatticClient
import tools.jackson.module.kotlin.jacksonObjectMapper


private data class HostingGitProblem(
    @field:JsonProperty("ssh_key") val sshKey: String,
    val username: String,
    @field:JsonProperty("repo_path") val repoPath: String,
    @field:JsonProperty("push_token") val pushToken: String
)

private data class HostingGitTrigger(
    @field:JsonProperty("repo_host") val repoHost: String,
)

private data class HostingGitSolution(
    val secret: String,
)

class HostingGit(
    val challengeName: String,
    val hackatticClient: HackatticClient,
    val host: String,
) : ITask {
    fun triggerUrl(token: String) = "_/git/$token"

    override fun run(playground: Boolean) {
        val mapper = jacksonObjectMapper()
        val problem = hackatticClient.getProblem(challengeName)
        val credentials = mapper.readValue(problem, HostingGitProblem::class.java)
            .also { println("credentials: $it") }
//        val credentials = HostingGitProblem(
//            sshKey = "",
//            username = "hack",
//            repoPath = "little/snow.git",
//            pushToken = "49d3b441.5107.4a95.9bdc.e23123b5cc1d"
//        )


        // to be sure there is no further image
        ProcessBuilder("docker", "rm", "-f", "openssh-server")
		.redirectErrorStream(true)	
		.start().waitFor()

        // build docker image
        ProcessBuilder(
            "docker", "build",
            "--build-arg", "USER_NAME=${credentials.username}",
            "--build-arg", "PUBLIC_KEY=${credentials.sshKey}",
            "--build-arg", "REPO_PATH=${credentials.repoPath}",
            "-t", "challenge-image", "."
        ).redirectErrorStream(true).start().waitFor()

        // start the container
        ProcessBuilder(
            "docker", "run", "-d",
            "--name", "openssh-server",
            "-p", "22:2222",
            "challenge-image"
        ).start()

        Thread.sleep(3000)

        ProcessBuilder(
            "docker", "exec", "openssh-server",
            "sh", "-c",
            "mkdir -p /config/${credentials.repoPath} && " +
            "git init --bare /config/${credentials.repoPath} && " +
            "chown -R ${credentials.username}:${credentials.username} /config/${credentials.repoPath}"
        ).start().waitFor()

        // trigger endpoint
        val myHost = mapper.writeValueAsString(HostingGitTrigger(host))
            .also { println("myHost: $it") }
        hackatticClient.triggerEndpoint(triggerUrl(credentials.pushToken), myHost)

//        println("let us sleep")
//        Thread.sleep(15_000)
//        println("wake up")

        // read secret
        val psGetSecret = ProcessBuilder(
            "docker", "exec", "-u", credentials.username,
            "openssh-server", "git", "-C", "/config/${credentials.repoPath}",
            "show", "main:solution.txt"
        ).start()
        val solutionTxt = psGetSecret.inputStream.bufferedReader().readText().trim()
            .also { println("solutionTxt: $it") }

        psGetSecret.waitFor()

        val solution = mapper.writeValueAsString(HostingGitSolution(solutionTxt))
        hackatticClient.submitSolution(challengeName, solution, playground)
    }
}

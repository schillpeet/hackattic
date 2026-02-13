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

    /**
     * Testing:
     * host should be: challenge.mnemobyte.de
     * $ git remote add origin hack@challenge.mnemobyte.de:little/snow.git
     * $ git push origin main
     */
    override fun run(playground: Boolean) {
        val mapper = jacksonObjectMapper()
//        val problem = hackatticClient.getProblem(challengeName)
//        val credentials = mapper.readValue(problem, HostingGitProblem::class.java)
//            .also { println(it) }
        val credentials = HostingGitProblem(
            sshKey = "sshKey=ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQDog0/SdLmysud83Zmte3H+jonJq7NVELk4TaXfovSoQwcvQQUqsq1uC+ruiQSp9v8EylZep8Gs8RTF4sqCKzdqn8qWL3KrqNERhDwv9e/aYT21Ug9ub9M1UJm1mI4wQExgYdJDlqiIERZILqYCDiywM8yvXjncnpAw7wk0LIRuXhiEJTXYBw/IMUbToIoBQamg1bfpPGqcTBIHRNbFOjjDnr6bMUPb93fMinfk8Mz6EIVtPMcveclZHMnSWYOyKSEZsgTHyuJVyA3y8b6e5sNsDEWAw2/MbRsLTSAMp8NzPAy/TMHiKr4LZtm/784NmENCcb35wbnPh/jEuryzVCaqQYdULNls+AQ9w+03l5jxmoWkI0i5DuAxU/CtbMeawyXXrH3TeYk6G/Yvc0+QyWOD5G+iSFsWLvL9TfCPKu+RcKwiTSmFsFMLOLoe1fIuljuFAJfALfMX8uQqGxB2azYiVFkzMWOJDTASTNI9790xSqVrGmMoGViL0eMonmEII8xMA+q9igSsq3dBb+JrUy5LmDY32j0PAvQJPq/10WyrPwumJIjEMpQ9/rGy3Ywvcph/npAGJ0QnIWsh0EVQMfVUca+bDqYotqC6Rm8fCqQSLIIzgTF0aavhNuwzdeI3QaadkXy5PXCtcJeTf9dEOa1O0UV4I3xnWo+dEilF68TzGw== hosting_git",
            username = "hack",
            repoPath = "little/snow.git",
            pushToken = "49d3b441.5107.4a95.9bdc.e23123b5cc1d"
        )


        // to be sure there is no further image
        ProcessBuilder("docker", "rm", "-f", "openssh-server").start().waitFor()

        // build docker image
        ProcessBuilder(
            "docker", "build",
            "--build-arg", "USER_NAME=${credentials.username}",
            "--build-arg", "PUBLIC_KEY=${credentials.sshKey}",
            "--build-arg", "REPO_PATH=${credentials.repoPath}",
            "-t", "challenge-image", "."
        ).start().waitFor()

        // start the container
        ProcessBuilder(
            "docker", "run", "-d",
            "--name", "openssh-server",
            "-p", "22:2222",
            "challenge-image"
        ).start()


        // trigger endpoint
        val myHost = mapper.writeValueAsString(HostingGitTrigger(host))
            .also { println("myHost: $it") }
//        hackatticClient.triggerEndpoint(triggerUrl(credentials.pushToken), myHost)
        println("let us sleep")
        Thread.sleep(15_000)
        println("wake up")

        // read secret
        val psGetSecret = ProcessBuilder(
            "docker", "exec", "-u", credentials.username,
            "openssh-server", "git", "-C", "/config/${credentials.repoPath}",
            "show", "main:solution.txt"
        ).start()
        val solutionTxt = psGetSecret.inputStream.bufferedReader().readText().trim()
            .also { println("solutionTxt: $it") }

        psGetSecret.waitFor()

//        val solution = mapper.writeValueAsString(HostingGitSolution(solutionTxt))
//        hackatticClient.submitSolution(challengeName, solution, playground)
    }
}
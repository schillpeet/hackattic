package hackattic.challenges

import com.fasterxml.jackson.annotation.JsonProperty
import hackattic.HackatticClient
import tools.jackson.module.kotlin.jacksonObjectMapper

private data class BackupRestoreProblem(val dump: String)
private data class SSN(
    @field:JsonProperty("alive_ssns") val aliveSSNs: List<String>
)

class BackupRestore(val challengeName: String, val hackatticClient: HackatticClient) : ITask {

    override fun run(playground: Boolean) {
        val problem = hackatticClient.getProblem(challengeName)
        val mapper = jacksonObjectMapper()
        val dumpBase64 = mapper.readValue(problem, BackupRestoreProblem::class.java).dump

        val process = ProcessBuilder("scripts/hackattic-backup-restore.sh", dumpBase64).start()
        val output = process.inputStream.bufferedReader().readText().trim().split('\n')

        val result = SSN(output)
        val solution = mapper.writeValueAsString(result)
        hackatticClient.submitSolution(challengeName, solution, playground)
    }
}
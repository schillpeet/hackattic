package hackattic.challenges

import hackattic.HackatticClient
import tools.jackson.databind.JsonNode
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.security.MessageDigest


private data class MiniMinerProblem(
    val difficulty: Int,
    val block: Block
)
private data class Block(
    val nonce: Int?,
    val data: JsonNode
)
private data class MiniMinerSolution(
    val nonce: Int,
)


class MiniMiner(
    val challengeName: String,
    val hackatticClient: HackatticClient
): ITask {

    private fun bruteForceSha256Nonce(difficulty: Int, data: JsonNode): Int {
        val md = MessageDigest.getInstance("SHA-256")
        var nonce = -1
        val datas = data.toString()
        while (true) {
            nonce++
            val block = "{\"data\":$datas,\"nonce\":$nonce}"
            val hash = md.digest(block.toByteArray())

            val fullBytes = difficulty / 8
            val valid = hash.take(fullBytes).all { it == 0.toByte() }

            if (!valid) continue
            val remainingBits = difficulty % 8
            if (remainingBits == 0) return nonce

            val rem = hash[fullBytes].toInt() and 0xff
            // example: remainingBits = 3 => (8-3)=5
            // 11111111 << 5 = 11100000...
            // 11111111 <<< 8-3 = 11100000 AND 0xFF => 00011111
            // we have always to guarantee that we have a byte, for this we need 'and 0xff' at the end
            val mask = 0xff shl (8 - remainingBits) and 0xff
            if (rem and mask != 0) continue

            return nonce
        }
    }

    override fun run(playground: Boolean) {
        val problem = hackatticClient.getProblem(challengeName)
        val mapper = jacksonObjectMapper()
        val problemJson = mapper.readValue(problem, MiniMinerProblem::class.java)
        val difficulty = problemJson.difficulty
        val data = problemJson.block.data

        val nonce = bruteForceSha256Nonce(difficulty, data)
        val solutionJson = mapper.writeValueAsString(MiniMinerSolution(nonce))
        hackatticClient.submitSolution(challengeName, solutionJson, playground)
    }
}
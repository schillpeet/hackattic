package hackattic.challenges

import com.fasterxml.jackson.annotation.JsonProperty
import hackattic.HackatticClient
import tools.jackson.module.kotlin.jacksonObjectMapper

data class AGlobalPresenceProblem(
    @field:JsonProperty("presence_token") val token: String,
)

class AGlobalPresence(
    private val javaClient: HackatticClient,
    private val clientsByCountry: Map<String, HackatticClient>
) : ITask {
    companion object {
        private const val CHALLENGE = "a_global_presence"
    }
    private fun visitTheWorld(presenceToken: String): Boolean {
        val totalVisited = mutableSetOf<String>()
        val startTime = System.currentTimeMillis()
        while(totalVisited.size < 7 && (System.currentTimeMillis() - startTime) < 10_000) {
            for ((name, client) in clientsByCountry) {
                try {
                    val response = client.getMyCountry(presenceToken)
                    val currentList = response.split(',').filter { it.isNotBlank() }
                    totalVisited.addAll(currentList)
                    println("Proxy $name -> Server response: $response (total visited: ${totalVisited.size})")
                    if (totalVisited.size >= 7) break
                } catch (e: Exception) {
                    println("Error by proxy $name: ${e.message}")
                }
            }
            Thread.sleep(300)
        }
        return totalVisited.size >= 7
    }

    override fun run(playground: Boolean) {
        val getProblem = javaClient.getProblem(CHALLENGE)
        val jacksonMapper = jacksonObjectMapper()
        val presence = jacksonMapper.readValue(getProblem, AGlobalPresenceProblem::class.java)
        val visitedAll = visitTheWorld(presence.token)

        if (visitedAll) {
            val response = javaClient.submitSolution(CHALLENGE, "{}", playground)
            println("response body:\n${response}")
        } else println("Does not visit all countries")
    }
}
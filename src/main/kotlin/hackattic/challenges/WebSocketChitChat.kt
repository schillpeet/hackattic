package hackattic.challenges

import hackattic.HackatticClient
import hackattic.utils.TokenResponse
import tools.jackson.module.kotlin.jacksonObjectMapper

data class WebSocketChitChatSolution(
    val secret: String,
)

class WebSocketChitChat(
    val challengeName: String,
    val okHttpHackatticClient: HackatticClient,
    val javaClient: HackatticClient,
) : ITask {

    override fun run(playground: Boolean) {
        val mapper = jacksonObjectMapper()
        val problem = javaClient.getProblem(challengeName)
        val tokenResponse = mapper.readValue(problem, TokenResponse::class.java)

        val secretText = okHttpHackatticClient.webSocketWithOkHttpClient(tokenResponse.token)
        val secret = secretText
            .substringAfter("\"")
            .substringBefore("\"")

        val solution = mapper.writeValueAsString(WebSocketChitChatSolution(secret))
        javaClient.submitSolution(challengeName, solution, playground)
    }
}
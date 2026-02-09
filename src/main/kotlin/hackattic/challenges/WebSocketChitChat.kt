package hackattic.challenges

import hackattic.HackatticClient
import tools.jackson.module.kotlin.jacksonObjectMapper

data class WebSocketChitChatSolution(
    val secret: String,
)

data class TokenResponse(
    val token: String
)

class WebSocketChitChat(
    val challengeName: String,
    val javaClient: HackatticClient,
    val okHttpHackatticClient: HackatticClient,
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
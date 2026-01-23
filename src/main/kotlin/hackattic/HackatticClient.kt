package hackattic

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class HackatticClient(
    private val client: HttpClient,
    private val token: String
) {
    companion object {
        private const val HACKATTIC_CHALLENGE_URL = "https://hackattic.com/challenges/"
    }

    private fun buildChallengeUrl(
        challenge: String,
        endpoint: String,
        playground: Boolean = false
    ): URI {
        val playgroundParam = if (playground) "&playground=1" else ""
        return URI.create(
            "$HACKATTIC_CHALLENGE_URL$challenge/$endpoint?access_token=$token$playgroundParam"
        )
    }

    private fun executeRequest(req: HttpRequest, challenge: String): String {
        val response = client.send(req, HttpResponse.BodyHandlers.ofString())
        require(response.statusCode() in 200..299) {
            "Request to $challenge/problem failed with status ${response.statusCode()}: ${response.body()}"
        }
        return response.body()
    }

    fun getProblem(challenge: String): String {
        val req = HttpRequest.newBuilder()
            .uri(buildChallengeUrl(challenge, "problem"))
            .GET()
            .build()
       return executeRequest(req, challenge)
    }

    private fun executeBinaryRequest(req: HttpRequest): ByteArray {
        val response = client.send(req, HttpResponse.BodyHandlers.ofByteArray())
        require(response.statusCode() in 200..299) {
            "Binary request failed with status ${response.statusCode()}"
        }
        return response.body()
    }

    fun downloadFile(url: String): ByteArray {
        val req = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build()

        return executeBinaryRequest(req)
    }

    fun sendSolution(challenge: String, solution: String, playground: Boolean): String {
        val req = HttpRequest.newBuilder()
            .uri(buildChallengeUrl(challenge, "solve", playground))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(solution))
            .build()
        return executeRequest(req, challenge)
    }
}
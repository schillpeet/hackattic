package hackattic

import okhttp3.OkHttpClient
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class HackatticClient(
    private val javaHttpClient: HttpClient? = null,
    private val okHttpClient: OkHttpClient? = null,
    private val token: String
) {
    init {
        check(!(javaHttpClient == null && okHttpClient == null)) {
            "At least one client must be configured to use HackatticClient"
        }
    }

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
        val response = javaHttpClient?.send(req, HttpResponse.BodyHandlers.ofString())
        require(response?.statusCode() in 200..299) {
            "Request to $challenge/problem failed with status ${response?.statusCode()}: ${response?.body()}"
        }
        return response?.body() ?: error("Request to $challenge/problem failed")
    }

    fun getProblem(challenge: String): String {
        val req = HttpRequest.newBuilder()
            .uri(buildChallengeUrl(challenge, "problem"))
            .GET()
            .build()
       return executeRequest(req, challenge)
    }

    private fun executeBinaryRequest(req: HttpRequest): ByteArray {
        val response = javaHttpClient?.send(req, HttpResponse.BodyHandlers.ofByteArray())
        require(response?.statusCode() in 200..299) {
            "Binary request failed with status ${response?.statusCode()}"
        }
        return response?.body() ?: error("Binary request failed")
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

    fun getMyCountry(presenceToken: String): String {
        val request = okhttp3.Request.Builder()
            .url("https://hackattic.com/_/presence/$presenceToken")
            .build()
        return okHttpClient?.newCall(request)?.execute().use { response -> response?.body?.string() ?: error("Unexpected response") }
    }

    fun testLocationResponseViaTor() {
        val request = okhttp3.Request.Builder()
            .url("https://get.geojs.io/v1/ip/country.json")
            .build()

        okHttpClient?.newCall(request)?.execute().use { response ->
            println("Location Info: ${response?.body?.string()}")
        }
    }

}
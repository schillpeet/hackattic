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

    fun getProblem(challenge: String): String {
        val req = HttpRequest.newBuilder()
            .uri(buildChallengeUrl(challenge, "problem"))
            .GET()
            .build()
        return execute(req, bodyHandler())
    }

    /**
     * Generic HTTP response body handlers for different content types.
     */
    @Suppress("UNCHECKED_CAST")
    internal inline fun <reified T> bodyHandler(): HttpResponse.BodyHandler<T> =
        when (T::class) {
            ByteArray::class -> HttpResponse.BodyHandlers.ofByteArray()
            String::class -> HttpResponse.BodyHandlers.ofString()
            else -> error("Unhandled type ${T::class}")
        } as HttpResponse.BodyHandler<T>

    /**
     * Executes an HTTP request with the given body handler.
     */
    internal fun <T> execute(
        req: HttpRequest,
        handler: HttpResponse.BodyHandler<T>
    ): T {
        val client = requireNotNull(javaHttpClient)
        val response = client.send(req, handler)

        require(response.statusCode() in 200..299) {
            "Binary request failed with status ${response.statusCode()}"
        }
        return response.body()
    }

    /**
     * Fetches content from an arbitrary URL with type-safe response handling.
     */
    internal inline fun <reified T> getProblemFromDynamicUrl(url: String): T {
        val req = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build()

        return execute(req, bodyHandler())
    }

    // todo: refactor: Some classes make an extra printout.
    fun submitSolution(challenge: String, solution: String, playground: Boolean): String {
        val req = HttpRequest.newBuilder()
            .uri(buildChallengeUrl(challenge, "solve", playground))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(solution))
            .build()
        return execute<String>(req, bodyHandler()).also { println(it) }
    }

    fun getMyCountry(presenceToken: String): String {
        val request = okhttp3.Request.Builder()
            .url("https://hackattic.com/_/presence/$presenceToken")
            .build()
        return okHttpClient?.newCall(request)?.execute().use {
            response -> response?.body?.string() ?: error("Empty response body")
        }
    }
}
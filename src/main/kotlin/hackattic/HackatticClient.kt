package hackattic

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.CountDownLatch

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

    fun webSocketWithOkHttpClient(token: String): String {
        val url = "wss://hackattic.com/_/ws/$token"

        val client = requireNotNull(okHttpClient)

        val request = Request.Builder().url(url).build()
        val done = CountDownLatch(1)

        println("Starting WebSocket connection to: $url")
        var secretText: String? = null

        // object expression or anonymous objects -> https://kotlinlang.org/docs/object-declarations.html#object-expressions
        val webSocket = client.newWebSocket(request, object : WebSocketListener() {
            var lastTimestamp = 0L

            override fun onOpen(webSocket: WebSocket, response: Response) {
                lastTimestamp = System.currentTimeMillis()
            }
            override fun onMessage(webSocket: WebSocket, text: String) {
                println(text)
                when {
                    text.startsWith("ping!") -> sendResponse(webSocket, System.currentTimeMillis() - lastTimestamp)
                    text.startsWith("hello!") -> println("Connection opened at: $lastTimestamp")
                    text.startsWith("good!") -> println("Interval correct")
                    text.startsWith("ouch!") -> println("Failed! Message: $text")
                    text.startsWith("congratulations") -> {
                        secretText = text
                        webSocket.close(1000, "done")
                        done.countDown()
                    }
                    else -> println("Unknown message: $text")
                }
            }

            private fun sendResponse(webSocket: WebSocket, timeDiff: Long) {
                lastTimestamp += timeDiff
                val response = when (timeDiff) {
                    in 0..1099 -> "700"
                    in 1100..1749 -> "1500"
                    in 1750..2249 -> "2000"
                    in 2250..2749 -> "2500"
                    in 2750..3999 -> "3000"
                    else -> "timeDiff: out of range"
                }
                println(response)
                webSocket.send(response)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                println("Connection failed: $t, $response")
                done.countDown()
            }

            // if you receive a close frame
            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                println("Connection closing: $code, $reason")
                done.countDown()
            }
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                println("Connection closed: $code, $reason")
                done.countDown()
            }
        })
        done.await()

        client.dispatcher.executorService.shutdown()
        client.connectionPool.evictAll()

        return secretText ?: error("No secret found")
    }

    private fun buildBasicChallengeUrl(
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
            .uri(buildBasicChallengeUrl(challenge, "problem"))
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
    //  and 2: do we need challenge as string or could we integrate this information to the client
    //  -> check secrets use cases as well
    fun submitSolution(challenge: String, solution: String, playground: Boolean): String {
        val req = HttpRequest.newBuilder()
            .uri(buildBasicChallengeUrl(challenge, "solve", playground))
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
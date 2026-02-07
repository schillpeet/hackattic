package hackattic

import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.request.receiveText
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.coroutines.CompletableDeferred


object JottingJWTsState {
    val received = CompletableDeferred<String>()
}

class HackatticServer {
    private var server: EmbeddedServer<*,*>? = null

    fun start() {
        server = embeddedServer(Netty, 8080, host = "0.0.0.0") {
            routing {
                get("/health") {
                    call.respondText("OK")
                }

                post("/") {
                    val body = call.receiveText()
                    println("hello from server: $body")

                    call.respondText("OK")
                    JottingJWTsState.received.complete(body)

                }
            }
        }
        server?.start(wait = false)
    }
    fun stop() {
        server?.stop(1000, 2000)
    }
}
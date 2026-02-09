package hackattic

import hackattic.challenges.JottingJWTsSolution
import io.ktor.http.ContentType
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.request.receiveText
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import tools.jackson.module.kotlin.jacksonObjectMapper

data class JwtHandler(
    val shouldContinue: Boolean,
    val solution: JottingJWTsSolution,
)

class HackatticServer(private val jwtHandler: (String) -> JwtHandler) {
    private var server: EmbeddedServer<*,*>? = null
    private val objectMapper = jacksonObjectMapper()

    fun start(onCompletion: () -> Unit) {
        server = embeddedServer(Netty, 8080, host = "0.0.0.0") {
            routing {
                get("/health") {
                    call.respondText("OK")
                }

                post("/") {
                    val body = call.receiveText()
                    val jwt = body.removePrefix("Authorization: Bearer ")

                    val (shouldContinue, solution) = jwtHandler(jwt) // callback

                    if (!shouldContinue) {
                        call.respondText(
                            objectMapper.writeValueAsString(solution),
                            ContentType.Application.Json
                        )
                        println("Sent solution, calling onCompletion")
                        onCompletion()
                    }
                }
            }
        }
        server?.start(wait = false)
    }

    fun stop() {
        server?.stop(1000, 2000)
    }
}
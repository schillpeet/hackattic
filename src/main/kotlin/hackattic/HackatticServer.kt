package hackattic

import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.Route
import io.ktor.server.routing.routing
import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.jacksonObjectMapper


class HackatticContext(
    val mapper: ObjectMapper,
    val onCompletion: () -> Unit
)

// Generic Hackattic server with a small DSL to inject challenge-specific routing logic
class HackatticServer() {
    private var server: EmbeddedServer<*,*>? = null
    private val mapper = jacksonObjectMapper()

    /**
     * Starts a Hackattic challenge server.
     *
     * The routing block is executed in a Route scope and receives a HackatticContext
     * with shared utilities (ObjectMapper, completion hook).
     */
    fun start(
        onCompletion: () -> Unit,
        block: Route.(HackatticContext) -> Unit
    ) {
        val ctx = HackatticContext(mapper, onCompletion)

        server = embeddedServer(Netty, 8080, host = "0.0.0.0") {
            routing {
                block(ctx)
            }
        }
        server?.start(wait = false)
    }

    fun stop() {
        server?.stop(1000, 2000)
    }
}
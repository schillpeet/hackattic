package hackattic.challenges

import hackattic.HackatticClient
import hackattic.JottingJWTsState
import kotlinx.coroutines.runBlocking

class JottingJWTs(
    private val client: HackatticClient,
    private val challengeName: String
): ITask {
    override fun run(playground: Boolean) {
        println("Waiting for server input")

        val body = runBlocking { JottingJWTsState.received.await() }

        println("Hello from class: $body")
    }
}
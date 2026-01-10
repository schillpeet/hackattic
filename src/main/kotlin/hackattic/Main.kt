package hackattic

import hackattic.challenges.*
import hackattic.secrets.*
import java.net.http.HttpClient


fun main() {
    val client = HttpClient.newHttpClient()
    val token: String = System.getenv("HACKATTIC_TOKEN") ?: error("HACKATTIC_TOKEN not set")
    val hackatticClient = HackatticClient(client, token)

    HelpMeUnpack(hackatticClient).run()
}

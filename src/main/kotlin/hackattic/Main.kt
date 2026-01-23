package hackattic

import hackattic.challenges.BruteForceZip
import io.github.cdimascio.dotenv.dotenv
import java.net.http.HttpClient


fun main() {
    // just for table_of_ssl: this only needs to happen once when the app is started.
//    Security.addProvider(BouncyCastleProvider())
//
    val client = HttpClient.newHttpClient()
    val token: String = dotenv()["HACKATTIC_TOKEN"] ?: error("HACKATTIC_TOKEN not set")
    val hackatticClient = HackatticClient(client, token)

    BruteForceZip(hackatticClient).run()
}

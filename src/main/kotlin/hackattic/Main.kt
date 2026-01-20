package hackattic

import hackattic.challenges.*
import hackattic.secrets.*
import io.github.cdimascio.dotenv.dotenv
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.net.http.HttpClient
import java.security.Security


fun main() {
    // just for table_of_ssl: this only needs to happen once when the app is started.
//    Security.addProvider(BouncyCastleProvider())
//
//    val client = HttpClient.newHttpClient()
//    val token: String = dotenv()["HACKATTIC_TOKEN"] ?: error("HACKATTIC_TOKEN not set")
//    val hackatticClient = HackatticClient(client, token)
//
//    TalesOfSSL(hackatticClient).run()
    Secret01().getSecretSolution()
}

package hackattic.challenges

import hackattic.Challenge
import hackattic.HackatticClient
import hackattic.Secret
import hackattic.Task
import hackattic.secrets.Secret00
import hackattic.secrets.Secret01
import io.github.cdimascio.dotenv.dotenv
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.net.http.HttpClient
import java.security.Security

class HackatticDispatcher {

    private fun runChallenge(name: Challenge, playground: Boolean)  {
        val token: String = dotenv()["HACKATTIC_TOKEN"] ?: error("HACKATTIC_TOKEN not set")
        val client = HttpClient.newHttpClient()
        val hackatticClient = HackatticClient(client, token)

        when (name) {
            Challenge.HelpMeUnpack -> {
                HelpMeUnpack(hackatticClient).run(playground)
            }
            Challenge.BruteForceZip -> {
                BruteForceZip(hackatticClient).run(playground)
            }
            Challenge.TalesOfSSL -> {
                // just for table_of_ssl: this only needs to happen once when the app is started.
                Security.addProvider(BouncyCastleProvider())
                TalesOfSSL(hackatticClient).run(playground)
            }
            else -> println("unknown challenge $name")
        }
    }

    private fun runSecret(secretName: Secret) {
        when (secretName) {
            Secret.Secret00 -> Secret00().run()
            Secret.Secret01 -> Secret01().run()
        }
    }

    fun run(task: Task, playground: Boolean = false) {
        when (task) {
            is Challenge -> runChallenge(task, playground)
            is Secret -> runSecret(task)
        }
    }
}
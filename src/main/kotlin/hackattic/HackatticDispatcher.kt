package hackattic

import hackattic.challenges.AGlobalPresence
import hackattic.challenges.BackupRestore
import hackattic.challenges.BasicFaceDetection
import hackattic.challenges.BruteForceZip
import hackattic.challenges.CollisionCourse
import hackattic.challenges.HelpMeUnpack
import hackattic.challenges.MiniMiner
import hackattic.challenges.ReadingQR
import hackattic.challenges.TalesOfSSL
import hackattic.challenges.TouchToneDialing
import hackattic.challenges.VisualBasicMath
import hackattic.challenges.WebSocketChitChat
import hackattic.secrets.Secret00
import hackattic.secrets.Secret01
import io.github.cdimascio.dotenv.dotenv
import okhttp3.OkHttpClient
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.http.HttpClient
import java.security.Security
import java.util.concurrent.TimeUnit


enum class Countries { DK, PL, SE, AT, CH, FR, NL }

class HackatticDispatcher {
    // for 'a global presence' I will use TOR to make requests from different countries
    // and use SOCKSv5 for this
    private fun getTorOkHttpClient(token: String, country: Countries): HackatticClient {
        val port = when (country) {
            Countries.DK -> 9050
            Countries.PL -> 9051
            Countries.SE -> 9052
            Countries.AT -> 9053
            Countries.CH -> 9054
            Countries.FR -> 9055
            Countries.NL -> 9056
        }

        val socksProxy = Proxy(
            Proxy.Type.SOCKS,
            InetSocketAddress("127.0.0.1", port)
        )

        val okClient = OkHttpClient.Builder()
            .proxy(socksProxy)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()

        return HackatticClient(null, okClient, token)
    }

    private fun getOkHttpClient(token: String): HackatticClient {
        val client = OkHttpClient.Builder().build()
        return HackatticClient(null, client, token)
    }

    private fun getJavaHttpClient(token: String): HackatticClient {
        val client = HttpClient.newHttpClient()
        return HackatticClient(client, null, token)
    }


    // TODO refactor this if you have more than 8 calls. User an 'enum with Factory Lambda'
    private fun runChallenge(name: Challenge, playground: Boolean)  {
        val token: String = dotenv()["HACKATTIC_TOKEN"] ?: error("HACKATTIC_TOKEN not set")

        when (name) {
            Challenge.HelpMeUnpack -> {
                val hackatticClient = getJavaHttpClient(token)
                HelpMeUnpack(hackatticClient).run(playground)
            }
            Challenge.BruteForceZip -> {
                val hackatticClient = getJavaHttpClient(token)
                BruteForceZip(hackatticClient).run(playground)
            }
            Challenge.TalesOfSSL -> {
                // just for table_of_ssl: this only needs to happen once when the app is started.
                Security.addProvider(BouncyCastleProvider())
                val hackatticClient = getJavaHttpClient(token)
                TalesOfSSL(hackatticClient).run(playground)
            }

            Challenge.AGlobalPresence -> {
                val hackatticClient = getJavaHttpClient(token)
                val clients = mapOf(
                    "DK" to getTorOkHttpClient(token, Countries.DK),
                    "PL" to getTorOkHttpClient(token, Countries.PL),
                    "SE" to getTorOkHttpClient(token, Countries.SE),
                    "AT" to getTorOkHttpClient(token, Countries.AT),
                    "CH" to getTorOkHttpClient(token, Countries.CH),
                    "FR" to getTorOkHttpClient(token, Countries.FR),
                    "NL" to getTorOkHttpClient(token, Countries.NL),
                )
                AGlobalPresence(hackatticClient, clients).run(playground)
            }

            Challenge.VisualBasicMath -> {
                val hackatticClient = getJavaHttpClient(token)
                VisualBasicMath(hackatticClient).run(playground)
            }

            Challenge.BackupRestore -> {
                val hackatticClient = getJavaHttpClient(token)
                BackupRestore(Challenge.BackupRestore.toString().toSnakeCase(), hackatticClient).run(playground)
            }

            Challenge.MiniMiner -> {
                val hackatticClient = getJavaHttpClient(token)
                MiniMiner(Challenge.MiniMiner.toString().toSnakeCase(), hackatticClient).run(playground)
            }

            Challenge.WebsocketChitChat -> {
                val okHttpHackatticClient = getOkHttpClient(token)
                val javaHackatticClient = getJavaHttpClient(token)

                val challengeName = Challenge.WebsocketChitChat.toString().toSnakeCase()
                WebSocketChitChat(
                    challengeName,
                    okHttpHackatticClient,
                    javaHackatticClient
                ).run(playground)
            }

            Challenge.ReadingQr -> {
                val hackatticClient = getJavaHttpClient(token)
                val challengeName = Challenge.ReadingQr.toString().toSnakeCase()
                ReadingQR(challengeName, hackatticClient).run(playground)
            }

            Challenge.TouchToneDialing -> {
                val javaHackatticClient = getJavaHttpClient(token)
                val challengeName = Challenge.TouchToneDialing.toString().toSnakeCase()
                TouchToneDialing(challengeName, javaHackatticClient).run(playground)
            }

            Challenge.CollisionCourse -> {
                val hackatticClient = getJavaHttpClient(token)
                val challengeName = Challenge.CollisionCourse.toString().toSnakeCase()
                CollisionCourse(challengeName, hackatticClient).run(playground)
            }

            Challenge.BasicFaceDetection -> {
                val javaClient = getJavaHttpClient(token)
                val challengeName = Challenge.BasicFaceDetection.toString().toSnakeCase()
                BasicFaceDetection(javaClient, challengeName).run(playground)
            }
        }
    }

    // TODO: refactor this -> add this to your enum class
    //  and 2: add toSnakeCase as challenge name to the other calls
    private fun String.toSnakeCase() = replace(Regex("(?<=.)([A-Z])"), "_$1").lowercase()

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
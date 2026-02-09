package hackattic.challenges

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.annotation.JsonProperty
import hackattic.HackatticClient
import hackattic.HackatticServer
import hackattic.JwtHandler
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import tools.jackson.module.kotlin.jacksonObjectMapper

data class JottingJWTsAppUrl(
    @field:JsonProperty("app_url") val appUrl: String,
)

data class JottingJWTsProblem(
    @field:JsonProperty("jwt_secret") val secret: String,
)

data class JottingJWTsSolution(
    val solution: String,
)

class JottingJWTs(
    private val hackatticClient: HackatticClient,
    private val challengeName: String,
    private val ownAppUrl: String
): ITask {
    private var jwtSecret: String? = null
    private val solution = StringBuilder()
    private val completionSignal = CompletableDeferred<Unit>()

    fun validateAndProcessJWT(token: String): JwtHandler {
        val decodedJWT = try {
            val algorithm = Algorithm.HMAC256(jwtSecret)
            val verifier: JWTVerifier = JWT.require(algorithm).build()
            verifier.verify(token)
        } catch (e: Exception) {
            println("Invalid token: ${e.message}")
            return JwtHandler(true, JottingJWTsSolution(solution.toString()))
        }

        val append = decodedJWT.getClaim("append")?.asString() ?: return JwtHandler(
            false,
            JottingJWTsSolution(solution.toString())
        )

        solution.append(append)
        return JwtHandler(true, JottingJWTsSolution(solution.toString()))
    }


    override fun run(playground: Boolean) = runBlocking {
        val mapper = jacksonObjectMapper()
        val problem = hackatticClient.getProblem(challengeName)
        jwtSecret = mapper.readValue(problem, JottingJWTsProblem::class.java).secret

        val server = HackatticServer { jwt -> validateAndProcessJWT(jwt)}

        server.start(
            onCompletion = {
                completionSignal.complete(Unit)
            }
        )

        // sends my own app url to challenge server
        val appUrl = mapper.writeValueAsString(JottingJWTsAppUrl(ownAppUrl))
        hackatticClient.submitSolution(challengeName, appUrl, playground)

        completionSignal.await()

        println("Solution is: $solution")
        server.stop()
    }
}
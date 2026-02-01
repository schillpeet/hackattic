package hackattic.utils

import tools.jackson.module.kotlin.jacksonObjectMapper

data class TokenResponse(
    val token: String
)

/**
 * When should you use this? 😄
 * If you have a temporary token that is valid for x lengths of time and remains v
 * alid for the entire duration of that period
 *
 * how to call:
 * val token = tokenProvider.getToken() ?:
 *             tokenProvider.createToken({ javaClient.getProblem(challengeName) })
 */
class TokenProvider(
    private val tokenStore: TokenStore,
) {

    fun getToken(): String? {
        val now = System.currentTimeMillis()
        val bufferedTime = 2 * 60 * 1000 // 2m buffered time

        val storedToken = tokenStore.load()
        if (storedToken != null && now < storedToken.expiresAt - bufferedTime) {
            return storedToken.token
        }
        return null
    }

    fun createToken(fetchToken: () -> String): String {
        val responseJson = fetchToken()
        val response = jacksonObjectMapper().readValue(responseJson, TokenResponse::class.java)

        val now = System.currentTimeMillis()
        val expiresAt = now + 60 * 60 * 1000

        val token = Token(
            token = response.token,
            expiresAt = expiresAt,
        )

        tokenStore.save(token)
        return token.token
    }

}
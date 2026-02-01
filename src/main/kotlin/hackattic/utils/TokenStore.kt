package hackattic.utils

import tools.jackson.module.kotlin.jacksonObjectMapper
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

data class Token(
    val token: String,
    val expiresAt: Long,
)

class TokenStore(
    private val path: Path = Paths.get(
        System.getProperty("user.dir"),
        ".cache", // TODO refactor: we have also .env file
        "wccToken.json" // this is a special name for websocket chit chat challenge, we don't need this anymore
    )
) {
    private val mapper = jacksonObjectMapper()

    fun load(): Token? {
        if (!Files.exists(path)) return null
        return mapper.readValue(path.toFile(), Token::class.java)
    }

    fun save(token: Token) {
        Files.createDirectories(path.parent)
        mapper.writeValue(path.toFile(), token)
    }
}
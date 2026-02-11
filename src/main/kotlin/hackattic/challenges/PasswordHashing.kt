package hackattic.challenges

import com.fasterxml.jackson.annotation.JsonProperty
import hackattic.HackatticClient
import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator
import org.bouncycastle.crypto.generators.SCrypt
import org.bouncycastle.crypto.macs.HMac
import org.bouncycastle.crypto.params.KeyParameter
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.security.MessageDigest
import kotlin.io.encoding.Base64
import kotlin.text.toHexString

private data class PasswordHashingProblem(
    val password: String, // the password you'll operate on
    val salt: String, // the salt we'll use - also user as a secret where necessary; keep in mind it comes base64 encoded - decode for the raw bytes
    val pbkdf2: PBKDF2,
    val scrypt: Scrypt
)
private data class PBKDF2(
    val hash: String, // the digest to use
    val rounds: Int, // the number of rounds to use
)
private data class Scrypt(
    @field:JsonProperty("N") val n: Int, // the N parameter for scrypt's KDF
    val p: Int, // the parallelization parameter
    val r: Int, // the blocksize parameter
    val buflen: Int, // intended output length in octets
    @field:JsonProperty("_control") val control: String, // example scrypt calculated for password="rosebud", salt="pepper", N=128, p=8, n=4
)

// Send all values in hexlified form, e.g. md5('foo') -> 7ddd5f60c97d589b0becc3c55d6afd25.
private data class PasswordHashingSolution(
    val sha256: String,
    val hmac: String,
    val pbkdf2: String,
    val scrypt: String,
)

class PasswordHashing(
    val challengeName: String,
    val hackatticClient: HackatticClient
) : ITask {

    private fun getSHA256(password: ByteArray): ByteArray {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(password)
        return md.digest()
    }

    private fun getHMAC(message: ByteArray, secretKey: ByteArray): ByteArray {
        val hmac = HMac(SHA256Digest())
        hmac.init(KeyParameter(secretKey))

        hmac.update(message, 0, message.size)

        val result = ByteArray(hmac.macSize)
        hmac.doFinal(result, 0)
        return result
    }

    private fun digitFromName(hash: String): SHA256Digest {
        when (hash) {
            "sha256" -> return SHA256Digest()
            else -> throw IllegalArgumentException("Unsupported hash: $hash")
        }
    }

    // DK = PBKDF2(PRF, Password, Salt, c, dkLen)
    // PRF := pseudorandom function (here: hash)
    // c := iteration count (here: rounds)
    // dkLen := desired length of the derived key: I guess 32 Bytes = 256 Bits
    private fun getPbkdf2(hash: String, password: ByteArray, salt: ByteArray, rounds: Int, dkLen: Int = 32): ByteArray {
        val digest = digitFromName(hash)
        val generator = PKCS5S2ParametersGenerator(digest)

        generator.init(password, salt, rounds)

        val params = generator.generateDerivedParameters(dkLen * 8)
        return (params as KeyParameter).key
    }

    private fun getScrypt(password: ByteArray, salt: ByteArray, n: Int, r: Int, p: Int, buflen: Int): ByteArray {
        return SCrypt.generate(password, salt, n, r, p, buflen)
    }

    override fun run(playground: Boolean) {
        val mapper = jacksonObjectMapper()
        val rawProblem = hackatticClient.getProblem(challengeName)
        val problem = mapper.readValue(rawProblem, PasswordHashingProblem::class.java)
            .also { println("problem: $it") }

        val rawBytesSalt = Base64.decode(problem.salt)
        val password = problem.password.toByteArray()

        val sha256 = getSHA256(
            password = password,
        ).also { println("sha256: ${it.toHexString()}") }

        val hmac = getHMAC(
            message = password,
            secretKey = rawBytesSalt,
        ).also { println("hmac: ${it.toHexString()}") }

        val pbkdf2 = getPbkdf2(
            hash = problem.pbkdf2.hash,
            password = password,
            salt = rawBytesSalt,
            rounds = problem.pbkdf2.rounds
        ).also { println("pbkdf2: ${it.toHexString()}") }

        val scrypt = getScrypt(
            password = password,
            salt = rawBytesSalt,
            n = problem.scrypt.n,
            r = problem.scrypt.r,
            p = problem.scrypt.p,
            buflen = problem.scrypt.buflen,
        ).also { println("scrypt: ${it.toHexString()}") }

        val hexlified = PasswordHashingSolution(
            sha256 = sha256.toHexString(),
            hmac = hmac.toHexString(),
            pbkdf2 = pbkdf2.toHexString(),
            scrypt = scrypt.toHexString(),
        )

        val solution = mapper.writeValueAsString(hexlified)
            .also { println(it) }
        hackatticClient.submitSolution(challengeName, solution, playground)
    }
}
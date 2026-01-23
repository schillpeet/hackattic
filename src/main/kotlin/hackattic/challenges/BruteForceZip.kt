package hackattic.challenges

import com.fasterxml.jackson.annotation.JsonProperty
import hackattic.HackatticClient
import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.io.File
import java.nio.file.Files

data class ZipUrl(
    @field:JsonProperty("zip_url") val url: String,
)

class BruteForceZip (
    override val hackattic: HackatticClient,
): ChallengeIT {

    companion object {
        private const val CHALLENGE = "brute_force_zip"
        private const val ZIP2JOHN_PATH = "/opt/homebrew/opt/john-jumbo/share/john/zip2john"
    }

    private fun runBruteForceWithJohnTheRipper(): String {
        // TODO DRY!
        val workDir = File("challenge_work").apply { mkdirs() }
        val zipFile = File(workDir, "secret.zip")
        val hashFile = File(workDir, "zip.hash")
        val potFile = File(workDir, "session.pot")
        val secretFile = File(workDir, "secret.txt")

        hashFile.delete()
        potFile.delete()
        secretFile.delete()

        ProcessBuilder(ZIP2JOHN_PATH, zipFile.absolutePath)
            .redirectOutput(hashFile)
            .start()
            .waitFor()

        // is there no poison pill???
        // use a mask with a set and brute force it!!!
        val johnProcess = ProcessBuilder(
            "john",
            hashFile.absolutePath,
            "-1=?l?d", // set with lowercase and digits
            "--mask=?1?1?1?1?1?1", // mask with this set
            "--min-len=4",
            "--max-len=6",
            "--fork=8",  // full power
            "--pot=${potFile.absolutePath}", // use fresh potFile
            "--session=hacksession" // clear session
        ).inheritIO().start()

        val startTime = System.currentTimeMillis()

        while (System.currentTimeMillis() - startTime < 20_000) {
            if (potFile.exists() && potFile.length() > 0) {
                break // password found
            }
            Thread.sleep(200)
        }
        johnProcess.destroyForcibly()

        val showProcess = ProcessBuilder(
            "john",
            "--show",
            "--pot=${potFile.absolutePath}",
            hashFile.absolutePath
        ).directory(workDir).start()

        val showOutput = showProcess.inputStream.bufferedReader().readText()
        val password = showOutput.split(":").getOrNull(1)?.trim()
            .also { println("password: $it") }
            ?: error("Password couldn't extract from john out: $showOutput")

        ProcessBuilder("unzip", "-o", "-P", password, zipFile.absolutePath)
            .directory(workDir)
            .start()
            .waitFor()

        return secretFile.readText().trim()
    }

    private fun getZip(mapper: ObjectMapper): ByteArray {
        val oneTimeUrl = hackattic.getProblem(CHALLENGE)
        val url = mapper.readValue(oneTimeUrl, ZipUrl::class.java).url
            .also { println("one-time-url: $it") }
        val zip = hackattic.downloadFile(url)
        return zip
    }

    private fun postSolution(solution: String, playground: Boolean) {
        val response = hackattic.sendSolution(CHALLENGE, solution, playground)
        println("response body:\n${response}")
    }

    private fun sendSecret(secret: String, mapper: ObjectMapper) {
        val solution = mapper.writeValueAsString(mapOf("secret" to secret))
        postSolution(solution, false)
    }

    override fun run(playground: Boolean) {
        val mapper = jacksonObjectMapper()
        val zipData = getZip(mapper)

        // TODO DRY!
        val workDir = File("challenge_work").apply { mkdirs() }
        val zipFile = File(workDir, "secret.zip")

        Files.write(zipFile.toPath(), zipData)
        val secret = runBruteForceWithJohnTheRipper()

        sendSecret(secret, mapper)
    }

}
package hackattic.challenges

import com.fasterxml.jackson.annotation.JsonProperty
import hackattic.HackatticClient
import tools.jackson.core.type.TypeReference
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.io.File
import java.io.FileOutputStream
import java.math.BigInteger
import java.net.URL


data class VisualBasicMathProblem(@field:JsonProperty("image_url") val imageUrl: String)
data class VisualBasicMathSolution(@field:JsonProperty("result") val result: String)
data class OcrResult(
    val sign: String,
    val numbers: String
)

class VisualBasicMath(
    private val hackatticClient: HackatticClient
) : ITask {
    companion object {
        private const val CHALLENGE = "visual_basic_math"
        private val pythonBinary = File("scripts/venv/bin/python3").absolutePath
        private val ocrScript = File("scripts/ocr_helper.py").absolutePath
    }

    internal fun calculate(results: List<OcrResult>): String {
        return results.fold(BigInteger.ZERO) { acc, item ->
            val value = item.numbers.toBigInteger()

            when (item.sign.first()) {
                '+' -> acc + value
                '-' -> acc - value
                'x' -> acc * value
                '÷' -> acc / value
                else -> error("Invalid op: ${item.sign}")
            }
        }.toString()
    }

    private fun fetchAndSubmitSolution(playground: Boolean) {
        val mapper = jacksonObjectMapper()
        val problem = hackatticClient.getProblem(CHALLENGE)
            .also { println("image url: $it") }
        val urlObj = mapper.readValue(problem, VisualBasicMathProblem::class.java)

        val imageBytes = hackatticClient.getProblemFromDynamicUrl<ByteArray>(urlObj.imageUrl)

        val debugDir = File("challenge_work/visual_basic_math_data")
        debugDir.mkdirs()
        val originalName = URL(urlObj.imageUrl).path.substringAfterLast("/")
        val imageFile = File(debugDir, "$originalName.png")

        FileOutputStream(imageFile).use { it.write(imageBytes) }

        // analyze
        val process = ProcessBuilder(
            pythonBinary,
            ocrScript,
            imageFile.absolutePath
        )
            .redirectErrorStream(true)
            .start()
        val exitCode = process.waitFor()
        val json = process.inputStream.bufferedReader().readText()

        if (exitCode != 0) {
            val errorText = process.errorStream.bufferedReader().readText()
            error("Python crashed: $errorText")
        }

        val signsAndNumbers = mapper.readValue(json, object: TypeReference<List<OcrResult>>() {})

        val result = calculate(signsAndNumbers)

        val response = hackatticClient.submitSolution(
            CHALLENGE, jacksonObjectMapper().writeValueAsString(VisualBasicMathSolution(result)), playground)
        println("Server Response: $response")
    }

    override fun run(playground: Boolean) {
        fetchAndSubmitSolution(playground)
    }
}
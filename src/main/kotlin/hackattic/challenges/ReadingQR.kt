package hackattic.challenges

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.common.HybridBinarizer
import hackattic.HackatticClient
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import javax.imageio.ImageIO

data class ReadingQRProblem(
    @field:JsonProperty("image_url") val imageUrl: String,
)
data class ReadingQRSolution(
    val code: String,
)

class ReadingQR(
    private val challengeName: String,
    private val hackatticClient: HackatticClient
): ITask {

    private fun readingQr(filename: String): String? {
        val file = File("/challenge_work/reading_qr/$filename")
        val bufferedImage = ImageIO.read(file)
        val source = BufferedImageLuminanceSource(bufferedImage)
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

        val reader = MultiFormatReader()
        return try {
            val result = reader.decode(binaryBitmap)
            result.text
        } catch (e: NotFoundException) {
            null
        }

    }

    override fun run(playground: Boolean) {
        val problem = hackatticClient.getProblem(challengeName)
        val mapper = jacksonObjectMapper()
        val imageUrl = mapper.readValue(problem, ReadingQRProblem::class.java)
        val picByteArray = hackatticClient.getProblemFromDynamicUrl<ByteArray>(imageUrl.imageUrl)

        val filename = "$challengeName.png"
        val outputDir = Paths.get("challenge_work", "reading_qr")
        val outputFile = outputDir.resolve(filename)

        Files.createDirectories(outputDir)
        Files.write(outputFile, picByteArray)

        val result = readingQr(filename)
            .also { println(it) }
            ?: error("Could not read QR image")
        val solutionJson = mapper.writeValueAsString(ReadingQRSolution(result))
        hackatticClient.submitSolution(
            challengeName, solutionJson, playground)
    }
}
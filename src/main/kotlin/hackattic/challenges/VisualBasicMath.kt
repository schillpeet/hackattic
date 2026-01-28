package hackattic.challenges

import com.fasterxml.jackson.annotation.JsonProperty
import hackattic.HackatticClient
import net.sourceforge.tess4j.Tesseract
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.math.BigInteger
import java.net.URL
import javax.imageio.ImageIO


data class ImageUrl(@field:JsonProperty("image_url") val imageUrl: String)
data class Result(@field:JsonProperty("result") val result: String)

class VisualBasicMath(
    private val hackatticClient: HackatticClient
) : TaskIT {
    companion object {
        private const val CHALLENGE = "visual_basic_math"
    }

    internal fun calculate(rawLines: String): String {
        return rawLines.trim().lines().fold(BigInteger.ZERO) { acc, line ->
            if (line.isBlank()) return@fold acc

            val cleanLine = line.replace(" ", "").trim()
            val op = cleanLine[0]

            val valueString = cleanLine.substring(1).filter { it.isDigit() }

            if (valueString.isEmpty()) return@fold acc
            val value = valueString.toBigInteger()
            //println("op: $op value: $value\tresult: $acc")

            when (op) {
                '+' -> acc.add(value)
                '-' -> acc.subtract(value)
                'x' -> acc.multiply(value)
                '÷' -> acc.divide(value)
                else -> error("Invalid op: $op")
            }
        }.toString()
    }

    internal fun thresholdedAndErosionOfPicture(image: BufferedImage? = null, example: String? = "example"): BufferedImage {
        java.util.logging.Logger.getLogger("nu.pattern.OpenCV").level = java.util.logging.Level.OFF
        nu.pattern.OpenCV.loadShared()

        val folderPath = "challenge_work/visual_basic_math_data/"
        val outputPath = "${folderPath}$example-output.png"

        val baos = ByteArrayOutputStream()
        ImageIO.write(image, "png", baos)
        val src = Imgcodecs.imdecode(MatOfByte(*baos.toByteArray()), Imgcodecs.IMREAD_COLOR)

        val gray = Mat()
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY)

        // 1. scale
        val scaled = Mat()
        Imgproc.resize(gray, scaled, Size(), 2.0, 2.0, Imgproc.INTER_AREA)

        // 2. blurring: AUF KEINEN FALL NUTZEN
//        val blurred = Mat()
//        Imgproc.GaussianBlur(scaled, blurred, Size(3.0, 3.0), 0.0)

        // 3. thresholding -> make colors to black/white - works with 193 🥳 wtf nope 👎
        val binary = Mat()
        // wrong/thresh: 5/175-193,
        Imgproc.threshold(scaled, binary, 193.0, 255.0, Imgproc.THRESH_BINARY)


        // 4. Morphological Erosion -> makes the thin ductus thicker
        val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(2.0, 2.0))
        val finalMat = Mat()
        Imgproc.erode(binary, finalMat, kernel)


        // 5. do we need this???
        val padded = Mat()
        Core.copyMakeBorder(finalMat, padded, 40, 40, 40, 40, Core.BORDER_CONSTANT, Scalar(255.0))

        Imgcodecs.imwrite(outputPath, finalMat)

        // convert Mat to butteredImage
        val mob = MatOfByte()
        Imgcodecs.imencode(".png", padded, mob)
        return ImageIO.read(ByteArrayInputStream(mob.toArray()))
    }

    internal fun readLineByLine(image: BufferedImage): String {
        val tesseract = Tesseract().apply {
            setVariable("tessedit_char_whitelist", "0123456789+-x÷")
            setDatapath(File("src/main/resources/tessdata").absolutePath)
            setLanguage("eng")
            setVariable("user_defined_dpi", "300") // Warning: Invalid resolution 1 dpi. Using 70 instead.
            setPageSegMode(6) // 4,6
            setVariable("classify_bln_numeric_mode", "1") // prefers numbers
        }
        return try {
            tesseract.doOCR(image).trim()
        } catch (e: Exception) {
            "Error by OCR: ${e.message}"
        }
    }

    private fun fetchAndSubmitSolution(playground: Boolean) {
        val problem = hackatticClient.getProblem(CHALLENGE)
        val urlObj = jacksonObjectMapper().readValue(problem, ImageUrl::class.java)
        val image = ImageIO.read(URL(urlObj.imageUrl)) // it's an API call as well
            .also { println("imageUrl: $it") }?: error("Invalid image")

        // analyze
        val preparedImage = thresholdedAndErosionOfPicture(image)
        val lines = readLineByLine(preparedImage)
        val result = calculate(lines)

        val response = hackatticClient.submitSolution(
            CHALLENGE, jacksonObjectMapper().writeValueAsString(Result(result)), playground)
        println("Server Response: $response")
    }

    override fun run(playground: Boolean) {
        fetchAndSubmitSolution(playground)
    }
}
package hackattic.challenges

import hackattic.HackatticClient
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.io.File
import java.net.URL
import javax.imageio.ImageIO

class VisualBasicMathTest {

    private val hackatticClient = mockk<HackatticClient>()
    private val task = VisualBasicMath(hackatticClient)

    @Test
    fun `should correctly recognize text from example png` () {
        // given
        val localFile = File("src/test/resources/pictures/example.png")
        val localUrl = localFile.toURI().toURL().toString()
        val fakeJsonResponse = """{"image_url": "$localUrl"}"""
        val urlObj = jacksonObjectMapper().readValue(fakeJsonResponse, ImageUrl::class.java)
        val image = ImageIO.read(URL(urlObj.imageUrl))

        val expectedText = "+5645347\nx4969403\n+4380727\n-5488893\nx9857509\n-2373279\nx1468497\n-4002123"

        // when
        val preparedImage = task.thresholdedAndErosionOfPicture(image)
        val actualText = task.readLineByLine(preparedImage)
        val actualCalculation = task.calculate(actualText)

        // then
        assertEquals(expectedText.trim(), actualText.trim())
        assertEquals("406101962502879331718194989", actualCalculation)
    }
    @Test
    fun `should correctly recognize image from example 2 png` () {
        // given
        val localFile = File("src/test/resources/pictures/example2.png")
        val localUrl = localFile.toURI().toURL().toString()
        val fakeJsonResponse = """{"image_url": "$localUrl"}"""
        val urlObj = jacksonObjectMapper().readValue(fakeJsonResponse, ImageUrl::class.java)
        val image = ImageIO.read(URL(urlObj.imageUrl))

        val expectedText2 = "-4440764\n+8664981\n÷3131620\n÷4775423\n+6096849\nx2139358\n÷8233477\n+5545160"

        // when
        val preparedImage = task.thresholdedAndErosionOfPicture(image)
        val actualText = task.readLineByLine(preparedImage)
        val actualCalculation = task.calculate(actualText)

        // then
        assertEquals(expectedText2.trim(), actualText.trim())
        assertEquals("7129343", actualCalculation)
    }

}

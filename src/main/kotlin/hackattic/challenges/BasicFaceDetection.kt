package hackattic.challenges

import ai.djl.Application
import ai.djl.modality.cv.Image
import ai.djl.modality.cv.ImageFactory
import ai.djl.modality.cv.output.DetectedObjects
import ai.djl.repository.zoo.Criteria
import com.fasterxml.jackson.annotation.JsonProperty
import hackattic.HackatticClient
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.nio.file.Files
import java.nio.file.Paths

data class BasicFaceDetectionProblem(
    @field:JsonProperty("image_url") val imageUrl: String,
)

data class BasicFaceDetectionSolution(
    @field:JsonProperty("face_tiles") val faceTiles: List<List<Int>>,
)

/**
 * I had an time issue
 * ./gradlew run  0.93s user 0.13s system 8% cpu 11.914 total
 * => too long; so I started to create a preloading model structure and a warmup additionally
 *
 */
class BasicFaceDetection(
    val javaClient: HackatticClient,
    val challengeName: String,
): ITask {

    private fun getEightXEightPics(imagePath: String): Array<Array<Image>> {
        val imagePath = Paths.get(imagePath)
        val img = ImageFactory.getInstance().fromFile(imagePath)
        val squareWidth = img.width / 8
        val squareHeight = img.height / 8

        return Array(8) { row ->
            Array(8) { col ->
                val x = col * squareWidth
                val y = row * squareHeight
                img.getSubImage(x , y, squareWidth, squareHeight)
            }
        }
    }

    private fun countFaces(): List<List<Int>> {
        val basePath = "challenge_work/$challengeName/"
        val images = getEightXEightPics("${basePath}$challengeName.jpg")
        //val outputDir = Paths.get("${basePath}output")

        val criteria = Criteria.builder()
            .setTypes(Image::class.java, DetectedObjects::class.java)
            .optApplication(Application.CV.OBJECT_DETECTION)
            .optFilter("backbone", "mobilenet_v2")
            .optArgument("threshold", 0.5) // TODO to hard?
            .build()

        val faces = ArrayList<List<Int>>()
        criteria.loadModel().use { model ->
            model.newPredictor().use { predictor ->
                //var idx = 0
                images.forEachIndexed { x, row ->
                    row.forEachIndexed { y, img ->
                        try {
                            val detection = predictor.predict(img)
                            if (detection.numberOfObjects > 0) {
                                //val outputPath = outputDir.resolve("face_${idx}.jpg")
                                //img.save(Files.newOutputStream(outputPath), "jpg")
                                faces.add(listOf(x,y))
                            }
                        } catch (e: Throwable) {
                            //println("[$idx]: Exception occurred while predicting ${e.message}")
                        }
                        //idx++
                    }
                }
            }
        }
        return faces
    }

    override fun run(playground: Boolean) {
        // get one time url
        val mapper = jacksonObjectMapper()
        val getProblem = javaClient.getProblem(challengeName)
        val oneTimeUrl = mapper.readValue(
            getProblem,
            BasicFaceDetectionProblem::class.java
        ).imageUrl.also { println("image url: $it") }

        // save images to tmp folder challenge_work
        val facesImage = javaClient.getProblemFromDynamicUrl<ByteArray>(oneTimeUrl)
        val fileName = "$challengeName.jpg"
        val outputDir = Paths.get("challenge_work", challengeName)
        val outputFile = outputDir.resolve(fileName)
        Files.createDirectories(outputDir)
        Files.write(outputFile, facesImage)

        val faces = countFaces()//.also { println("faces: $it") }

        val solution = mapper.writeValueAsString(BasicFaceDetectionSolution(faces))
        javaClient.submitSolution(challengeName, solution, playground)
    }
}
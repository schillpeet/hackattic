package hackattic.challenges

import ai.djl.Application
import ai.djl.inference.Predictor
import ai.djl.modality.cv.Image
import ai.djl.modality.cv.ImageFactory
import ai.djl.modality.cv.output.DetectedObjects
import ai.djl.repository.zoo.Criteria
import ai.djl.repository.zoo.ZooModel
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
 * 😎 It works now. Simply load the model beforehand and run it once with dummy data to initialize everything.
 */
object FaceModel {
    lateinit var model: ZooModel<Image, DetectedObjects>
        private set

    lateinit var predictor: Predictor<Image, DetectedObjects>
        private set

    fun init() {
        val criteria = Criteria.builder()
            .setTypes(Image::class.java, DetectedObjects::class.java)
            .optApplication(Application.CV.OBJECT_DETECTION)
            .optFilter("backbone", "mobilenet_v2")
            .optArgument("threshold", 0.5) // TODO to hard?
            .build()
        model = criteria.loadModel()
        predictor = model.newPredictor()
        println("Model + Predictor loaded")
    }
}

class BasicFaceDetection(
    val javaClient: HackatticClient,
    val challengeName: String,
): ITask {
    companion object {
        private const val BASE_PATH = "challenge_work/basic_face_detection/basic_face_detection.jpg"
    }

    private fun getEightXEightPics(): Array<Array<Image>> {
        val imagePath = Paths.get(BASE_PATH)
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
        val images = getEightXEightPics()
        //val outputDir = Paths.get("${basePath}output")

        val faces = ArrayList<List<Int>>()

        images.forEachIndexed { x, row ->
            row.forEachIndexed { y, img ->
                try {
                    val detection = FaceModel.predictor.predict(img)
                    if (detection.numberOfObjects > 0) {
                        //val outputPath = outputDir.resolve("face_${idx}.jpg")
                        //img.save(Files.newOutputStream(outputPath), "jpg")
                        faces.add(listOf(x,y))
                    }
                } catch (_: Throwable) {
                    //println("[$idx]: Exception occurred while predicting ${e.message}")
                }
            }
        }
        return faces
    }

    private fun warmup() {
        val path = Paths.get(System.getProperty("user.dir"), BASE_PATH)

        val dummyImage = ImageFactory.getInstance().fromFile(path)
        FaceModel.predictor.predict(dummyImage)

        println("Warmup inference done")
    }

    private fun shutdown() {
        FaceModel.predictor.close()
        FaceModel.model.close()
    }

    override fun run(playground: Boolean) {
        FaceModel.init()
        warmup()

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

        shutdown()
    }
}
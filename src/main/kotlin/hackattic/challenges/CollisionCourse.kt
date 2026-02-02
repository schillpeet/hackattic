package hackattic.challenges

import hackattic.HackatticClient
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.io.File
import java.util.Base64


data class CollisionCourseProblem(
    val include: String,
)
data class CollisionCourseSolution(
    val files: List<String>,
)

class CollisionCourse(
    val challengeName: String,
    val hackatticClient: HackatticClient
): ITask {

    override fun run(playground: Boolean) {
        val mapper = jacksonObjectMapper()
        val problem = hackatticClient.getProblem(challengeName)
        val json = mapper.readValue(problem, CollisionCourseProblem::class.java)
        val randomInput = json.include

        val workDir = File("challenge_work/collision_course").apply { mkdirs() }
        val collisionFile = File(workDir, "collisionFile.txt")
        collisionFile.writeBytes(randomInput.toByteArray(Charsets.US_ASCII))


        val fastcollProcess = ProcessBuilder(
            "scripts/fastcoll",
            "-p", collisionFile.absolutePath,
            "-o", "${workDir}/out1.bin", "${workDir}/out2.bin",
        ).start()
        fastcollProcess.waitFor()

        val md5Process = ProcessBuilder("md5sum", "${workDir}/out1.bin", "${workDir}/out2.bin")
            .inheritIO()
            .start()
        md5Process.waitFor()

        val out1b64 = Base64.getEncoder().encodeToString(File("${workDir}/out1.bin").readBytes())
        val out2b64 = Base64.getEncoder().encodeToString(File("${workDir}/out2.bin").readBytes())

        println("Base64 out1: $out1b64")
        println("Base64 out2: $out2b64")

        val solution = mapper.writeValueAsString(CollisionCourseSolution(listOf(out1b64, out2b64)))
        hackatticClient.submitSolution(challengeName, solution, playground)
    }
}
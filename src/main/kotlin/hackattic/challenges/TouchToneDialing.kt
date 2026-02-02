package hackattic.challenges

import com.fasterxml.jackson.annotation.JsonProperty
import hackattic.HackatticClient
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.nio.file.Files
import java.nio.file.Paths

data class TouchToneDialingProblem(
    @field:JsonProperty("wav_url") val wavUrl: String,
)
data class TouchToneDialingSolution(
    val sequence: String,
)

class TouchToneDialing (
    val challengeName: String,
    val javaHackatticClient: HackatticClient,
): ITask {

    override fun run(playground: Boolean) {
        val mapper = jacksonObjectMapper()
        val problem = javaHackatticClient.getProblem(challengeName)
        val oneTimeUrl = mapper.readValue(problem, TouchToneDialingProblem::class.java).wavUrl
            .also { println("oneTimeUrl: $it") }

        val wavBytes = javaHackatticClient.getProblemFromDynamicUrl<ByteArray>(oneTimeUrl)

        val filename = "$challengeName.wav"
        val outputDir = Paths.get("challenge_work", "touch_tone_dialog")
        val outputFile = outputDir.resolve(filename)

        Files.createDirectories(outputDir)
        Files.write(outputFile, wavBytes)

        val pb = ProcessBuilder(
            "multimon-ng",
            "-t",
            "wav",
            "-a",
            "DTMF",
            outputFile.toAbsolutePath().toString()
        ).start()

        val dtmfs = StringBuilder()
        pb.inputStream.bufferedReader().forEachLine { line ->
            if ("DTMF:" in line) {
                line.substringAfter("DTMF:").trim().firstOrNull()?.also { dtmfs.append(it) }
            }
        }

        // Even with multimon-ng we get duplicates from 1 and 2 because... I have no ide
        val cleanDTMFs = dtmfs.fold("") { acc, c ->
            when (c) {
                '1', '2' -> if (acc.lastOrNull() == c) acc else acc + c
                else -> acc + c
            }
        }

        println("result: $cleanDTMFs")

        val solutionJson = mapper.writeValueAsString(TouchToneDialingSolution(cleanDTMFs))
        javaHackatticClient.submitSolution(
            challengeName,
            solutionJson,
            playground,
        )
    }
}
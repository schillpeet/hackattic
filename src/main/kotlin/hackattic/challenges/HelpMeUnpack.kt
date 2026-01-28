package hackattic.challenges

import hackattic.HackatticClient
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Base64

private data class Solution(
    val int: Int,
    val uint: UInt,
    val short: Short,
    val float: Float,
    val double: Double,
    val doubleBe: Double
)

/**
 * Challenge: "Help Me Unpack"
 *
 * This class is responsible for unpacking a Base64-encoded byte array
 * into the following values, always in this order:
 *
 * 1️⃣ `int`             - 4-byte signed integer (32-bit)
 * 2️⃣ `uint`            - 4-byte unsigned integer (32-bit)
 * 3️⃣ `short`           - 2-byte signed short (16-bit) on a 32-bit plattform in this case 4-byte as well
 * 4️⃣ `float`           - 4-byte floating point number (IEEE 754, 32-bit)
 * 5️⃣ `double`          - 8-byte double precision float (64-bit)
 * 6️⃣ `bigEndianDouble` - 8-byte double in big endian (network byte order, 64-bit)
 *
 * Note:
 * - All values are little endian **except** `bigEndianDouble`
 */
class HelpMeUnpack(
    val hackattic: HackatticClient
): TaskIT {
    companion object {
        private const val CHALLENGE = "help_me_unpack"
    }

    private fun getBase64Input(): String {
        val base64Res = hackattic.getProblem(CHALLENGE)
        val regex = Regex(""""bytes"\s*:\s*"([^"]+)"""")
        return regex
            .find(base64Res)?.groupValues?.get(1)
            ?: error("Response doesn't contain bytes: '$base64Res'")
    }

    private fun getUnpackedBytes(base64Input: String): Solution {
        val encodedStr = Base64.getDecoder().decode(base64Input)
        //println(encodedStr.joinToString(" ") { "%02x".format(it) })

        val resultInt: Int = ByteBuffer
            .wrap(encodedStr, 0, 4)
            .order(ByteOrder.LITTLE_ENDIAN)
            .int

        val resultUInt: UInt = ByteBuffer
            .wrap(encodedStr, 4, 4)
            .order(ByteOrder.LITTLE_ENDIAN)
            .int.toUInt()

        val resultShort: Short = ByteBuffer
            .wrap(encodedStr, 8, 2)
            .order(ByteOrder.LITTLE_ENDIAN)
            .short

        /**
         * On a 32-bit platform, even a 16-bit short file receives 32 bits of memory
         * if the subsequent memory to be allocated is not another short file. Therefore,
         * the float accesses the memory two bytes later, i.e., at offset 12 instead of
         * offset 10.
         */
        val resultFloat: Float = ByteBuffer
            .wrap(encodedStr, 12, 4)
            .order(ByteOrder.LITTLE_ENDIAN)
            .float

        val resultDouble: Double = ByteBuffer
            .wrap(encodedStr, 16, 8)
            .order(ByteOrder.LITTLE_ENDIAN)
            .double

        // Big Endian
        val resultDoubleBigEndian: Double = ByteBuffer
            .wrap(encodedStr, 24, 8)
            .order(ByteOrder.BIG_ENDIAN)
            .double

        return Solution(resultInt, resultUInt, resultShort, resultFloat, resultDouble, resultDoubleBigEndian)
    }

    private fun postSolution(solution: Solution, playground: Boolean) {
        val output = """
            {
            "int": ${solution.int},
            "uint": ${solution.uint},
            "short": ${solution.short},
            "float": ${solution.float},
            "double": ${solution.double},
            "big_endian_double": ${solution.doubleBe}
            }
        """.trimIndent()

        val response = hackattic.submitSolution(CHALLENGE, output, playground)

        println("response body:\n${response}")
    }

    override fun run(playground: Boolean) {
        val base64Input = getBase64Input()
        val values = getUnpackedBytes(base64Input)
        postSolution(values, playground)
    }
}
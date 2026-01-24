package hackattic.secrets

import hackattic.challenges.TaskIT

/**
 * Analysis of the encrypted text:
 *
 * Observation using a standard Caesar cipher
 * (e.g. https://cryptii.com/pipes/caesar-cipher):
 *
 * - wfno → udlm (shift +2)
 * - cltu → udlm (shift +8)
 * - irza → udlm (shift +14)
 *
 * Insight:
 * The first three words all decrypt to the same result ("udlm")
 * when using different shifts. This indicates that they represent
 * the same plaintext word, but encrypted with different offsets.
 *
 * Conclusion:
 * The cipher used is a progressive Caesar cipher.
 *
 * - The shift increases with each letter.
 * - Letter 0 is shifted by the base value.
 * - Letter 1 by base + 1, letter 2 by base + 2, and so on.
 */
class Secret01: TaskIT {
    companion object {
        private const val SECRET01 = "wfno, cltu, irza! afjmapqhvf syefsm-czxiwcw kdcghp. cppjvfzbtjdtaag! kzx nkissipp mx \"pjbck-xpsft\"."
    }

    // progressive Caesar or Caesar (Shift) Cipher
    private fun progressiveCaesar(base: Int = 0): String {
        var pos = 0
        return SECRET01.map { c ->
            val result = when {
                c in 'a'..'z' -> {
                    val shift = (base + pos) % 26
                    var res = (c - 'a' - shift) % 26
                    if (res < 0) res += 26
                    'a' + res
                }
                else -> c
            }
            pos++
            result
        }.joinToString("")
    }

    /**
     * Decryption approach:
     *
     * The text was decrypted by applying a progressive Caesar cipher with
     * different base shifts. For each tested base value, one meaningful
     * plaintext word appeared at a consistent position in the output.
     *
     * Final result:
     * well, well, well! impressive puzzle-solving skills. congratulations! the solution is "harry-jacob".
     *
     * Combining the meaningful results yields the final solution isn't the final solution 😂:
     * "harry-jacob".
     */
    override fun run(playground: Boolean) {
        println(progressiveCaesar())
    }
}

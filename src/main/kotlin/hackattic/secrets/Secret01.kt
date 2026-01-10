package hackattic.secrets

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
class Secret01 {
    companion object {
        private const val SECRET01 = "wfno, cltu, irza! afjmapqhvf syefsm-czxiwcw kdcghp. cppjvfzbtjdtaag! kzx nkissipp mx \"pjbck-xpsft\"."
    }

    // progressive Caesar or Caesar (Shift) Cipher
    private fun progressiveCaesar(base: Int): String {
        var pos = 0
        return SECRET01.map { c ->
            when {
                c in 'a'..'z' -> {
                    val r = (c - 'a' - base - pos + 26 * 10) % 26
                    pos++
                    'a' + r
                }
                else -> c
            }
        }.joinToString("")
    }

    /**
     * Decryption approach:
     *
     * The text was decrypted by applying a progressive Caesar cipher with
     * different base shifts. For each tested base value, one meaningful
     * plaintext word appeared at a consistent position in the output.
     *
     * Examples:
     * - base = 0,2,4  → "well"
     * - base = 6  → "impressive"
     * - base = 7  → "puzzle"
     * - base = 8  → "solving"
     * - base = 9  → "skills"
     * - base = 11 → "congratulations"
     * - base = 13 → "the"
     * - base = 14 → "solution"
     * - base = 15 → "is"
     * - base = 17 → "harry"
     * - base = 18 → "jacob"
     *
     * Final result:
     * well, well, well! impressive puzzle-solving skills. congratulations! the solution is "harry-jacob".
     *
     * These valid words appear at the same logical positions across different
     * base shifts, confirming the use of a progressive Caesar cipher where
     * the shift increases by 1 for each letter.
     *
     * Combining the meaningful results yields the final solution:
     * "harry-jacob".
     */
    fun getSecretSolution() {
        for (base in 0..<26) {
            progressiveCaesar(base).also { println(it) }
        }
    }
}

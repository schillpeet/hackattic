package secrets

class Secret00 {
    private companion object {
        const val SECRET00 = "2DcBKCvVTEV3p83rk1DFYqNs55GzEKxCsnadxZTzKJTEJ1nEm1at1RpyAU2kdB"
    }

    /**
     * I tried base64, base62 and base58
     * you find base58 in bitcoin source code as well. It's a version of base64 without the
     * following ugly letters: 0,O,l,I,+,/
     * Not only does this prevent confusion between these letters, but it also offers the
     * advantage that, by omitting + and /, the string can be selected by double-clicking.
     *
     * + / = ? → Base64
     * 2–7 only → Base32
     * no 0 O I l → Base58
     * only hex chars → Base16
     *
     * %4==0 → Base64
     * %8==0 → Hex
     * random → Base58 / 62
     *
     */
    fun getSecretSolution() {
        val alphabet = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
        val base58Map = alphabet.withIndex().associate { it.value to it.index }

        fun base58Decode(input: String): ByteArray {
            var num = java.math.BigInteger.ZERO
            for (c in input) {
                num = num.multiply(java.math.BigInteger.valueOf(58))
                    .add(java.math.BigInteger.valueOf(base58Map[c]!!.toLong()))
            }
            return num.toByteArray()
        }

        val decoded = base58Decode(SECRET00)
        println(decoded.toString(Charsets.UTF_8))
    }
}

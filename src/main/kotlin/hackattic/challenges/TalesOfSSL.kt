package hackattic.challenges

import hackattic.HackatticClient

class TalesOfSSL(
    private val client: HackatticClient
) {
    companion object {
        private const val CHALLENGE = "tales_of_ssl"
    }

    private fun getCertificate() {
        val getProblem = client.getProblem(CHALLENGE)
        // TODO next: extract json
    }

    fun run() {

    }
}
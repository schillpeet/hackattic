package hackattic.challenges

import hackattic.HackatticClient

interface ChallengeIT {
    val hackattic: HackatticClient

    fun run(playground: Boolean = false)
}

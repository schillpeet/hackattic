package hackattic.challenges

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import hackattic.HackatticClient
import redis.clients.jedis.Jedis
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.io.encoding.Base64


data class TheRedisOneProblem(
    val rdb: String,
    val requirements: Requirements,
)

data class Requirements(
    @field:JsonProperty("check_type_of") val checkTypeOf: String,
)

data class TheRedisOneSolution(
    @field:JsonProperty("db_count") var dbCount: Int?,
    @field:JsonProperty("emoji_key_value") var emojiKeyValue: Any?,
    @field:JsonProperty("expiry_millis") var expiryMillis: Long?,
) {
    // to add checkTypeOf as dynamic field
    @JsonIgnore
    private val dynamicField: MutableMap<String, Any> = mutableMapOf()

    fun addDynamicField(key: String, value: Any) {
        dynamicField[key] = value
    }

    @JsonAnyGetter
    fun any(): Map<String, Any> = dynamicField
}


class TheRedisOne(
    val challengeName: String,
    val hackatticClient: HackatticClient
) : ITask {

    companion object {
        private val WORK_DIR = File("challenge_work/the_redis_one")
        private const val PORT = 6379
    }

    override fun run(playground: Boolean) {
        val mapper = jacksonObjectMapper()
        val solutionRaw = TheRedisOneSolution(null,null,null)

        val redisContainerName = "redis-challenge"
        val filename = "dump.rdb"

        if (WORK_DIR.exists()) WORK_DIR.deleteRecursively()
        WORK_DIR.mkdirs()

        // this is the correct header, see challenge: 'looks like the header may have been... tampered with by a truly demonic'
        val header = "REDIS".toByteArray(Charsets.UTF_8)

        // clean up
        ProcessBuilder("docker", "rm", "-f", redisContainerName)
            .start().waitFor()

        val problemRaw = hackatticClient.getProblem(challengeName)
        val problem = mapper.readValue(problemRaw, TheRedisOneProblem::class.java)
            .also { println("problem:\n$it") }


        val rdbBytes = Base64.decode(problem.rdb)

        /**
         * source: https://github.com/redis/redis/blob/unstable/src/rdb.h
         *
         * there you will find the hex opcode of expiretime:
         * #define RDB_OPCODE_EXPIRETIME_MS 252    /* Expire time in milliseconds. */
         *
         * Print out the value of 252: $ printf "0x%X\n" 252
         * result: 0xFC
         *
         * -> so, I will search the place of 0xFC
         */
        val target = 0xFC.toByte()
        rdbBytes.forEachIndexed { idx, byte ->
            if (byte == target) {
                val nextEightBytes = rdbBytes.copyOfRange(idx + 1, idx + 9)
                val expiredTimeMillis = ByteBuffer
                    .wrap(nextEightBytes)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .long
                solutionRaw.expiryMillis = expiredTimeMillis
            }
        }

        header.copyInto(rdbBytes, destinationOffset = 0)
        val file = File(WORK_DIR, filename).apply { writeBytes(rdbBytes) }


        // start container with dump file
        ProcessBuilder(
            "docker", "run", "--name", redisContainerName, "-p", "$PORT:$PORT",
            "-v", "${file.absolutePath}:/data/$filename", "-d", "redis"
        ).start().waitFor()


        val jedis = Jedis("localhost", PORT)

        val keyspaceInfo = jedis.info("keyspace")
        val dbs = keyspaceInfo.lines()
            .filter { it.startsWith("db") }
            .map { it.substringAfter("db").substringBefore(":").toInt() }

        // number of DBs
        solutionRaw.dbCount = dbs.size

        for (db in dbs) {
            jedis.select(db) // iterates throw DBs
            val keys = jedis.keys("*")
            for (key in keys) {
                val value = when (jedis.type(key)) {
                    "string" -> jedis.get(key)
                    "hash" -> jedis.hgetAll(key)
                    "list" -> jedis.lrange(key, 0, -1)
                    "set" -> jedis.smembers(key)
                    "zset" -> jedis.zrangeWithScores(key, 0, -1)
                    else -> error("Unknown key type: $key")
                }

                // check if there is an emoji
                if (key.any { it.code > 127 }) {
                    solutionRaw.emojiKeyValue = value
                }

                // Check the type you are looking for
                if (key == problem.requirements.checkTypeOf) {
                    solutionRaw.apply {
                        addDynamicField(key = key, value = jedis.type(key))
                    }
                }
            }
        }

        val solution = mapper.writeValueAsString(solutionRaw)
            .also { println("solution:\n$it") }

        hackatticClient.submitSolution(challengeName, solution, playground)
    }
}
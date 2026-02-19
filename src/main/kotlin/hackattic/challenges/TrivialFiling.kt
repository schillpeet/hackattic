package hackattic.challenges

import com.fasterxml.jackson.annotation.JsonProperty
import hackattic.HackatticClient
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.io.File
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketTimeoutException

private data class TrivialFilingProblem(
    val files: Map<String, String>
)
private data class TrivialFilingSolution(
    @field:JsonProperty("tftp_host") val tftpHost: String,
    @field:JsonProperty("tftp_port") val tftpPort: Int,
)

class TrivialFiling(
    val challengeName: String,
    val hackatticClient: HackatticClient,
    val host: String
) : ITask {

    companion object {
        private const val PORT = 6969
        //private const val SEND_PORT = 33000 // I use a fixed port instead of an ephemeral port, to guarantee that my firewall doesnt block
        private val workDir = File("challenge_work/trivial_filing")
    }

    /**
     * 2 Bytes   Opcode (1 = RRQ)
     * n Bytes   Filename
     * 1 Byte    0x00
     * n Bytes   Mode (netascii / octet)
     * 1 Byte    0x00
     *
     * $ tftp 127.0.0.1 6969
     * RRQ from /127.0.0.1:54237
     * filename: file
     * mode: octet
     */
    private fun parseTFTP(packet: DatagramPacket): String? {
        // just for testing, nmap
        if (packet.length < 2) {
            println("Packet too short for opcode")
            return null
        }

        val data = packet.data.copyOf(packet.length)

        // Opcode (first two bytes, Big Endian)
        val opcode = ((data[0].toInt() and 0xff) shl 8) or (data[1].toInt() and 0xff)
        if (opcode == 1) { // RRQ
            var idx = 2

            // read filename
            val filenameStart = idx
            while (data[idx] != 0.toByte()) idx++
            val filename = String(data, filenameStart, idx - filenameStart)

            // jump over 0x00 (null byte)
            idx++

            // read transfer mode
            val modeStart = idx
            while (data[idx] != 0.toByte()) idx++
            val mode = String(data, modeStart, idx - modeStart)

            println("RRQ filename='$filename' mode='$mode' from ${packet.address}:${packet.port}")
            if (mode != "octet") error("Only octet modes are supported")
            return filename
        }
        return null

    }

    private fun sendFile(
        filename: String,
        clientAddress: InetAddress,
        clientPort: Int,
        socket: DatagramSocket,
    ) {
        val file = File(workDir, filename)
        val fileBytes = file.readBytes()

        val transferSocket = socket
        println("TRANSFER SOCKET LOCAL PORT: ${transferSocket.localPort}")

        var blockNumber = 1
        var offset = 0


        var retries = 0
        val maxRetries = 5

        while (true) {
            val remaining = fileBytes.size - offset
            val chunkSize = minOf(512, remaining)

            val dataPacketBytes = ByteArray(4 + chunkSize)

            // Opcode = 3 (DATA)
            dataPacketBytes[0] = 0
            dataPacketBytes[1] = 3

            // Block number
            dataPacketBytes[2] = (blockNumber shr 8).toByte()
            dataPacketBytes[3] = (blockNumber and 0xff).toByte()

            System.arraycopy(fileBytes, offset, dataPacketBytes, 4, chunkSize)

            val packet = DatagramPacket(
                dataPacketBytes,
                dataPacketBytes.size,
                clientAddress,
                clientPort
            )

            transferSocket.send(packet)
            println(
                """
                SEND DATA
                block=$blockNumber
                from ${transferSocket.localPort}
                to $clientAddress:$clientPort
                size=$chunkSize
                """.trimIndent()
            )


            val ackBuffer = ByteArray(4)
            val ackPacket = DatagramPacket(ackBuffer, ackBuffer.size)

            try {
                transferSocket.receive(ackPacket)
                println(
                    """
                    RECEIVED ACK
                    from ${ackPacket.address.hostAddress}:${ackPacket.port}
                    to ${transferSocket.localPort}
                    rawLength=${ackPacket.length}
                    """.trimIndent()
                )
            } catch (e: SocketTimeoutException) {
                retries++
                if (retries >= maxRetries) {
                    println("Transfer failed: too many retries")
                    return
                }
                println("Timeout, resending block $blockNumber")
                continue
            }


            if (ackPacket.address != clientAddress || ackPacket.port != clientPort) {
                println("WARNING: ACK from unexpected TID!")
            }

            val ackOpcode = ((ackBuffer[0].toInt() and 0xff) shl 8) or
                    (ackBuffer[1].toInt() and 0xff)

            val ackBlock = ((ackBuffer[2].toInt() and 0xff) shl 8) or
                    (ackBuffer[3].toInt() and 0xff)

            if (ackOpcode != 4) continue

            if (ackBlock == blockNumber) {
                offset += chunkSize
                blockNumber++

                if (chunkSize < 512) break
                continue
            }

            if (ackBlock == blockNumber - 1) {
                println("Duplicate ACK, ignoring")
                continue
            }
        }
    }

    override fun run(playground: Boolean) {
        val mapper = jacksonObjectMapper()
        val socket = DatagramSocket(PORT)
        val buffer = ByteArray(65_536)

        if (workDir.exists()) workDir.deleteRecursively()
        workDir.mkdirs()

        val problem = hackatticClient.getProblem(challengeName)
        val files = mapper.readValue(problem, TrivialFilingProblem::class.java).files


        files.forEach { (filename, input) ->
            val file = File(workDir, filename)
            file.writeText(input)
            // we need 666 (rwx) for the puts
            file.setReadable(true, false)
            file.setWritable(true, false)
        }


        Thread {
            while (true) {
                println("start server")
                val packet = DatagramPacket(buffer, buffer.size)
                socket.receive(packet)
                println(
                    """
                === NEW PACKET ON $PORT ===
                from ${packet.address.hostAddress}:${packet.port}
                length=${packet.length}
                """.trimIndent()
                )

                // just for testing, otherwise I can't do a nmap
                if (packet.length < 2) {
                    println("Ignoring empty/invalid packet")
                    continue
                }

                val filename = parseTFTP(packet)
                if (filename == null) {
                    println("Invalid or unsupported packet")
                    continue
                }

                val file = File(workDir, filename)
                if (!file.exists()) {
                    val msg = "File not found"
                    val msgBytes = msg.toByteArray()

                    val errorPacket = ByteArray(4 + msgBytes.size + 1)
                    errorPacket[0] = 0
                    errorPacket[1] = 5 // ERROR opcode
                    errorPacket[2] = 0
                    errorPacket[3] = 1 // error code 1

                    System.arraycopy(msgBytes, 0, errorPacket, 4, msgBytes.size)
                    errorPacket[errorPacket.size - 1] = 0

                    val packet = DatagramPacket(
                        errorPacket,
                        errorPacket.size,
                        packet.address,
                        packet.port
                    )

                    socket.send(packet)
                    println("Sent ERROR: File not found")
                    continue
                }
                sendFile(filename, packet.address, packet.port, socket)
            }
        }.start()

        val solution = mapper.writeValueAsString(TrivialFilingSolution(tftpHost = host, tftpPort = PORT))
        hackatticClient.submitSolution(challengeName, solution, playground)

        Thread.sleep(Long.MAX_VALUE)
    }
}
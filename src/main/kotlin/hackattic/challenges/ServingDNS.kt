package hackattic.challenges

import com.fasterxml.jackson.annotation.JsonProperty
import hackattic.HackatticClient
import org.xbill.DNS.AAAARecord
import org.xbill.DNS.ARecord
import org.xbill.DNS.DClass
import org.xbill.DNS.Flags
import org.xbill.DNS.Message
import org.xbill.DNS.Name
import org.xbill.DNS.RPRecord
import org.xbill.DNS.Record
import org.xbill.DNS.Section
import org.xbill.DNS.TXTRecord
import org.xbill.DNS.Type
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

import kotlin.concurrent.Volatile

data class RawDnsRecord(
    val name: String,
    val type: String,
    val data: String
)
private data class ServingDNSProblem(
    val records: List<RawDnsRecord>,
)
data class DnsRecord(
    val name: Name,
    val type: Int,
    val data: String,
)

private data class ServingDNSSolution(
    @field:JsonProperty("dns_ip") val dnsIp: String,
    @field:JsonProperty("dns_port") val dnsPort: Int,
)


class UDPServer(
    private val records: List<DnsRecord>,
    private val port: Int,
): Thread() {
    private val socket: DatagramSocket = DatagramSocket(port)
    @Volatile private var running = true

    fun shutdown() {
        running = false
        socket.close()
    }

    fun DnsRecord.toDnsjavaRecord(questionName: Name): Record? = when (type) {
        Type.TXT -> TXTRecord(questionName, DClass.IN, 300, data)
        Type.A -> ARecord(questionName, DClass.IN, 300, InetAddress.getByName(data))
        Type.AAAA -> AAAARecord(questionName, DClass.IN, 300, InetAddress.getByName(data))
        Type.RP -> RPRecord(questionName, DClass.IN, 300, Name.fromString(data, Name.root), Name.root)
        else -> null
    }

    override fun run() {
        while (running) {
            try {
                // rfc1035 - 2.3.4. Size limits: UDP messages    512 octets or less
                val buf = ByteArray(512)
                val packet = DatagramPacket(buf, buf.size)
                socket.receive(packet)

                val message = Message(packet.data)
                val question = message.question // ! FQDN with trailing dot

                // 1. check if a record exists for the requested domain.
                val record = records.find { dnsRecord ->
                    if (dnsRecord.type != question.type) return@find false
                    if (dnsRecord.name == question.name) return@find true

                    // wildcard handling
                    if (dnsRecord.name.isWild) {
                        val parent = Name(dnsRecord.name, 1) // removes first label
                        return@find question.name.subdomain(parent)
                                && question.name.labels() == parent.labels() + 1
                    }
                    false
                }
                    .also { println("1. Found record: $it") }

                fun sendPacket(record: Record) {
                    println("sending packet: $record")

                    // The client sends a self-selected ID so that it knows which response to consider.
                    val response = Message(message.header.id)
                    response.header.setFlag(Flags.QR.toInt())
                    response.addRecord(question, Section.QUESTION) // it's rfc conform to send question back
                    response.addRecord(record, Section.ANSWER)

                    val responseBytes = response.toWire()
                    socket.send(
                        DatagramPacket(responseBytes, responseBytes.size, packet.address, packet.port)
                    )
                }
                record?.toDnsjavaRecord(question.name).also { println("dns record match: $it") }

                record?.toDnsjavaRecord(question.name)?.let { sendPacket(it) }
            } catch (e: Exception) {
                if (running) e.printStackTrace()
            }
        }
        socket.close()
    }
}

class ServingDNS(
    val challengeName: String,
    val hackatticClient: HackatticClient,
    val myIP: String,
) : ITask {
    private val port = 5300

    override fun run(playground: Boolean) {
        val mapper = jacksonObjectMapper()
        val problemRaw = hackatticClient.getProblem(challengeName)
        val problemRawObj = mapper.readValue(problemRaw, ServingDNSProblem::class.java)
            .also { println(it) }

        val records = problemRawObj.records.map { (name, type, data) ->
            DnsRecord(
                Name.fromString(name, Name.root), // absolute
                Type.value(type), // Int-Code
                data
            )
        }

        val startUdp = UDPServer(records = records, port = port)
        startUdp.start()

        // send ip and port to hackattic server
        val solution = mapper.writeValueAsString(ServingDNSSolution(dnsIp = myIP, dnsPort = port))
        hackatticClient.submitSolution(challengeName, solution, playground)

        println("Server starts...")
        Thread.sleep(42_000)
        println("Server ends...")

        startUdp.shutdown()
        startUdp.join()
    }
}
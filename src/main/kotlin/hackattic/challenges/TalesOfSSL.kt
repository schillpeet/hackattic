package hackattic.challenges

import com.fasterxml.jackson.annotation.JsonProperty
import com.neovisionaries.i18n.CountryCode
import hackattic.HackatticClient
import org.bouncycastle.asn1.ASN1Sequence
import org.bouncycastle.asn1.pkcs.RSAPrivateKey
import org.bouncycastle.asn1.x500.X500NameBuilder
import org.bouncycastle.asn1.x500.style.BCStyle
import org.bouncycastle.asn1.x509.Extension
import org.bouncycastle.asn1.x509.GeneralName
import org.bouncycastle.asn1.x509.GeneralNames
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.math.BigInteger
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.interfaces.RSAPrivateCrtKey
import java.security.spec.RSAPrivateCrtKeySpec
import java.security.spec.RSAPublicKeySpec
import java.util.*


data class TalesOfSSLProblem(
    @field:JsonProperty("private_key") val privateKeyBase64: String,
    @field:JsonProperty("required_data") val requiredData: RequiredData
)

data class RequiredData(
    val domain: String,
    @field:JsonProperty("serial_number") val serialNumber: String,
    val country: String
)

internal class UtilsOfTales() {
    fun generatePublicKeyFromPrivateKey(privateKey: PrivateKey): PublicKey {
        val rsaPrivateKey = privateKey as RSAPrivateCrtKey
        val spec = RSAPublicKeySpec(rsaPrivateKey.modulus, rsaPrivateKey.publicExponent)
        return KeyFactory.getInstance("RSA").generatePublic(spec)
    }

    fun getRSAPrivateKeyFromBase64(base64Key: String): PrivateKey {
        val keyBytes = Base64.getDecoder().decode(base64Key)
        val seq = ASN1Sequence.getInstance(keyBytes)
        require(seq is ASN1Sequence) { "Invalid ASN.1 structure" }
        val rsa = RSAPrivateKey.getInstance(seq)

        val keySpec = RSAPrivateCrtKeySpec(
            rsa.modulus,
            rsa.publicExponent,
            rsa.privateExponent,
            rsa.prime1,
            rsa.prime2,
            rsa.exponent1,
            rsa.exponent2,
            rsa.coefficient
        )

        val privateKey = KeyFactory.getInstance("RSA").generatePrivate(keySpec)
        return privateKey
    }

    fun countryNameToAlpha2(displayName: String): String {
        val normalizedInput = displayName.lowercase().replace(" islands", "")
        val cc = CountryCode.entries.firstOrNull {
            it.getName().lowercase().contains(normalizedInput)
        }
        return cc?.alpha2 ?: error("Unknown country code: $displayName")
    }
}

class TalesOfSSL (
    val hackattic: HackatticClient
): ITask {
    companion object {
        private const val CHALLENGE = "tales_of_ssl"
    }

    fun generateSelfSignedCertificate(certRes: TalesOfSSLProblem): String {
        val utilsOfTales = UtilsOfTales()
        val privateKey: PrivateKey = utilsOfTales.getRSAPrivateKeyFromBase64(certRes.privateKeyBase64)
        val publicKey: PublicKey = utilsOfTales.generatePublicKeyFromPrivateKey(privateKey)
        val countryAlpha2 = utilsOfTales.countryNameToAlpha2(certRes.requiredData.country)
        val domainNameFQDN = certRes.requiredData.domain

        // required data and something else. These values represent the CertificationRequestInfo
        // this information includes: Subject, Serial Number, Validity Dates (notBefore, notAfter), and the Subject Public Key Info
        val serialNumber: BigInteger = BigInteger(certRes.requiredData.serialNumber.removePrefix("0x"), 16).abs()
        val notBefore: Date = Date()
        val notAfter: Date = Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365)

        // self-signed CA means that issuer and subject are the same person
        val nameBuilder = X500NameBuilder(BCStyle.INSTANCE)
            .addRDN(BCStyle.CN, domainNameFQDN) // just the domain name, (FQDN – Fully Qualified Domain Name)
            .addRDN(BCStyle.C, countryAlpha2) // https://www.iso.org/obp/ui/#iso:code:3166:CX
        val issuer = nameBuilder.build()
        val subject = nameBuilder.build()
        val publicKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.encoded)

        // maybe, wen need the SAN name as well
        val altName = GeneralNames(GeneralName(GeneralName.dNSName, domainNameFQDN))

        // creates first a TBSCertificate structure (To-Be-Signed)
        // X500Name issuer, BigInteger serial, Date notBefore, Date notAfter, Locale dateLocale, X500Name subject, SubjectPublicKeyInfo publicKeyInfo
        val certBuilder = JcaX509v3CertificateBuilder(issuer, serialNumber, notBefore, notAfter, subject, publicKeyInfo)
            .addExtension(Extension.subjectAlternativeName, false, altName)


        // then we have to sign CSR, but before we need the signer
        val signer = JcaContentSignerBuilder("SHA256WithRSA")
            .setProvider("BC") // Bouncy Castle
            .build(privateKey)

        // sign it to generate the finished certificate
        val certHolder = certBuilder.build(signer)

        // generate base64 cert
        val certBase64 = Base64.getEncoder().encodeToString(certHolder.encoded)

        return certBase64
    }

    private fun postSolution(solution: String, playground: Boolean) {
        val response = hackattic.submitSolution(CHALLENGE, solution, playground)
        println("response body:\n${response}")
    }

    override fun run(playground: Boolean) {
        val getProblem = hackattic.getProblem(CHALLENGE)
        val mapper = jacksonObjectMapper()
        val certRes = mapper.readValue(getProblem, TalesOfSSLProblem::class.java)
        val selfSignedCertificateBase64 = generateSelfSignedCertificate(certRes)
        val solution = mapper.writeValueAsString(mapOf("certificate" to selfSignedCertificateBase64))
        postSolution(solution, playground)
    }
}
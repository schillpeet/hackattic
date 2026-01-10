package hackattic.challenges

import org.junit.jupiter.api.Test
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPublicKey
import kotlin.test.assertEquals

class TalesOfSSLTest {

    private val utilsOfTales = UtilsOfTales()

    @Test
    fun `validate that public key generation from private key works correctly`() {
        // given
        val keyPairGen = KeyPairGenerator.getInstance("RSA")
        keyPairGen.initialize(1024)

        val keyPair = keyPairGen.generateKeyPair()
        val origPrivateKey = keyPair.private
        val origPublicKey = keyPair.public as RSAPublicKey

        // when
        val generatedPublicKey = utilsOfTales.generatePublicKeyFromPrivateKey(origPrivateKey) as RSAPublicKey

        // then
        assertEquals(origPublicKey.modulus, generatedPublicKey.modulus)
        assertEquals(origPublicKey.publicExponent, generatedPublicKey.publicExponent)
    }

    @Test
    fun `validate the correct country alpha-2 code from display name`() {
        // given
        val countryDisplayName = "Christmas Island"
        val countryAlpha2Code = "CX"

        // when
        val result = utilsOfTales.countryNameToAlpha2(countryDisplayName)

        // then
        assertEquals(countryAlpha2Code, result)
    }

    @Test
    fun `validate the correct country alpha-2 code from display name with non ISO conform country name`() {
        // given
        // correct names are (https://www.iso.org/obp/ui/#iso:code:3166:CC):
        // Short name:              COCOS (KEELING) ISLANDS
        // Short name lower case:   Cocos (Keeling) Islands (the)
        val countryDisplayName = "Cocos Islands"
        val countryAlpha2Code = "CC"

        // when
        val result = utilsOfTales.countryNameToAlpha2(countryDisplayName)

        // then
        assertEquals(countryAlpha2Code, result)
    }

    @Test
    fun `validate the correct country alpha-2 code from display name with non ISO conform country name 2`() {
        // given
        // correct names are (https://www.iso.org/obp/ui/#iso:code:3166:CC):
        // Short name:              COCOS (KEELING) ISLANDS
        // Short name lower case:   Cocos (Keeling) Islands (the)
        val countryDisplayName = "Tokelau Islands"
        val countryAlpha2Code = "TK"

        // when
        val result = utilsOfTales.countryNameToAlpha2(countryDisplayName)

        // then
        assertEquals(countryAlpha2Code, result)
    }

    @Test
    fun `parse hex serial number`() {
        val input = "0xb007ab1e"
        val serial = BigInteger(input.removePrefix("0x"), 16)
        assertEquals(BigInteger("b007ab1e", 16), serial)
    }

}
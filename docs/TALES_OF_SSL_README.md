# 🔐 Tales of SSL: The Road to a Certificate

This documentation describes my journey from an abstract data structure to a finished cryptographic certificate.

## 🏗 The Layered Architecture (The Serialization Chain)

When we talk about a certificate, we go through a chain of formats. You can imagine it like an onion:

1. **ASN.1 (Abstract Syntax Notation One):** The logical “blueprint” (structure).
2. **DER (Distinguished Encoding Rules):** The binary representation of the structure (hex look).
3. **Base64:** The conversion of binary data into printable text.
4. **PEM (Privacy Enhanced Mail):** The decorative format for storing as a file.

## Examples

### ASN.1 Format (Abstract)

It almost looks like code or a JSON structure:

```text
Certificate ::= SEQUENCE {
    tbsCertificate      TBSCertificate,
    signatureAlgorithm  AlgorithmIdentifier,
    signatureValue      BIT STRING  }
```

### DER Format (Binary / Hex)

These are the actual bytes on disk. A hex dump might look like this:

```text
30 82 03 1a 30 82 02 02 a0 03 02 01 02 02 09 00 ...
```

Note: The first byte `30` often stands for a SEQUENCE in ASN.1.

### Base64 Format

The binary bytes are translated into text (4 characters for every 3 bytes):

```text
MIIC9DCCAl2gAwIBAgIJAQAAMAkGByqGSM44BAMwRTELMAkGA1UEBhMCQVUx...
```

### What is PEM?

A PEM format is essentially just the Base64-encoded DER sequence wrapped with special header and footer decorations. It always has a clear beginning and end:

```text
-----BEGIN CERTIFICATE-----
[Here are the Base64 data]
-----END CERTIFICATE-----
```

> https://darutk.medium.com/illustrated-x-509-certificate-84aece2c5c2e

## TBS – _To Be Signed_

A central concept in X.509 is the **TBS section (To Be Signed)**.  
It refers **exactly to the part of a structure that is cryptographically signed — and nothing beyond that.**

### Example: Certificate (X.509)

A CSR (Certificate Signing Request) logically consists of three parts:

1. **TBSCertificate**
   - Subject (identity)
   - SubjectPublicKeyInfo
   - Validity (NotBefore / NotAfter)
   - Extensions (SAN, KeyUsage, …)
2. **SignatureAlgorithm**
3. **SignatureValue**

So when we sign the TBS with our private key, we send it to a certification authority (CA) which, in turn, signs the certificate and sends it back to us.  
The result is a CRT (= certificate).

## Self-Signed Certificates: The Process

When we create a self-signed certificate, we take on two roles at once: the applicant and the certification authority (CA).

The process:

1. Generate a key pair: We need a private key (secret). From it, we derive the public key (public).
2. Build the TBS block: Combine your public key and identity information (name, domain).
3. Sign: Take the TBS block and sign it with your own private key.
4. Assemble: Combine the TBS block, the algorithm name, and the freshly created signature.

Result: The finished CRT (certificate).

Important: In a normal process, we send the TBS data as a CSR (Certificate Signing Request) to an external CA. That CA then signs the block with its private key. In a "self-signed" setup, we use our own key instead.

## Analyzing an Existing Private Key

```shell
$ echo <PRIVATE_KEY> | base64 -D > key.der
$ openssl pkey -inform DER -in key.der -text -noout
```

### Output

```text
Private-Key: (1024 bit, 2 primes)
modulus: (...)
publicExponent: 65537 (0x10001)
privateExponent: (...)
prime1: (...)
prime2: (...)
exponent1: (...)
exponent2: (...)
coefficient: (...)
```

This structure exactly matches the RSA Private Key syntax. Since we see fields like modulus, publicExponent, and the various primes, we can confidently conclude that it’s an RSA key.  
For reference, see [RFC8017](https://www.rfc-editor.org/rfc/rfc8017#appendix-A.1.2).

## 🏁 Summary: The 5 Steps to a Certificate

1. Choose the ASN.1 structure: Define what should appear in the certificate (RFC 5280).
2. Fill the TBS data: Add identity and public key information.
3. DER encoding: Convert the TBS data into a unique binary format.
4. Sign: Hash and encrypt the binary TBS block (signature).
5. Wrap: Encode everything in Base64 and surround it with `-----BEGIN CERTIFICATE-----`.

## Helpful References

- RFC 5280: The “constitution” for X.509 certificates.
- RFC 2986: Specification for CSRs (PKCS#10).
- X.690: The standard for DER encoding.
- (https://darutk.medium.com/illustrated-x-509-certificate-84aece2c5c2e) (Highly recommended for visual learners).

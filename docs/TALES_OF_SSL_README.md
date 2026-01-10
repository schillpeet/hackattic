# Tales of SSL

### read it

- https://darutk.medium.com/illustrated-x-509-certificate-84aece2c5c2e

## X.509 Certificate (RFC 5280)

ASN.1 = abstract structure
⇒
DER = Distinguished Encoding Rules (embodied byte sequence)
⇒
Base64 = format conversion
⇒
PEM (decoration)

ASN.1 (abstract) converts certificate (RFC 5280) to a 
DER binary data in X.690 (looks like HEX) which is also
converted to Base64 (RFC 4648) text data.

If we add at the top and end of this Base64 text the following
lines:
```text
-----BEGIN CERTIFICATE-----
-----END CERTIFICATE-----
```

---

In the challenges is the question 'what's PEM? Is this a PEM? 
Wait, is it a CRT? With ASN? And where's my CSR?'

But we have just the requirement 'the requested certificate 
in base64 encoded DER format'.

⇒ We need ASN as basic format.
⇒ I think, we dont need PEM

### CSR (Certificate Signing Request) – The Order

It contains:
- information about us: domain name, organization, country
- out public key
- generated private key

### CRT (Certificate) - The Certificate

It's the finished Certificate. It contains:
- our public key
- our data
- digital signature

---

What have we to do?

We have to create a self-signed certificate. That means,
first of all, we create a CSR (and haven't send it to a 
CA - Certificate Authority), because we are self the CA!
Then self-signe this CSR with the private key (our signature)
and we have the CRT!

public key + personal data = CSR
⇒
CSR + private Key = CRT (Signed CSR)

---

Steps:

1. create a CertificationRequestInfo (https://datatracker.ietf.org/doc/html/rfc2986#section-3)
   2. A CertificationRequestInfo value containing 
      3. a subject distinguished name, 
      4. a subject public key, 
      5. and optionally a set of attributes is constructed by an entity requesting
         certification.
6. sign the CertificationRequestInfo with the subject entity's private key.
7. Then add a signature algorithm identifier

---

### Determinate kind of private key

```shell
$ echo "MIICXQIBAAKBgQCsNehz1yyou+PCaODGcL67Ox3NJuK7AxArOaQ5P/Zh6csjvNyh2vLXhuhN/5Lx35eQSWT5jL6aPNrP9KBTFb+YRiwjlFenuhsP0nGcPTtsFGjaRpRNRU4xrHyoU3IlP7gY2uKCHfCJgZTxeV6p5BilvJTzGd135w62rIvtbOSDgwIDAQABAoGAa+U2p+WH6IwX7kVRl2MqTRqD2HZllfAcYEi0GN53Wu9lRBXfUlVg0yKGR+A5y+tgBZnGdwf0n6RDIAnPrV6x9AjYll/odPPqhnzG7QzzfmUzzgLVjmusVZGkqmfvh+Fvu5IjPz9DB/HibxWc42tIGEMLTZDEUMfx+qa7n98fjhECQQDlYvp6aLHTzhhcZowcYnDS9dYCfHVRlzkP4RKFCChPgBZIhu5vXjTuNPTzIMTqbgeVSx/j3bd/0WxXOmTSsEfJAkEAwDC9w0xRlwY+qJ2tdCHv2SLxq5v9uecOq4T/L7sO21wyHibSxFW/Sb91G6YGZEziDc3O68uVQg6+wq9BRgiu6wJBAI0UMf9lMrGU2PDDdTrj5IYrAoOm7jTPMB4vDEfbe4dhvNLAghbmtuEmmtyJc/LG100f1i48J+ap89s2I9pc5tkCQQCsv2LiF0hDAkbx0oClIRfwSVuGT7kZDxl9jBa/tVheTZlyxpyuAxDkXeYKSwn1v7F0jOgPw7bOYGiQn2yBYa6vAkBM7Xqwa5QLvuzsiSt2njO/zhCVB9YkkVBjFt101L+ySHeCsrK8t8I65rZYAuLWRVY/8ahF7F/lrSRxr7QdVg0C" \
| base64 -D > key.der
$ openssl pkey -inform DER -in key.der -text -noout
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

Take a look at (https://www.rfc-editor.org/rfc/rfc8017#appendix-A.1.2).
What we see is a RSA Private Key Syntax.

# Jotting JWTs

First of all, you have to start the tunnel:

```shell
$ cloudflared tunnel run --url http://localhost:8080 hackattic-tunnel
```

After that, you can start the program.

## JWT Structure

A JWT is a token with the following structure:

```shell
token = "<Header>.<Payload>.<Signature>"
```

### Header

alg: Algorithm of encryption (here: HMAC with SHA256).

```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

### Payload

There are seven registered claim names from JWT specification (e.g. iat) and/or custom claim names (e.g. loggedInAs).

```json
{
  "loggedInAs": "admin",
  "iat": 1422779638
}
```

### Signature

the selected algorithm from header encrypt a `secret`, the base64url encoded `header` and the
base64url encoded payload.

```json
HMAC_SHA256(
  secret,
  base64urlEncoding(header) + '.' +
  base64urlEncoding(payload)
)
```

### Use

A client-agent sends the token in the `Authorization` header with a request. The kind of
the authorization is also marked. With `Bearer` you say, that you will use a token-based
authorization.

```http request
Authorization: Bearer <token>
```

#### Example

```kotlin
post("/") {
    val authHeader = call.request.header(HttpHeaders.Authorization)
    val jwt = authHeader?.removePrefix("Bearer ")?.trim()
}
```
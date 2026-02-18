# HackAttic

Take a look at: https://hackattic.com/

## Setup

Basic setup:

```shell
$ echo -e HACKATTIC_TOKEN=<YOUR_HACKATTIC_TOKEN> > .env
```

If you need your own server...

...for tunneling:

```shell
$ echo OWN_APP_URL=<OWN_APP_URL> >> .env
$ echo HOST=<OWN_HOST> >> .env
```

...for VPS server:

```shell
$ echo VPS_HOST=<VPS_HOST> >> .env
$ echo MY_IP=<MY_IP> >> .env
```

_INFO: I implemented `dotenv()` to get my token but there are other
options to do this e.g. with gradle.properties - maybe next time._

## Start

Tasks are started via the HackatticDispatcher from main.

There are two kinds of tasks:

- Challenges (Hackattic challenges, require your access token → see setup)
- Secrets (local secret tasks, no token required)

You select the task by passing the corresponding enum value:

- `Challenge.<Name>` refers to a Hackattic challenge (CamelCase)
- `Secret.<Name>` refers to a local secret task
- `playground = true` enables Hackattic playground mode (optional, default is false)

```kotlin
fun main() {
    // Run a Hackattic challenge
    HackatticDispatcher().run(Challenge.TalesOfSSL, playground = true)

    // Run a secret task
    HackatticDispatcher().run(Secret.Secret01)
}

```

## Solved Challenges

I have written down my thoughts/insights on the challenges in the docs about the challenges.

- Help me unpack ✅
- Tales of SSL ✅
- Brute force ZIP ✅
- A global presence ✅
- Visual basic math ✅
- Backup restore ✅
- Mini miner ✅
- WebSocket chit chat ✅
- Reading QR ✅
- Touch-Tone dialing ✅
- Collision course ✅
- Basic face detection ✅
- Password hashing ✅

The following challenges require running your own reachable server. 
See `docs/TUNNELING_GENERAL_SERVER_SETUP.md` for details on how to setup
your own Cloudflare Tunnel:

- Jotting JWTs ✅
- Dockerized solutions ✅

For the following task, using a Cloudflare Tunnel was not feasible.
It requires a stable public IP address and SSH port forwarding,
which are not reliably supported with Cloudflare Tunnels. See 
`HOSTING_GIT.md` for additional notes.

- Hosting Git ✅
- Serving Dns ✅

## Solved Secrets (all 😎)

- Secret #0: ✅
- Secret #1: ✅

## Solved Kata (all 😎)

- java solutions are located in: `src/main/java/kata`
- all other solutions are located in: `kata-others/*`

| Name                     | Java | Python | C++ | Haskell | JavaScript | Ruby | Perl | Elixir | Rust | Go | PHP |
|--------------------------|------|--------|-----|---------|------------|------|------|--------|------|----|-----|
| A Case Of Snake          | ✅    | ✅      | ✅   | ✅       | ✅          | ✅    | ✅    | ✅      | ✅    | ✅  | ✅   |
| Almost Binary            | ✅    | ✅      | ✅   | ✅       | ✅          | ✅    | ✅    | ✅      | ✅    | ✅  | ✅   |
| Debasing64               | ✅    | ✅      | ✅   | ✅       | ✅          | ✅    | ✅    | ✅      | ✅    | ✅  | ✅   |
| It Is Almost Compression | ✅    | ✅      | ✅   | ✅       | ✅          | ✅    | ✅    | ✅      | ✅    | ✅  | ✅   |
| Open Parens              | ✅    | ✅      | ✅   | ✅       | ✅          | ✅    | ✅    | ✅      | ✅    | ✅  | ✅   |
| Sorting JSON Lines       | ✅    | ✅      | ✅   | ✅       | ✅          | ✅    | ✅    | ✅      | ✅    | ✅  | ✅   |
| The Sum Of Things        | ✅    | ✅      | ✅   | ✅       | ✅          | ✅    | ✅    | ✅      | ✅    | ✅  | ✅   |
| What Day Was It          | ✅    | ✅      | ✅   | ✅       | ✅          | ✅    | ✅    | ✅      | ✅    | ✅  | ✅   |
| Yes It Fizz              | ✅    | ✅      | ✅   | ✅       | ✅          | ✅    | ✅    | ✅      | ✅    | ✅  | ✅   |

## Insights

- Certificate generation is awesome. (Alternatively: Creating certificates is a great way to track progress.)
- Ruby is a powerful and elegant language – definitely a keeper!
- Perl, like Ruby, is a very concise and beautiful language, but you absolutely have to delve deeper into regular expressions – cool language!
- Base64 refers to the 64 distinct characters used for encoding; there are many variations like Base62 (often used for URL shortness) or even Base58 (used in Bitcoin).
- Haskell: realized once again how beautiful FP is!
- Traditional PKZIP (zip -e) cracks damn fast (<0.1s) with John the Ripper ✌️ – use 7z+AES256, tar+GPG, or age (perfect for scripts & automation) instead.
- and many other things 😄
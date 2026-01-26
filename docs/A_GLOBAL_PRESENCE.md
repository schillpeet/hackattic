# A Global Presence (HackAttic Challenge)

How do you communicate with the same server from 7 different countries within 30 seconds?
My solution: 7 parallel Tor instances, each acting as a dedicated SOCKS proxy with a fixed exit country.

## The Problem with the JDK HTTP Client

For previous challenges, I used the native `java.net.http.HttpClient` (JDK 17).
Unfortunately, this client does not reliably support SOCKS v5 proxies [RFC1928](https://www.rfc-editor.org/rfc/rfc1928)  (which Tor relies on). This is a well-known 
limitation and frequently discussed on StackOverflow and in OpenJDK issue trackers [stackoveflow_java_http_client_socks_issue](https://stackoverflow.com/questions/70011046/how-to-use-a-socks-proxy-with-java-jdk-httpclient).

Instead of reinventing the wheel, I use `OkHttp` [OkHttpClient_supports_SOCKS_proxy](https://square.github.io/okhttp/changelogs/changelog_2x/#version-220), 
which has had stable SOCKS proxy support (since 2014 😄 with v2.2.0).

In the spirit of Donald E. Knuth [bio](https://www-cs-faculty.stanford.edu/~knuth/):

> “Premature optimization (abstraction) is the root of all evil.”

I intentionally keep both HTTP clients side by side for now, rather than forcing an overly complex abstraction layer too early.

## Tor Configuration: Why 7 Separate Instances?

A single Tor process (`torrc`) behaves unpredictably when trying to enforce multiple exit countries via different SOCKS ports.
To guarantee strict isolation, correctness, and speed, I run 7 completely independent Tor instances.

Each instance has its own configuration with the following key parameters:

- `ExitNodes {cc}`:
Specifies the country (ISO country code) through which traffic should exit the Tor network.
- `StrictNodes 1`:
Forces Tor to only use the specified exit country. Without this, Tor may silently fall back to another country if the 
- preferred one is unavailable — which would break the challenge.
- `DataDirectory`:
Each Tor instance needs its own directory for keys and cache to avoid file conflicts.

## The Bootstrapping Process

When Tor starts, it builds an encrypted circuit of three relays.
This process is called bootstrapping.

Why “Bootstrapped 100%” matters

If requests are sent before Tor reaches 100% bootstrapping:

- connections may fail, or
- Tor may use fallback routes that do not respect the configured exit country.

Since the challenge has a 30-second time limit, all 7 Tor tunnels must be fully established before the first request is sent.

# Setup & Execution

## 1. Prepare Tor nodes

Install `tor` for the script:

```shell
$ brew install tor
```

Start the script and wait until all Tor instances are fully bootstrapped:

```shell
$ scripts/start_tor_nodes.sh
Starting Tor instances...
Waiting for 'Bootstrapped 100%' for all 7 instances...
Status: 2/7 instances ready.
...
Status: 7/7 instances ready.
ALL INSTANCES READY! You can now start your Kotlin program.
```

### Hint
If a connection cannot be established, find out the country in a separate terminal:

```shell
$ grep -L "Bootstrapped 100%" ./tor-challenge/data/data_*/tor.log
``` 

Replace this country with any other in the following files:
- scripts/hackattic-global-presence.sh for the variable COUNTRIES
- HackatticDispatcher:
  - enum class
  - function: getTorOkHttpClient
  - function: runChallenge

Note: I know that if you want to make changes, especially in many places, you should refactor the code!

## 2. Solve the challenge

Now start the Kotlin program.
Since all proxies are already “warm”, requests are dispatched instantly across 7 different countries.

## 3. Cleanup

After completion, stop all background Tor processes:

```shell
$ pkill tor
# If necessary:
$ sudo pkill -9 tor
```
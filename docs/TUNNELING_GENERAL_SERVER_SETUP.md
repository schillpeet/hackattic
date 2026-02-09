# Tunneling - General Server Setup

This is a way to make sure your local server is publicly reachable without exposing ports directly.

I use Cloudflare as a reverse proxy with my own domain. For challenges that require a publicly 
reachable server, I expose my local application using a Cloudflare Tunnel.

## Prerequisites

- A Cloudflare account 
- A domain managed by Cloudflare 
- A local server running on localhost:8080

## One-time setup

```shell
# Install cloudflared
$ brew install cloudflared

# Authenticate with Cloudflare (opens a browser window)
$ cloudflared tunnel login

# Create a tunnel (the name is arbitrary)
$ cloudflared tunnel create hackattic-tunnel

# Map a subdomain to the tunnel
$ cloudflared tunnel route dns hackattic-tunnel challenge.<my-domain>
```

## Running the tunnel

Once the tunnel is created, you can start and stop it as needed.

```shell
# Start the tunnel and forward traffic to the local server
cloudflared tunnel run --url http://localhost:8080 hackattic-tunnel
```

Stopping the tunnel with CTRL+C only terminates the running process. The tunnel itself remains 
registered in Cloudflare and can be reused at any time.

After this, requests to `https://challenge.<my-domain>`will be forwarded to my local server 
running on localhost:8080.

## Testing

1. Start the tunnel
2. Start the application
3. Send a test request through the tunnel

```shell
$ curl -X POST https://challenge.<my-domain>/ \
  -H "Content-Type: text/plain" \
  -d "hello through the tunnel"
OK%
```

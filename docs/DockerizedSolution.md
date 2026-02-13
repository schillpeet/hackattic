# Dockerized Solution

If you open the tunnel, you have to change the port to the host port, not the container port.^^

```shell
$ cloudflared tunnel run --url http://localhost:5001 hackattic-tunnel
```

## General

Create a simple dockerfile:

```dockerfile
FROM alpine:latest

CMD ["echo", "Hello World!"]
```

Build and run:

```shell
§ docker build -t hello-world-alpine .
$ docker run hello-world-alpine
Hello World!
```

Create your own registry and push hello-world-alpine image:

```shell
$ docker run -d -p 5001:5000 --name registry registry:2
$ docker tag hello-world-alpine localhost:5001/hello-world-alpine:latest
$ docker push localhost:5001/hello-world-alpine:latest
The push refers to repository [localhost:5001/hello-world-alpine]
d8ad8cd72600: Pushed
46d365cea6e9: Pushed
latest: digest: sha256:030***
```

Pull and run:

```shell
$ docker pull localhost:5001/hello-world-alpine:latest
$ docker run localhost:5001/hello-world-alpine:latest
```

---

## Docker registry with authentification

```shell
# 1. Create directories:
#    - registry-auth: will contain the htpasswd file for authentication
#    - registry-data: will store the images persistently
$ mkdir -p registry-auth registry-data

# 2. Create the htpasswd file with user 'admin'
# -B → use bcrypt (secure)
# -b → for non-interactive
# -c → create a new file
# You will be prompted for a password
$ htpasswd -bBc registry-auth/htpasswd admin supersecret


$ docker run -d \
  -p 5001:5000 \
  --name registry \
  -v $(pwd)/registry-auth:/auth \                       # Mount host auth folder into container
  -v $(pwd)/registry-data:/var/lib/registry \           # Store images persistently
  -e "REGISTRY_AUTH=htpasswd" \                         # Enable authentication
  -e "REGISTRY_AUTH_HTPASSWD_REALM=Registry Realm" \    # Realm for auth
  -e "REGISTRY_AUTH_HTPASSWD_PATH=/auth/htpasswd" \     # Path to htpasswd file inside container
  registry:2

# Login to the registry
$ docker login localhost:5001

$ docker push localhost:5001/hello-world-alpine:latest
The push refers to repository [localhost:5001/hello-world-alpine]
46d365cea6e9: Pushed
d8ad8cd72600: Pushed
latest: digest: sha256:030f40... size: 855
```

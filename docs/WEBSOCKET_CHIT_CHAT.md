# WebSocket chit hat

Example:

```shell
$ curl "https://hackattic.com/challenges/websocket_chit_chat/problem?access_token=<TOKEN>"
{"token": "<TOKEN2>"}%

$ websocat wss://hackattic.com/_/ws/<TOKEN2>
hello! start counting the time between the pings (start from the moment you opened this connection)
ping!
ouch! seems like you missed a beat... sorry, this conversation is over. closing socket.
```

The server sends messages approximately at the following intervals (in milliseconds):

- 700
- 1500
- 2000
- 2500
- 3000

The server may send one of the following messages:

- `hello! ...`
- `ping!`
- `good!`
- `ouch! ...`
- `secret: ???` (special/hidden response)

To handle slight variations in message timing, define a tolerance window around each expected interval.
We suggest half of the distance to the next interval as tolerance.

| Interval (ms) | Tolerance Window (ms) | Notes                                        |
| ------------- | --------------------- |----------------------------------------------|
| 700           | 0 → 1100              | From connection or after `ping!`             |
| 1500          | 1100 → 1750           | Medium-short interval                        |
| 2000          | 1750 → 2250           | Medium interval                              |
| 2500          | 2250 → 2750           | Medium-long interval                         |
| 3000          | 2750 → ∞              | Longest interval, until next message arrives |

### Testing

I started locally a websocket server with `websocat`:

```shell
$ docker run --rm -p 8080:8080 solsson/websocat ws-l:0.0.0.0:8080 \
  sh-c:'trap "exit 0" INT TERM; while true; do echo "ping!"; sleep 0.7; done'
```


### it's funny

I didn't quite understand the task. I thought the token you get from the problem endpoint was valid 
for one hour and that's why I had to store it temporarily, but:

"Hit the problem endpoint, grab a one-time token. The token expires in 60 minutes, so take your time." 
- means that once I've used the token, it still becomes invalid 😄

...at least now I have a TokenProvider, in case I need it again^^
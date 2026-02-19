# Trivial Filing

That was a tough challenge. I tried so many things and ended up with a race condition. 
Now I can't say for sure whether the challenge expected ephemeral ports 
(which would actually be RFC TFTP), because I ended up doing everything over a single 
socket (i.e., over one port). I also briefly used a Docker image 'tftp-hpa' because it 
seemed the easiest solution, but without success – was that also a race condition? I 
didn't think so... In the end, though, I learned a lot again. 😂

## Insights

Structure of TFTP RRQ (Read Request):

```text
2 Bytes   Opcode (1 = RRQ)
n Bytes   Filename
1 Byte    0x00
n Bytes   Mode (netascii / octet)
1 Byte    0x00
```

for example: `RRQ "file" netascii`

```text
00 01            # Opcode = 1 (RRQ)
66 69 6C 65      # "file"
00               # Nullterminator
6E 65 74 61 73 63 69 69  # "netascii"
00               # Nullterminator
```

## Utils

Listen on udp inputs:

```shell
$ apk add tcpdump
$ tcpdump -i eth0 udp
```

## Testing on macOS (M1)

You can run your own TFTP server on macOS. The server’s home is `/private/tftproot`.
Any files you put there, like foobar.txt, can be read or updated by a client—but clients cannot create new files.

```shell
# from session a
$ sudo launchctl load -F /System/Library/LaunchDaemons/tftp.plist
$ cd /private/tftproot
$ sudo touch foobar.txt
$ sudo chmod 666 foobar.txt
$ echo "hello from tftp server" >> foobar.txt

# from session b
$ cd ~
$ ls -a
.          ..
$ tftp 127.0.0.1
tftp> get foobar.txt
Received 23 bytes during 0.0 seconds in 1 blocks
tftp> quit
$ ls -a
.          ..          foobar.txt
$ cat foobar.txt
hello from tftp server
# Modify file locally and send back
$ echo "\nhello from client" >> foobar.txt
$tftp 127.0.0.1
tftp> put foobar.txt
Sent 42 bytes during 0.0 seconds in 1 blocks

# session a
$ cat foobar.txt
hello from tftp server

hello from client
```

You can also watch the TFTP traffic – this is pretty kool:

```shell
$ sudo tcpdump -i any udp port 69
tcpdump: data link type PKTAP
tcpdump: verbose output suppressed, use -v[v]... for full protocol decode
listening on any, link-type PKTAP (Apple DLT_PKTAP), snapshot length 524288 bytes
17:17:33.614924 IP localhost.54906 > localhost.tftp: TFTP, length 34, RRQ "foobar" octet tsize 0 rollover 0
17:17:33.614957 IP localhost.54906 > localhost.tftp: TFTP, length 34, RRQ "foobar" octet tsize 0 rollover 0
17:18:03.096205 IP localhost.50682 > localhost.tftp: TFTP, length 35, WRQ "foobar" octet tsize 36 rollover 0
17:18:03.096242 IP localhost.50682 > localhost.tftp: TFTP, length 35, WRQ "foobar" octet tsize 36 rollover 0
```

Here you can see the `RRQ` (read request) and `WRQ` (write request) packets in action – this is how TFTP actually talks.
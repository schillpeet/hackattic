# Serving DNS

- official DNS port: `53`
- we need UDP (standard DNS protocol; fast, 'cause no handshake) → UDP-Server

## DNS Message Structure

```shell
DNS Message
├── Header (12 bytes)
├── Question Section (QDCOUNT entries)
├── Answer Section (ANCOUNT RRs)
├── Authority Section (NSCOUNT RRs)
└── Additional Section (ARCOUNT RRs)
```
## Structure of DNS Header

```shell
0-1   ID        (16 bit)
2-3   FLAGS     (16 bit)
4-5   QDCOUNT   (16 bit)
6-7   ANCOUNT   (16 bit)
8-9   NSCOUNT   (16 bit)
10-11 ARCOUNT   (16 bit)
```

## Flags (16 Bit Field)

```shell
| QR | OPCODE (4) | AA | TC | RD |
| RA | Z | AD | CD | RCODE (4) |
```

## Resource record (RR)

1. `NAME`: fully qualified domain name of the node in the tree
2. `TYPE`: indicates the format of the data and it gives a hint of its intended us
3. `CLASS`: independent name space with potentially different delegations of DNS zones, e.g. (IN for Internet)
4. `TTL`: time to live - Count of seconds that the RR stays valid
5. `RDLENGTH`: how much bytes has RDATA
6. `RDATA`: such as the IP address for address records, or the priority and hostname for MX records

Class `RP`: Responsible Person
    - Information about the responsible person(s) for the domain. Usually an email address with the @ replaced by a .

---

Use `nc` (netcat) or `dig` (domain information groper) to get information from DNS, 'cause it
can handle UDP requests/responses, e.g.:

```shell
$ dig @8.8.8.8 -p 53 example.com A
;; QUESTION SECTION:
;example.com.			IN	A

;; ANSWER SECTION:
example.com.		300	IN	A	104.18.26.120
example.com.		300	IN	A	104.18.27.120
```
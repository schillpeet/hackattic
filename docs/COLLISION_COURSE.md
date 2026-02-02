# Collision course

MD5 (`M`essage-`D`igest `A`lgorithm `5`) is a widely used cryptographic hash function developed 
in 1991 that converts arbitrary data into a fixed 128-bit hash value (usually 32 hexadecimal 
characters). Due to serious security vulnerabilities (collision susceptibility), it is obsolete 
for security applications such as password hashing or digital signatures.

There we come...

---

## Example to generate MD5 hash

```kotlin
val randomStr = "Quality is the opposite of subjectivity."
val md5 = MessageDigest.getInstance("MD5")
val hashBytes = md5.digest(randomStr.toByteArray())
val md5Hash = hashBytes.joinToString("") { String.format("%02x", it) }
// 3c298073b6506d3cbaf22ca2ee0f036d
```

### Script: fastcoll

#### Setup

```shell
$ git clone https://github.com/brimstone/fastcoll.git
$ cd fastcoll
$ brew install boost
$ clang++ -O3 -o fastcoll *.cpp \                                                                                                                                                  ⏎
    -I/opt/homebrew/include \
    -L/opt/homebrew/lib \
    -lboost_filesystem \
    -lboost_program_options \
    -DBOOST_TIMER_ENABLE_DEPRECATED
```

#### How to use it

```shell
$ echo "random_string" > prefix.txt
$ ./fastcoll -p prefix.txt -o out1.bin out2.bin                                                                                                                                    ✭
MD5 collision generator v1.5
by Marc Stevens (http://www.win.tue.nl/hashclash/)

Using output filenames: 'out1.bin' and 'out2.bin'
Using prefixfile: 'prefix.txt'
Using initial value: 35c6e9d4c2be9d70d85f2566b7011096

Generating first block: ...............................
Generating second block: S01...........
Running time: 7.30487 s

$ md5sum out1.bin out2.bin                                                                                                                                                         ✭
5cbdd3ff24902aeada16b29174533e91  out1.bin
5cbdd3ff24902aeada16b29174533e91  out2.bin
```

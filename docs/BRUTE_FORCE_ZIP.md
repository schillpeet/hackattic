# Brute Force Zip

## ZIP Documentation

[zip_docu_pkware](https://pkwaredownloads.blob.core.windows.net/pkware-general/Documentation/APPNOTE-6.3.9.TXT)

- 6.1 Traditional PKWARE Decryption (I guess we use this)

### 4.3.6 Overall .ZIP file format (extract)

```txt
      [local file header 1]
      [encryption header 1]
      [file data 1]
      [data descriptor 1]
      ...
```

### 4.3.7  Local file header (extract)

This has 30 Bytes:

```txt
local file header signature     4 bytes  (0x04034b50)
      version needed to extract       2 bytes
      general purpose bit flag        2 bytes
      compression method              2 bytes
      last mod file time              2 bytes
      last mod file date              2 bytes
      crc-32                          4 bytes
      compressed size                 4 bytes
      uncompressed size               4 bytes
      file name length                2 bytes
      extra field length              2 bytes

      file name (variable size)
      extra field (variable size)
```

We're interested in `crc-32 4 bytes`. If you zip a file and encoding it, you will see
for example:

```shell
$ zip -e secret.zip secret.txt
Enter password:
Verify password:
  adding: secret.txt (stored 0%)
$ zipinfo -v secret.zip
...
  file security status:                           encrypted
  extended local header:                          yes
  ...
  32-bit CRC value (hex):                         ef6c2b78
```

### Password Verification Optimization

If we take the highest byte of this CRC32 value `0xEF`, we have our checksum byte. When we
decrypt just the 12-byte encryption header with a password candidate, we can verify if the
**last byte** of the decrypted header matches `0xEF`. If it matches, we found a candidate.

The key insight: 1 byte has only 256 possible values (`0x00` to `0xFF`). This means we get
a **1/256 chance** of a false positive. This optimization saves us from decrypting and
decompressing the entire file for 255 out of 256 wrong passwords - we only need to decrypt
the 30-byte local header plus the 12-byte encryption header (42 bytes total).

**Note**: The traditional PKZIP encryption uses the CRC32's highest byte as the final byte
in the encryption header for password verification. This is stored in byte 11 of the
12-byte encryption header.


---

### John the Ripper
Brute-forcing with pure Kotlin/JVM using libraries like `zip4j` would be too slow, so we
use `john` (John the Ripper) as a subprocess wrapper.

```shell
$ brew install john-jumbo
``` 

```shell
# 1. Convert ZIP to John hash format
$ echo 'export PATH="$(brew --prefix john-jumbo)/share/john:$PATH"' >> ~/.zshrc && source ~/.zshrc
# check if ZIP is supported
$ john --list=formats | grep 'ZIP'
# transform your zip to hash
$ zip2john secret.zip > zip.hash
ver 1.0 efh 5455 efh 7875 secret.zip/secret.txt PKZIP Encr: 2b chk, TS_chk, cmplen=21, decmplen=9, crc=EF6C2B78

# 2. Crack the password
$ time john zip.hash --incremental=lower --min-length=4 --max-length=6
Using default input encoding: UTF-8
Loaded 1 password hash (PKZIP [32/64])
Press 'q' or Ctrl-C to abort, almost any other key for status
secret           (secret.zip/secret.txt)
1g 0:00:00:00 DONE (2026-01-23 20:16) 33.33g/s 34133p/s 34133c/s 34133C/s samila..alyala
Use the "--show" option to display all of the cracked passwords reliably
Session completed
0,13s user 0,02s system 84% cpu 0,180 total

# 3. Show cracked password
$ john --show zip.hash
secret.zip/secret.txt:secret:secret.txt:secret.zip::secret.zip
```

**Result**: Password is `secret`, cracked in **0.13 seconds**.

So, I will implement this shell logic in my code!

#### Implementation

This shell-based approach is wrapped in Kotlin code using `ProcessBuilder` to automate:
1. Converting ZIP to hash with `zip2john`
2. Running `john` with appropriate options
3. Extracting the found password with `john --show`
4. Unzipping the file with the cracked password
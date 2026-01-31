# Mini Miner

SHA256 (`S`ecure `H`ash `A`lgorithm) converts input data of arbitrary size
into a 256-bit (32-byte) hash value.

Example using the string `Hello World!`

```shell
$ echo -n "Hello, World\!"| sha256
dffd6021bb2bd5b0af676290809ec3a53191dd81c7f70a4b28688a362182986f
1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 
2   2   2   2   2   2   2   2   2   2   2   2   2   2   2   2   
4       4       4       4       4       4       4       4   
8               8               8               8       
16                              16               
32   
```

The first byte here is `d` with 8 bits, represented in binary a `01100100`.
The second byte is 'f' with 8 bits, represented as `01100110`.
And the third byte as `01100100` as well.

> val d = 'd'.code.toString(2).padStart(8, '0')

In the task, it says: `difficulty: how many bits (high order) need to be 0 in the SHA256 hash`.
So, for example, if we have a difficulty number of 18, we would need to consider the first two 
bytes completely and the first two bits of the third byte.

To illustrate the principle I want to implement:

```kotlin
val difficulty = 18
// takeBytes = dff
val takeBytes = data.take(ceil(difficulty / 8.0).toInt())
// takeBits = 011001000110011001
val takeBits = takeBytes.toByteArray().joinToString("") {
    it.toInt().and(0xFF).toString(2).padStart(8, '0')
}.take(difficulty)
// onlyZeros = false
val onlyZeros = takeBits.all { it == '0' }
```

Of course, this can be done faster if we skip all the casts 😄 and
check the first full bytes for zero directly. More details in the code.
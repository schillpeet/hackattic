package main

import (
	"bufio"
	"encoding/base64"
	"fmt"
	"os"
)

func debasing64() {
	s := bufio.NewScanner(os.Stdin)
	for s.Scan() {
		d, _ := base64.StdEncoding.DecodeString(s.Text())
		fmt.Printf("%s\n", d)
	}
}

/* func main() {
	debasing64()
} */

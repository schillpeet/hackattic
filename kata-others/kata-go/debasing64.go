package main

import (
	"bufio"
	"encoding/base64"
	"fmt"
	"os"
)

func main() {
	s := bufio.NewScanner(os.Stdin)
	for s.Scan() {
		d, _ := base64.StdEncoding.DecodeString(s.Text())
		fmt.Printf("%s\n", d)
	}
}

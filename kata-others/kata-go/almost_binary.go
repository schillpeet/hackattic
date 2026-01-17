package main

import (
	"bufio"
	"fmt"
	"os"
	"strconv"
	"strings"
)

func main() {
	s := bufio.NewScanner(os.Stdin)
	r := strings.NewReplacer("#", "1", ".", "0")
	for s.Scan() {
		v, _ := strconv.ParseInt(r.Replace(s.Text()), 2, 64)
		fmt.Println(v)
	}
}

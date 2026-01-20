package main

import (
	"bufio"
	"fmt"
	"os"
	"strconv"
	"strings"
)

func the_sum_of_things() {
	scanner := bufio.NewScanner(os.Stdin)
	for scanner.Scan() {
		input := scanner.Text()

		elements := strings.Fields(input)
		result := 0
		for _, val := range elements {
			if num, err := strconv.ParseInt(val, 0, 0); err == nil {
				result += int(num)
			} else {
				result += int(val[0])
			}
		}
		fmt.Println(result)
	}
}
func main() {
	the_sum_of_things()
}

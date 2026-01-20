package main

import (
	"fmt"
	"strconv"
	"strings"
)

func it_is_almost_compression() {
	var input string
	for {
		_, err := fmt.Scan(&input)
		if err != nil {
			break
		}
		count := 1
		runes := []rune(input)
		curValue := runes[0]
		var result strings.Builder

		for i := 1; i < len(runes); i++ {
			if curValue == runes[i] {
				count++
			} else {
				appendResult(&result, curValue, count)
				curValue = runes[i]
				count = 1
			}
		}
		appendResult(&result, curValue, count)
		fmt.Println(result.String())
	}
}

func appendResult(result *strings.Builder, curValue rune, count int) {
	if count <= 2 {
		result.WriteString(strings.Repeat(string(curValue), count))
	} else {
		result.WriteString(strconv.Itoa(count))
		result.WriteRune(curValue)
	}
}

/* func main() {
	it_is_almost_compression()
}
*/

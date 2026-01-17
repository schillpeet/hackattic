package main

import (
	"fmt"
	"strings"
	"unicode"
)

func a_case_of_snakes() {
	var word string
	for {
		_, err := fmt.Scan(&word)
		if err != nil {
			break
		}
		var result strings.Builder
		numUpper := 0
		for _, r := range word {
			if unicode.IsUpper(r) {
				numUpper++
				result.WriteRune('_')
				result.WriteRune(unicode.ToLower(r))
			} else {
				result.WriteRune(r)
			}
		}
		len_fir := strings.Index(result.String(), "_")
		if numUpper == 1 || len_fir > 3 {
			fmt.Println(result.String())
		} else {
			_, after, _ := strings.Cut(result.String(), "_")
			fmt.Println(after)
		}
	}
}

func main() {
	a_case_of_snakes()
}

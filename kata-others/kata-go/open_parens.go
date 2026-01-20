package main

import (
	"bufio"
	"fmt"
	"os"
)

func open_parens() {
	scanner := bufio.NewScanner(os.Stdin)
	for scanner.Scan() {
		input := scanner.Text()

		var stack []rune
		possible := true

		for _, val := range input {
			if val == '(' {
				stack = append(stack, val) // push
			} else if val == ')' {
				if len(stack) == 0 {
					possible = false
					break
				}
				stack = stack[:len(stack)-1] // pop
			}
		}
		if len(stack) == 0 && possible == true {
			fmt.Println("yes")
		} else {
			fmt.Println("no")
		}
	}
}

func main() {
	open_parens()
}

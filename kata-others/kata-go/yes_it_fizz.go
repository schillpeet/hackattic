package main

import (
	"fmt"
	"strconv"
)

func yes_it_fizz() {
	var start, end int
	for {
		_, err := fmt.Scan(&start, &end)
		if err != nil {
			break
		}

		for i := start; i <= end; i++ {
			result := ""
			if i%3 == 0 {
				result += "Fizz"
			}
			if i%5 == 0 {
				result += "Buzz"
			}
			if result == "" {
				result += strconv.Itoa(i)
			}
			fmt.Println(result)
		}
	}
}

/*
func main() {
	yes_it_fizz()
}
*/

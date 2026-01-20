package main

import (
	"fmt"
	"time"
)

func what_day_was_it() {
	var value int
	for {
		_, err := fmt.Scan(&value)
		if err != nil {
			break
		}
		ts := int64(86_400 * value)
		t := time.Unix(ts, 0)
		weekday := t.Weekday()
		fmt.Println(weekday)
	}
}

/* func main() {
	what_day_was_it()
} */

package main

import (
	"bufio"
	"encoding/json"
	"fmt"
	"os"
	"sort"
	"strings"
)

/**
{"Bentley.G":{"balance":2134,"account_no":233831255}}
{"Alton.K":{"balance":9315,"account_no":203123613,"extra":{"balance":131}}}
{"Bancroft.M":{"balance": 233,"account_no":287655771101},"extra":{"balance":98}}
*/

type Account struct {
	Name    string
	Balance float64 // json standard
}

func format_balance(n float64) string {
	s := fmt.Sprintf("%.0f", n)
	if len(s) <= 3 {
		return s
	}

	var result []string
	for len(s) > 3 {
		result = append([]string{s[len(s)-3:]}, result...)
		s = s[:len(s)-3]
	}
	result = append([]string{s}, result...)

	return strings.Join(result, ",")
}

func sorting_json_lines() {
	scanner := bufio.NewScanner(os.Stdin)
	var accounts []Account

	for scanner.Scan() {
		jsonString := scanner.Text()
		var jsonMap map[string]any

		json.Unmarshal([]byte(jsonString), &jsonMap)

		var extraBalance any
		if extra, ok := jsonMap["extra"].(map[string]any); ok {
			extraBalance = extra["balance"]
		}

		for name, data := range jsonMap {
			if name == "extra" { // dirty fix
				continue
			}
			details := data.(map[string]any)

			var finalBalance interface{}
			if personExtra, ok := details["extra"].(map[string]any); ok {
				finalBalance = personExtra["balance"]
			} else if extraBalance != nil {
				finalBalance = extraBalance
			} else {
				finalBalance = details["balance"]
			}
			balanceValue, _ := finalBalance.(float64)
			accounts = append(accounts, Account{Name: name, Balance: balanceValue})
		}
	}
	// sorting
	sort.Slice(accounts, func(i, j int) bool {
		return accounts[i].Balance < accounts[j].Balance
	})

	for _, acc := range accounts {
		formatted := format_balance(acc.Balance)
		fmt.Printf("%s: %s\n", acc.Name, formatted)
	}
}

func main() {
	sorting_json_lines()
}

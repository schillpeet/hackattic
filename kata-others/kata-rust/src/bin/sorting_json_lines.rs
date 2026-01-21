use std::{
    i64,
    io::{self, BufRead},
};

/*
{"Bentley.G":{"balance":2134,"account_no":233831255}}
{"Alton.K":{"balance":9315,"account_no":203123613,"extra":{"balance":131}}}
{"Bancroft.M":{"balance": 233,"account_no":287655771101},"extra":{"balance":98}}
*/

fn parse_balance(line: &str) -> Option<i64> {
    let balance_pos = line.find("\"balance\"")?;
    let after_balance = &line[balance_pos + "\"balance\"".len()..];
    let balance_num: String = after_balance
        .chars()
        .skip_while(|c| !c.is_ascii_digit() && *c != '-')
        .take_while(|c| c.is_ascii_digit() || *c == '-')
        .collect();
    let balance = balance_num.parse::<i64>().ok()?;
    Some(balance)
}

fn main() {
    let stdin = io::stdin();
    let mut entries: Vec<(String, i64)> = Vec::new();
    for line in stdin.lock().lines() {
        let line = line.unwrap();
        let mut chars = line.trim().chars().skip_while(|c| *c != '"').skip(1);
        let name: String = chars.by_ref().take_while(|c| *c != '"').collect();

        let Some(mut balance) = parse_balance(&line) else {
            return;
        };

        if line.contains("extra") {
            if let Some(first_balance_pos) = line.find("\"balance\"") {
                let substring_line = &line[first_balance_pos + "\"balance\"".len()..];

                let after_first_number = substring_line
                    .chars()
                    .skip_while(|c| !c.is_ascii_digit() && *c != '-')
                    .skip_while(|c| c.is_ascii_digit() || *c == '-')
                    .collect::<String>();

                if let Some(new_balance) = parse_balance(&after_first_number) {
                    balance = new_balance;
                }
            }
        }
        entries.push((name, balance));
    }
    entries.sort_by(|a, b| a.1.cmp(&b.1));
    for (name, balance) in entries {
        let formatted = |n: i64| -> String {
            let s = n.abs().to_string();
            let mut result = String::new();
            for (i, c) in s.chars().enumerate() {
                if i > 0 && (s.len() - i) % 3 == 0 {
                    result.push(',');
                }
                result.push(c);
            }
            if n < 0 {
                format!("-{}", result)
            } else {
                result
            }
        };
        println!("{name}: {}", formatted(balance));
    }
}

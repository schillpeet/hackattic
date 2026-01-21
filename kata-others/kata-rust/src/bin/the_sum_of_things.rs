use std::io::{self, BufRead};

fn main() {
    let stdin = io::stdin();
    for line in stdin.lock().lines() {
        let line = line.unwrap();
        let parts = line.split_whitespace();
        let mut result = 0;
        for p in parts {
            if p.starts_with("0b") {
                result += i32::from_str_radix(&p[2..], 2).unwrap_or(0);
            } else if p.starts_with("0o") {
                result += i32::from_str_radix(&p[2..], 8).unwrap_or(0);
            } else if p.starts_with("0x") {
                result += i32::from_str_radix(&p[2..], 16).unwrap_or(0);
            } else if let Ok(num) = p.parse::<i32>() {
                result += num;
            } else {
                result += p.as_bytes()[0] as i32;
            }
        }
        println!("end: {}", result);
    }
}

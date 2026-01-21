use std::io::{self, BufRead};

fn main() {
    let stdin = io::stdin();
    for line in stdin.lock().lines() {
        let line_str = line.unwrap();
        let mut parts = line_str.split_whitespace();
        let start: i32 = parts.next().unwrap().parse().unwrap();
        let end: i32 = parts.next().unwrap().parse().unwrap();

        for el in start..=end {
            if el % 3 == 0 && el % 5 == 0 {
                println!("FizzBuzz");
            } else if el % 3 == 0 {
                println!("Fizz");
            } else if el % 5 == 0 {
                println!("Buzz");
            } else {
                println!("{}", el)
            }
        }
    }
}

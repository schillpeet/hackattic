use std::io::{self, BufRead};

fn main() {
    let stdin = io::stdin();
    for line in stdin.lock().lines() {
        let input = line.unwrap();
        let mut stack: Vec<char> = Vec::new();
        for c in input.chars() {
            if c == '(' {
                stack.push(c);
            } else {
                if stack.is_empty() {
                    stack.push(c);
                    break;
                } else {
                    stack.pop();
                }
            }
        }
        if stack.is_empty() {
            println!("yes");
        } else {
            println!("no");
        }
    }
}

use std::io::{self, BufRead};

fn main() {
    let stdin = io::stdin();
    let weekdays = [
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday",
        "Saturday",
        "Sunday",
    ];
    // negative input as well
    for line in stdin.lock().lines() {
        let days_to_add: i64 = line.unwrap().trim().parse().unwrap_or(0);
        let result = ((days_to_add + 3) % 7 + 7) % 7;
        println!("{}", weekdays[result as usize]);
    }
}

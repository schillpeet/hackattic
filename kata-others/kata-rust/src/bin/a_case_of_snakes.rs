use std::io::{self, BufRead};

fn main() {
    let stdin = io::stdin();
    for line in stdin.lock().lines() {
        let line = line.unwrap();
        let count_upper_chars = line.chars().filter(|c| c.is_uppercase()).count();
        let line_lowercase_underscore: String = line
            .chars()
            .flat_map(|c| {
                if c.is_uppercase() {
                    vec!['_', c.to_lowercase().next().unwrap()]
                } else {
                    vec![c]
                }
            })
            .collect();
        let idx_first_underscore = line_lowercase_underscore.find(|c| c == '_').unwrap();
        if count_upper_chars > 1 && idx_first_underscore <= 3 {
            let cutted_result = &line_lowercase_underscore[(idx_first_underscore + 1)..];
            println!("{cutted_result}")
        } else {
            println!("{line_lowercase_underscore}");
        }
    }
}

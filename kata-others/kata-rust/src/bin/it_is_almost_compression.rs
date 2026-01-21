use std::io::{self, BufRead};

fn process_group(group: &Vec<char>, res: &mut String) {
    if group.len() > 2 {
        res.push_str(&group.len().to_string());
        res.push(group[0]);
    } else {
        res.extend(group.iter());
    }
}

fn main() {
    let stdin = io::stdin();
    for line in stdin.lock().lines() {
        let input: Vec<char> = line.unwrap().chars().collect();
        let mut cur_group = vec![input[0]];
        let mut fin_res = String::new();
        for i in 1..input.len() {
            if input[i] == input[i - 1] {
                cur_group.push(input[i]);
            } else {
                process_group(&cur_group, &mut fin_res);
                cur_group = vec![input[i]];
            }
        }
        process_group(&cur_group, &mut fin_res);
        println!("{}", fin_res)
    }
}

use std::io::{self, BufRead};

fn main() {
    for line in io::stdin().lock().lines() {
        let line = line.unwrap();

        let bin_str: String = line
            .chars()
            .map(|c| if c == '#' { '1' } else { '0' })
            .collect();
        let intval = isize::from_str_radix(&bin_str, 2).unwrap();
        println!("{}", &intval);
    }
}

use std::io::{self, BufRead};

fn manual_base64_decode(input: &str) -> Vec<u8> {
    let mut alphabet = [0u8; 256];
    let b64 = b"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    for (i, &c) in b64.iter().enumerate() {
        alphabet[c as usize] = i as u8;
    }

    let input = input.trim().as_bytes();
    let mut result = Vec::new();
    let mut chunks = input.chunks_exact(4);

    while let Some(chunk) = chunks.next() {
        let n = (alphabet[chunk[0] as usize] as u32) << 18
            | (alphabet[chunk[1] as usize] as u32) << 12
            | (alphabet[chunk[2] as usize] as u32) << 6
            | (alphabet[chunk[3] as usize] as u32);

        result.push((n >> 16) as u8);
        if chunk[2] != b'=' {
            result.push((n >> 8) as u8);
        }
        if chunk[3] != b'=' {
            result.push(n as u8);
        }
    }
    result
}

fn main() {
    let stdin = io::stdin();
    for line in stdin.lock().lines() {
        let line = line.unwrap();

        // Statt base64-Crate nutzen wir unsere eigene Funktion:
        let bytes = manual_base64_decode(&line);

        if let Ok(decoded_str) = String::from_utf8(bytes) {
            println!("{}", decoded_str);
        }
    }
}

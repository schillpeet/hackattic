use v5.10;
while (<>) {
    my ($start, $end) = split; # implicit argument and Awk-style split
    # string repetition operator and Boolean-to-Numeric Coercion
    say( ("Fizz" x !($_ % 3) . "Buzz" x !($_ % 5)) || $_ ) for $start .. $end;
}
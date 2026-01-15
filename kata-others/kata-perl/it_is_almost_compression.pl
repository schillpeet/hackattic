use v5.10;
while (<>) {
    chomp;
    say s/((.)\2{2,})/length($1) . $2/ger;
}
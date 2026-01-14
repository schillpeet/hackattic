use v5.10;
while (<>) {
    chomp;
    say s/^[^A-Z]{1,3}(?=[A-Z].*[A-Z])//r
        =~ s/([A-Z])/_\L$1/gr
        =~ s/^_//r;
}
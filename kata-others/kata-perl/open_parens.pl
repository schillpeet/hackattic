use v5.10;

while (<>) {
    chomp;
    my @stack;
    for (split //) {
        !(@stack && $stack[-1] eq "(" && $_ eq ")") ? push @stack, $_ : pop @stack;
    }
    say !@stack ? "yes" : "no";
}
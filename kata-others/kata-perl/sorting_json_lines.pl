use v5.10;

my %res;
while (<STDIN>) {
    if (/"([^"]+)"/) {
        my $name = $1;
        my @balances = /"balance"\s*:\s*(-?\d+)/g;
        $res{$name} = $balances[-1];
    }
}
for (sort { $res{$a} <=> $res{$b} } keys %res) {
    say "$_: " . "$res{$_}" =~ s/(?<=\d)(?=(\d{3})+(?!\d))/,/gr;
}
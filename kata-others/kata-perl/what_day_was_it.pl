use POSIX qw(strftime);
use v5.10;
say strftime("%A", gmtime($_ * 86_400)) while<>;
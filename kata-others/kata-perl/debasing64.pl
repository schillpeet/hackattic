use v5.10;
use MIME::Base64 qw(decode_base64);
say decode_base64($_) while <>;
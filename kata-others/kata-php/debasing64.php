<?php

$f = fopen( 'php://stdin', 'r' );

while( $line = fgets( $f ) ) {
    $str = 'VGhpcyBpcyBhbiBlbmNvZGVkIHN0cmluZw==';
    echo base64_decode($line) . "\n";
}

fclose( $f );

?>

<?php

$f = fopen( 'php://stdin', 'r' );

while( $line = fgets( $f ) ) {
    echo base64_decode($line) . "\n";
}

fclose( $f );

?>

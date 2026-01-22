<?php

$f = fopen( 'php://stdin', 'r' );

while( $line = fgets( $f ) ) {
    $bin = str_replace(array('#', '.'), array('1', '0'), $line);
    echo bindec($bin) . "\n";
}

fclose( $f );

?>

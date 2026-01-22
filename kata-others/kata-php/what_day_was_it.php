<?php

$f = fopen( 'php://stdin', 'r' );

while( $line = fgets( $f ) ) {
    $timestamp = strtotime('1970-01-01');
    $days = (int) trim($line);
    $timestamp = strtotime("+$days days", $timestamp);
    echo date('l', $timestamp), PHP_EOL;
}   

fclose( $f );

?>

<?php

$f = fopen( 'php://stdin', 'r' );

while( $line = fgets( $f ) ) {
    $line = str_split(trim($line));
    $stack = [];
    foreach ($line as $char) {
        if ($char == "(") array_push($stack, "(");
        else {
            if (count($stack) == 0) {
                array_push($stack, ")");
                break;
            } else array_pop($stack);
        }
    }
    if (count($stack) === 0) echo "yes" . "\n";
    else echo "no" . "\n";
}   

fclose( $f );

?>

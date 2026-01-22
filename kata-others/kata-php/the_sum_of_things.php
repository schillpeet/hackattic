<?php

$f = fopen( 'php://stdin', 'r' );

while( $line = fgets( $f ) ) {
    $line = trim($line);
    $split = explode(" ", $line);
    $result = 0;
    foreach ($split as $value) {
        if (is_numeric($value)) {
            $result += $value;
        } else if (strlen($value) == 1) {
            $result += ord($value);
        } else if ($value[1] === "b") {
            $result += bindec($value);
        } else if ($value[1] === "o") {
            $result += octdec($value);
        } else if ($value[1] === "x") {
            $result += hexdec($value);
        }
    }
    echo $result . "\n";
}   

fclose( $f );

?>

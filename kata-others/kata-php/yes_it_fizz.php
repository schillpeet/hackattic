<?php

$f = fopen( 'php://stdin', 'r' );

while( $line = fgets( $f ) ) {
    [$start, $end] = array_map('intval', explode(' ', trim($line)));
    $range = range($start, $end);
    foreach ($range as $value) {
        $out = '';
        if ($value % 3 == 0) $out .= "Fizz";
        if ($value % 5 == 0) $out .=  "Buzz";

        echo ($out !== '' ? $out : $value) . PHP_EOL;
    }
}

fclose( $f );

?>

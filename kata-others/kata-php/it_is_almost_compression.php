<?php

$f = fopen( 'php://stdin', 'r' );

while( $line = fgets( $f ) ) {
    preg_match_all('/(.)\1*/', $line, $matches);
    foreach ($matches[0] as $group) {
        if (strlen($group) > 2) {
            echo strlen($group) . $group[0];
        } else {
            echo $group;
        }
    }
    echo PHP_EOL;
}

fclose( $f );

?>

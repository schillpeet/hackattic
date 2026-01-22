<?php

$f = fopen( 'php://stdin', 'r' );

while( $line = fgets( $f ) ) {
    $line = trim($line);
    $count_uppercase = preg_match_all('/[A-Z]/', $line);

    $snake = preg_replace_callback('/[A-Z]/', function($match) {
        return '_' . strtolower($match[0]);
    }, $line);
    if ($count_uppercase == 1) {
        echo $snake, PHP_EOL;
    } else {
        $parts = preg_split('/_/', $snake);
        if (strlen($parts[0]) < 4) {
            $snake = substr($snake, strlen($parts[0]) + 1);
        }
        echo $snake, PHP_EOL;
    }
}   

fclose( $f );

?>

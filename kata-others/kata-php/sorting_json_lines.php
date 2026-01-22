<?php

$f = fopen( 'php://stdin', 'r' );
/*
{"Bentley.G":{"balance":2134,"account_no":233831255}}
{"Barclay.E":{"balance":1123,"account_no":312333321}}
{"Alton.K":{"balance":9315,"account_no":203123613,"extra":{"balance":131}}}
{"Bancroft.M":{"balance": 233,"account_no":287655771101,"extra":{"balance":98}}}
 */
$accounts = [];

while( $line = fgets( $f ) ) {
    $data = json_decode($line, true);
    $all_keys = array_keys($data);
    $name = $all_keys[0];
    $num_of_keys = count($all_keys);

    $balance = $data[$name]['extra']['balance'] ?? null;
    if ($balance === null) {
        $balance = $data[$name]['balance'] ?? null;
    }
    if ($num_of_keys == 2) {
        $balance = $data['extra']['balance'];
    }
    $accounts[$name] = (int)$balance;
}
asort($accounts);

foreach ($accounts as $name => $balance) {
    $formatted_balance = number_format($balance, 0, '.', ',');
    echo "$name: $formatted_balance" . "\n";
};

fclose( $f );

?>

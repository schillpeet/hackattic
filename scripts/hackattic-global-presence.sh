#!/bin/bash

# Country List
COUNTRIES=("dk" "pl" "se" "at" "ch" "fr" "nl")
BASE_DIR="./tor-challenge"

mkdir -p "$BASE_DIR/data"
mkdir -p "$BASE_DIR/configs"

echo "Starting Tor instances..."

for i in "${!COUNTRIES[@]}"; do
    COUNTRY=${COUNTRIES[$i]}
    PORT=$((9050 + i))
    CONF="$BASE_DIR/configs/$COUNTRY.conf"
    DATA="$BASE_DIR/data/data_$COUNTRY"

    mkdir -p "$DATA"

    # Create configuration on-the-fly if it doesn't exist
    echo -e "SocksPort $PORT\nExitNodes {$COUNTRY}\nStrictNodes 1\nDataDirectory $DATA\nLog notice file $DATA/tor.log" > "$CONF"

    # Start Tor in the background and redirect logs to the data directory
    tor -f "$CONF" > "$DATA/tor.log" 2>&1 &
done

echo "Waiting for 'Bootstrapped 100%' for all 7 instances..."

while true; do
    READY=$(grep -l "Bootstrapped 100%" $BASE_DIR/data/data_*/tor.log 2>/dev/null | wc -l)
    echo "Status: $READY/7 instances ready."

    if [ "$READY" -eq 7 ]; then
        echo "ALL INSTALLS READY! You can now start your Kotlin program."
        break
    fi
    sleep 2
done
#!/usr/bin/env bash
set -e

rm -f challenge_work/backup_restore/dump.sql.gz
rm -f challenge_work/backup_restore/dump.sql

printf '%s' "$1" | base64 -d > challenge_work/backup_restore/dump.sql.gz
gzip -k -d challenge_work/backup_restore/dump.sql.gz

dropdb --if-exists hackattic
createdb hackattic

psql -d hackattic < challenge_work/backup_restore/dump.sql > /dev/null 2>&1
psql -d hackattic -At -c "SELECT ssn FROM public.criminal_records WHERE status LIKE 'alive';"
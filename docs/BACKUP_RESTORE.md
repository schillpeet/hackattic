# Backup restore

## Findings
I really struggled to use an embedded PostgreSQL database here, which cost me a lot of 
time and frustration 😄. Then I tried to replicate what I had previously done in the 
shell using ProcessBuilder, until it occurred to me: why am I trying so hard to implement 
this in Kotlin? I admit, it's fun to search for and find solutions, but you don't have 
to overdo it. ^^

I could have just written a shell script from the start.

## cheat sheet

```shell
# check postgres is started
$ brew services list

# start interactive postgreSQL terminal
$ psql postgres

# show all DBs
postgres=# \l

# connect to special DB
postgres=# \c <DB>

# list all relations
postgres=# \dt

# create table
postgres=# CREATE TABLE robots (
id SERIAL PRIMARY KEY,
name TEXT NOT NULL,
mission TEXT UNIQUE
);

# add tuple
foobar=# INSERT INTO robots (name, mission) VALUES ('Mr. Robot', 'To liberate society by erasing global debt.');
```

***HINT:***
- a base64 encoded pg_dump dump (by the way, -Z9)

```shell
$ pg_dump --help
-Z, --compress=0-9           compression level for compressed formats

# create a dump
$ pg_dump <db_name> > <db_name>_dump_db.sql
$ file <db_name>_dump_db.sql
<db_name>_dump_db.sql: ASCII text

# create a compressed dump
$ pg_dump <db_name> -Z9 > <db_name>_comporessed_dump_db.sql
<db_name>_comporessed_dump_db.sql: gzip compressed data, max compression, original size modulo 2^32 3129
```

### What I did

I first tried to solve the task in the shell. That is, to fetch the base64 string once and then look 
at its contents:

```shell
$ echo <base64_string> | base64 -d > dump.sql
$  file dump.sql
dump.sql: gzip compressed data
$ mv dump.sql dump.sql.gz
$ gzip -k -d dump.sql.gz

# check if the DB has a name (there was no name)
$ head -n 50 dump.sql
$ createdb hackattic
$ psql -d hackattic < dump.sql
$ psql hackattic
hackattic=# \dt
              List of relations
 Schema |       Name       | Type  |  Owner
--------+------------------+-------+---------
 public | criminal_records | table | howami
(1 row)
hackattic=# select count(*) from criminal_records;
 count
-------
    86
(1 row)
hackattic=# select * from criminal_records limit 1;
 id |      name      |     felony     |     ssn     |        home_address        |        entry        |          city           | status
----+----------------+----------------+-------------+----------------------------+---------------------+-------------------------+--------
  1 | Rodney Goodwin | Animal cruelty | 579-29-0060 | 5990 Decker Neck Suite 006 | 1975-06-19 00:00:00 | Fieldsborough, LA 92849 | alive
(1 row)
hackattic=# select distinct status from criminal_records;
   status
------------
 missing
 alive
 terminated
(3 rows)
hackattic=# select ssn from criminal_records where status like 'alive';
     ssn
-------------
 579-29-0060
 141-85-4255
 622-57-2228
 267-81-0916
 057-91-9762
 189-33-3242
 761-87-0328
 259-93-7724
 209-43-3639
 213-99-6773
 442-06-6667
 047-48-9313
 515-63-6538
 697-88-1794
 683-68-9804
 502-25-2311
 260-59-9507
 876-19-0555
 068-50-8988
 782-03-2649
 638-30-2498
 407-13-5706
 530-03-8887
(23 rows)
```
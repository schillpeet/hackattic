# The redis one

Well, redis looks very kool.. ❤️

```shell
$ docker run -d --name my-redis -p 6379:6379 redis
$ strings dump.rdb | head                                                                                                                                                                        ⏎
mySQL0009
...

# ok, this header is not correct 😂 -> repair it => vim 💪 => change 'mySQL' to 'REDIS'

$ docker stop my-redis
$ docker cp ./dump.rdb my-redis:/data/dump.rdb
$ docker start my-redis
$ docker logs my-redis
...* Done loading RDB, keys loaded: 10, keys expired: 1.


$ docker exec -it my-redis redis-cli
# so, there are 5 DBs
127.0.0.1:6379> info keyspace
db0:keys=2,expires=0,avg_ttl=0,subexpiry=0
db2:keys=1,expires=0,avg_ttl=0,subexpiry=0
db3:keys=2,expires=0,avg_ttl=0,subexpiry=0
db10:keys=3,expires=0,avg_ttl=0,subexpiry=0
db12:keys=2,expires=0,avg_ttl=0,subexpiry=0

# check db 0:
127.0.0.1:6379> select 0
OK
127.0.0.1:6379> keys *
1) "odd_hill_count"
2) "cold_base"
# get type of key 'cold_base'
127.0.0.1:6379> TYPE cold_base
hash

# check db 2
127.0.0.1:6379[1]> select 2
OK
127.0.0.1:6379[2]> keys *
1) "blue_hall_hash"

#
127.0.0.1:6379[2]> select 3
OK
127.0.0.1:6379[3]> keys *
1) "purple_wood_count"
2) "curly_pond_hash"

# check db 10
127.0.0.1:6379[10]> keys *
1) "spring_union_hash"
2) "\xf0\x9f\x98\x9a"
3) "broken_hat_hash"

# "\xf0\x9f\x98\x9a" -> f0 9f 98 9a -> 😚 => so...
127.0.0.1:6379[10]> GET 😚
"fd191853bee1f03e01a7dbbbbae89b7510e5a1dbb533d4ee32d47a1f7c2eac0b857d825bd446f03f1fede9bd6de5e6d25b23906a3cd1267babae0798808fbf66"
# you can also type the hex
127.0.0.1:6379[10]> get "\xf0\x9f\x98\x9a"

# check db 12
127.0.0.1:6379[12]> keys *
1) "blue_scene_hash"
2) "cold_mode_hash"

# I was too slow to find the expiration date of a key, but the command would actually be the following.
# First, however, I will initialize a key with this property and demonstrate how time passes:
# https://redis.io/docs/latest/commands/expire/
127.0.0.1:6379[12]> expire blue_scene_hash 42
(integer) 1
127.0.0.1:6379[12]> pttl blue_scene_hash
(integer) 40707
127.0.0.1:6379[12]> pttl blue_scene_hash
(integer) 35542
127.0.0.1:6379[12]> pttl blue_scene_hash
(integer) 28787
# it doesnt exist anymore
127.0.0.1:6379[12]> pttl blue_scene_hash
(integer) -2
```

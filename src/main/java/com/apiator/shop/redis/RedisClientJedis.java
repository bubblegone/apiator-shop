package com.apiator.shop.redis;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

@Component
public class RedisClientJedis implements RedisClient{
    private final Jedis jedis;

    public RedisClientJedis() {
        jedis = new Jedis();
    }

    @Override
    public String get(String key) {
        return jedis.get(key);
    }

    @Override
    public void set(String key, String value, long expirationTime) {
        jedis.set(key, value, new SetParams().ex(expirationTime));
    }

    @Override
    public void del(String key) {
        jedis.del(key);
    }
}

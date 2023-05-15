package com.apiator.shop.redis;

public interface RedisClient {
    String get(String key);
    void set(String key, String value, long expirationTime);
    void del(String key);
}

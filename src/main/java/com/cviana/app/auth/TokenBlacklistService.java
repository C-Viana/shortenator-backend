package com.cviana.app.auth;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {
	private final RedisTemplate<String, String> redisTemplate;
	
	@Value("${api.security.token.expiry}")
    private long CACHE_TTL_MINUTES;

    public TokenBlacklistService(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void add(String token, String username) {
		redisTemplate.opsForValue().set(token, token, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
    }

    public boolean isBlacklisted(String token) {
        return redisTemplate.hasKey(token);
    }
}

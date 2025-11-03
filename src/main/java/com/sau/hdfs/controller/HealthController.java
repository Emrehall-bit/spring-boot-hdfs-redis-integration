package com.sau.hdfs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/health")
    public String health() {
        try {
            redisTemplate.opsForValue().set("test", "ok");
            String value = (String) redisTemplate.opsForValue().get("test");
            return "✅ App is healthy! Redis connected: " + (value != null);
        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        }
    }
}

package com.show.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author 916202420@qq.com
 * @date 2022/4/11 15:52
 */
@Configuration
public class RedisConfig {


    @Bean("redis")
    @Description("Redis设置序列化")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory rcf) {
        RedisTemplate<String, Object> rt = new RedisTemplate<>();
        rt.setConnectionFactory(rcf);
        Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        serializer.setObjectMapper(om);
        StringRedisSerializer srs = new StringRedisSerializer();
        // 设置开启默认序列化
        rt.setDefaultSerializer(serializer);
        rt.setEnableDefaultSerializer(true);
        // 设置序列化策略
        rt.setKeySerializer(srs);
        rt.setHashKeySerializer(srs);
        rt.setValueSerializer(serializer);
        rt.setHashValueSerializer(serializer);
        return rt;
    }
}
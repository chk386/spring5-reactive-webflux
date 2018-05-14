package com.nhnent.webfluxtest.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author haekyu.cho@nhnent.com
 * @since 2018-04-12
 */
@Configuration
public class CacheConfiguration {

    @Bean
    public ReactiveRedisTemplate reactiveRedisTemplate(final ReactiveRedisConnectionFactory factory) {
        RedisSerializer<String> stringRedisSerializer = new StringRedisSerializer();
        RedisSerializer<Object> jsonRedisSerializer = new GenericJackson2JsonRedisSerializer(new ObjectMapper());

        RedisSerializationContext<String, Object> context = RedisSerializationContext.<String, Object>newSerializationContext()
            .hashKey(stringRedisSerializer)
            .hashValue(jsonRedisSerializer)
            .key(stringRedisSerializer)
            .value(jsonRedisSerializer)
            .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }
}

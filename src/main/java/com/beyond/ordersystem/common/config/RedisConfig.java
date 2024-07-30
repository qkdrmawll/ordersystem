package com.beyond.ordersystem.common.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
//    application.yml의 정보를 소스코드의 변수로 가져오는 것
    @Value("${spring.redis.host}")
    public String host;
    @Value("${spring.redis.port}")
    public int port;

    @Bean
    @Qualifier("2") // 숫자를 1부 센다
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);
//        1번 db 사용
        configuration.setDatabase(1); // 숫자를 0부터 센다
        return new LettuceConnectionFactory(configuration);
    }
    @Bean
    @Qualifier("2")
    public RedisTemplate<String,Object> redisTemplate(@Qualifier("2") RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer()); //자바의 스트링을 레디스의 스트링과 맞추기 위해
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer()); // 레디스의 value와 json을 맞추기 위해서
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }
}

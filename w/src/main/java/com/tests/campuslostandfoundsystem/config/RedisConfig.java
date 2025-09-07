package com.tests.campuslostandfoundsystem.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
   public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
       RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
       redisTemplate.setKeySerializer(RedisSerializer.string());
      redisTemplate.setHashKeySerializer(RedisSerializer.string());

//      json序列化规则
       ObjectMapper om = new ObjectMapper();
       om.activateDefaultTyping(BasicPolymorphicTypeValidator.builder()
                       .allowIfSubType("*")
                       .build(),
               ObjectMapper.DefaultTyping.NON_FINAL);

      GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(om);
      redisTemplate.setValueSerializer(serializer);
      redisTemplate.setHashValueSerializer(serializer);

       redisTemplate.afterPropertiesSet();
       return redisTemplate;
   }

   @Bean
   public LettuceConnectionFactory redisConnectionFactory(RedisProperties redisProperties) {
       RedisStandaloneConfiguration  config = new RedisStandaloneConfiguration();
       config.setHostName(redisProperties.getHost());
       config.setPort(redisProperties.getPort());
       config.setPassword(redisProperties.getPassword());
       config.setDatabase(redisProperties.getDatabase());
       return new LettuceConnectionFactory(config);
   }
}

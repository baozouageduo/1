package com.tests.campuslostandfoundsystem.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonCodec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class RedissonConfig {
    @Value("${spring.data.redis.host}")
    String redisHost = "localhost";
    @Value("${spring.data.redis.port}")
    int redisPort = 6379;
    @Value("${spring.data.redis.password:}")
    String redisPassword;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + redisHost + ":" + redisPort);

        if(redisPassword!= null && !redisPassword.isEmpty()){
            config.useSingleServer().setPassword(redisPassword);
        }
        ObjectMapper om = new ObjectMapper();
        om.activateDefaultTyping(om
                        .getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL);
        config.setCodec(new JsonJacksonCodec(om));
        return Redisson.create(config);
    }
}

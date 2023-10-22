package com.cheng.bibackend.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "spring.redis")
@Configuration
public class RedissonConfig {
    //redis的几个参数
    private Integer database;
    private String host;
    private Integer port;
    private String password;
    //定义一个redis客户端
    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        //对接一下redis
        config.useSingleServer()
                .setDatabase(database)
                .setAddress("redis://"+host+":"+port)
        //密碼 沒密碼需要註釋掉
                .setPassword(password);
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}

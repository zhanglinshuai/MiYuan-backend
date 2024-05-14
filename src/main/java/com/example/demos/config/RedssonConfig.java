package com.example.demos.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author tbb
 *
 */
@Configuration
public class RedssonConfig
{
	    @Bean
	    public RedissonClient getRedisson()
	    {
	        Config config = new Config();
	        config.useSingleServer()
	                .setAddress("redis://192.168.6.52:6379")
	                .setRetryInterval(5000)
	                .setTimeout(10000)
	                .setConnectTimeout(10000);
	        return Redisson.create(config);
	    }

}

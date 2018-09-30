package com.njkol.redis.mongo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class MongoWithRedisSentinel {

	public static void main(String[] args) {
		SpringApplication.run(MongoWithRedisSentinel.class, args);
	}
}

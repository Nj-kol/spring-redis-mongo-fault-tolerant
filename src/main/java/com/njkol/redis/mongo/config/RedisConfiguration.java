package com.njkol.redis.mongo.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.DefaultLettucePool;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.njkol.redis.mongo.model.Review;

/**
 * Configuration for Redis that leverages Lettuce as a Redis client
 * 
 * @author Nilanjan Sarkar
 *
 */
@Configuration
public class RedisConfiguration extends CachingConfigurerSupport {

	@Value("${redis.cache.expiry.time}")
	private String cacheExpiryTime;

	@Value("${redis.cluster.name}")
	private String clusterName;

	@Value("${redis.sentinel.machines}")
	private String sentinalMachines;

	@Autowired
	private VendorReviewsSerializer vendRevSer;

	private KeyGenerator keyGen = new VendorReviewsKeyGenerator();

	static RedisSentinelConfiguration SENTINEL_CONFIG = new RedisSentinelConfiguration();

	@Bean
	public RedisSentinelConfiguration sentinelConfig() {

		RedisSentinelConfiguration conf = SENTINEL_CONFIG.master(clusterName);

		for (String sentinel : sentinalMachines.split(",")) {
			String[] hostPort = sentinel.split(":");
			conf = conf.sentinel(hostPort[0], Integer.parseInt(hostPort[1]));
		}

		SENTINEL_CONFIG = conf;
		
		return SENTINEL_CONFIG;
	}

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {

		DefaultLettucePool lettucePool = new DefaultLettucePool(sentinelConfig());
		lettucePool.afterPropertiesSet();
		LettuceConnectionFactory clientConfig = new LettuceConnectionFactory(sentinelConfig());
		clientConfig.afterPropertiesSet();
		return clientConfig;
	}

	@Bean
	public RedisTemplate<String, List<Review>> redisTemplate() {
		RedisTemplate<String, List<Review>> redisTemplate = new RedisTemplate<String, List<Review>>();
		redisTemplate.setConnectionFactory(redisConnectionFactory());
		redisTemplate.setExposeConnection(true);
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(vendRevSer);
		return redisTemplate;
	}

	@Bean
	public RedisCacheManager cacheManager() {

		RedisCacheManager redisCacheManager = new RedisCacheManager(redisTemplate());
		redisCacheManager.setTransactionAware(true);
		redisCacheManager.setDefaultExpiration(Long.parseLong(cacheExpiryTime));
		redisCacheManager.setLoadRemoteCachesOnStartup(false);
		redisCacheManager.setUsePrefix(true);
		return redisCacheManager;
	}

	@Bean("vendorReviewsKeyGenerator")
	public KeyGenerator keyGenerator() {
		return keyGen;
	}

}
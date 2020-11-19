package com.vinotest.rediscachedemo;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ResourceNotFoundException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl {

	@Autowired
	private UserRepository userRepository;
	
	private RedisTemplate<String, User> redisTemplate;
	
	
	public User findUserById(String id) {
        final String key = "post_" + id;
        final ValueOperations<String, User> operations = redisTemplate.opsForValue();
        final boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            final User post = operations.get(key);
            System.out.println("PostServiceImpl.findPostById() : cache post >> " + post.toString());
            return post;
        }
        
        final Optional<User> post = Optional.ofNullable(userRepository.findOne(id));
        if(post.isPresent()) {
            operations.set(key, post.get(), 10, TimeUnit.SECONDS);
            System.out.println("PostServiceImpl.findPostById() : cache insert >> " + post.get().toString());
            return post.get();
        } else {
            throw new ResourceNotFoundException("not found", null);
        }
    }
}

package com.example.tweet_service;

import com.netflix.discovery.EurekaNamespace;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TweetServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TweetServiceApplication.class, args);
	}

}

package com.example.tweet_service.repository;

import com.example.tweet_service.models.Retweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RetweetRepository extends JpaRepository<Retweet, UUID> {
    // Find retweets by original tweet ID
    long countByOriginalTweetId(UUID originalTweetId);

    // Custom method to delete retweets by original tweet ID
    void deleteByOriginalTweetId(UUID originalTweetId);

    boolean existsByOriginalTweetIdAndUserId(UUID originalTweetId, UUID userId);
}

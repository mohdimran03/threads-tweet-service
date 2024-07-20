package com.example.tweet_service.repository;

import com.example.tweet_service.models.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<Like, UUID> {
    // Find likes by tweet ID
    long countByTweetId(UUID tweetId);

    // Custom method to delete likes by tweet ID
    void deleteByTweetId(UUID tweetId);

    // Custom method to find a like by tweet ID and user ID
    boolean existsByTweetIdAndUserId(UUID tweetId, UUID userId);
}

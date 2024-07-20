package com.example.tweet_service.repository;

import com.example.tweet_service.models.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, UUID> {
    // Find replies by tweet ID
    List<Reply> findByTweetId(UUID tweetId);

    // Custom method to delete replies by tweet ID
    void deleteByTweetId(UUID tweetId);
}

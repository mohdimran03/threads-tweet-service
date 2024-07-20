package com.example.tweet_service.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Re_tweets")
public class Retweet {

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOriginalTweetId() {
        return originalTweetId;
    }

    public void setOriginalTweetId(UUID originalTweetId) {
        this.originalTweetId = originalTweetId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID originalTweetId;

    @Column(nullable = false)
    private UUID userId;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return "Retweet{" +
                "id=" + id +
                ", originalTweetId=" + originalTweetId +
                ", userId=" + userId +
                ", createdAt=" + createdAt +
                '}';
    }
}


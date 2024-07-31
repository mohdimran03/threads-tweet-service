package com.example.tweet_service.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Hashtags")
public class Hashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column
    private String tag;

    @Column(name = "tweetIds")
    private List<UUID> tweetIds = new ArrayList<>();

    @Column
    private LocalDateTime createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<UUID> getTweetIds() {
        return tweetIds;
    }

    public void setTweetIds(List<UUID> tweetIds) {
        this.tweetIds = tweetIds;
    }

    public void insertTweetId(UUID tweetId) {
        if (!tweetIds.contains(tweetId)) {
            tweetIds.add(tweetId);
        }
    }
}
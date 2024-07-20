package com.example.tweet_service.dtos;

import java.util.UUID;

public class TweetRequest {

    private UUID userId;

    private String content;

    // Getters and Setters
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

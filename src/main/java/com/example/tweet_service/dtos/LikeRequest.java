package com.example.tweet_service.dtos;

import java.util.UUID;

public class LikeRequest {

    private UUID userId;

    // Getters and Setters
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}


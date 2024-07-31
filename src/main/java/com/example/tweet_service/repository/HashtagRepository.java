package com.example.tweet_service.repository;

import com.example.tweet_service.models.Hashtag;
import com.example.tweet_service.models.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, UUID> {
    @Query(value = "SELECT h FROM Hashtag h WHERE h.tag = :tag")
    Optional<Hashtag> findByTag(String tag);

    @Query(value = "SELECT h.tag FROM Hashtag h WHERE h.createdAt >= :since")
    List<?> findTrendingHashtags(LocalDateTime since);
}

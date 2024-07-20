package com.example.tweet_service.repository;

import com.example.tweet_service.models.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, UUID> {
    @Query(value = "select t.* from tweets t where t.userid in (select l.userid from likes l where l.userid = :userId)", nativeQuery = true)
    List<Tweet> findTweetsLikedByUser(@Param("userId") UUID userId);
}

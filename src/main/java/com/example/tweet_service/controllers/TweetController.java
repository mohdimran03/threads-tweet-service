package com.example.tweet_service.controllers;

import com.example.tweet_service.dtos.LikeRequest;
import com.example.tweet_service.dtos.ReplyRequest;
import com.example.tweet_service.dtos.RetweetRequest;
import com.example.tweet_service.dtos.TweetRequest;
import com.example.tweet_service.models.Reply;
import com.example.tweet_service.models.Retweet;
import com.example.tweet_service.models.Tweet;
import com.example.tweet_service.services.TweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tweets")
public class TweetController {

    @Autowired
    private TweetService tweetService;

    // Create a new tweet
    @PostMapping
    public ResponseEntity<Tweet> postTweet(@RequestBody TweetRequest request) {
        Tweet tweet = tweetService.createTweet(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(tweet);
    }

    // Get details of a tweet
    @GetMapping("/{tweetId}")
    public ResponseEntity<Tweet> getTweet(@PathVariable UUID tweetId) {
        Tweet tweet = tweetService.getTweet(tweetId);
        return ResponseEntity.ok(tweet);
    }

    // Delete a tweet
    @DeleteMapping("/{tweetId}")
    public ResponseEntity<String> deleteTweet(@PathVariable UUID tweetId) {
        tweetService.deleteTweet(tweetId);
        return ResponseEntity.ok("Tweet deleted successfully");
    }

    // Like a tweet
    @PostMapping("/{tweetId}/like")
    public ResponseEntity<String> likeTweet(@PathVariable UUID tweetId, @RequestBody LikeRequest request) {
        tweetService.likeTweet(tweetId, request.getUserId());
        return ResponseEntity.ok("Tweet liked successfully");
    }

    // Remove like from a tweet
    @DeleteMapping("/{tweetId}/like")
    public ResponseEntity<String> removeLike(@PathVariable UUID tweetId, @RequestBody LikeRequest request) {
        tweetService.removeLike(tweetId, request.getUserId());
        return ResponseEntity.ok("Like removed successfully");
    }

    // Retweet a tweet
    @PostMapping("/{tweetId}/retweet")
    public ResponseEntity<String> retweet(@PathVariable UUID tweetId, @RequestBody RetweetRequest request) {
        tweetService.retweet(tweetId, request.getUserId());
        return ResponseEntity.ok("Tweet retweeted successfully");
    }

    // Remove retweet
    @DeleteMapping("/{tweetId}/retweet")
    public ResponseEntity<String> removeRetweet(@PathVariable UUID tweetId, @RequestBody RetweetRequest request) {
        tweetService.removeRetweet(tweetId, request.getUserId());
        return ResponseEntity.ok("Retweet removed successfully");
    }

    // Reply to a tweet
    @PostMapping("/{tweetId}/reply")
    public ResponseEntity<Reply> replyToTweet(@PathVariable UUID tweetId, @RequestBody ReplyRequest request) {
        Reply reply = tweetService.replyToTweet(tweetId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reply);
    }

    // Delete a reply
    @DeleteMapping("/{tweetId}/reply/{replyId}")
    public ResponseEntity<String> deleteReply(@PathVariable UUID tweetId, @PathVariable UUID replyId) {
        tweetService.deleteReply(replyId);
        return ResponseEntity.ok("Reply deleted successfully");
    }

    // Get replies to a tweet
    @GetMapping("/{tweetId}/replies")
    public ResponseEntity<List<Reply>> getReplies(@PathVariable UUID tweetId) {
        List<Reply> replies = tweetService.getReplies(tweetId);
        return ResponseEntity.ok(replies);
    }

    // Get tweets liked by a user
    @GetMapping("/users/{userId}/likes")
    public ResponseEntity<List<Tweet>> getLikedTweets(@PathVariable UUID userId) {
        List<Tweet> tweets = tweetService.getLikedTweets(userId);
        return ResponseEntity.ok(tweets);
    }

    // Get retweets of a tweet
    @GetMapping("/{tweetId}/retweets")
    public ResponseEntity<List<Retweet>> getRetweets(@PathVariable UUID tweetId) {
        List<Retweet> retweets = tweetService.getRetweets(tweetId);
        return ResponseEntity.ok(retweets);
    }

    // Get all tweets of a user
    @GetMapping("/users/{userId}/tweets")
    public ResponseEntity<List<Tweet>> getUserTweets(@PathVariable UUID userId) {
        List<Tweet> tweets = tweetService.getUserTweets(userId);
        return ResponseEntity.ok(tweets);
    }
}

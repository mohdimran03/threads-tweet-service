package com.example.tweet_service.services;

import com.example.tweet_service.dtos.ReplyRequest;
import com.example.tweet_service.dtos.TweetRequest;
import com.example.tweet_service.kafka.KafkaProducerService;
import com.example.tweet_service.kafka.events.NotificationEvent;
import com.example.tweet_service.models.*;
import com.example.tweet_service.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.tweet_service.repository.HashtagRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TweetService {

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private RetweetRepository retweetRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HashtagRepository hashtagRepository;

    public TweetService(HashtagRepository hashtagRepository) {
        this.hashtagRepository = hashtagRepository;
    }

    public Tweet createTweet(TweetRequest request) {
        // Create and save tweet
        Tweet tweet = new Tweet();
        tweet.setUserId(request.getUserId());
        tweet.setContent(request.getContent());
        tweet.setCreatedAt(LocalDateTime.now());
        tweet.setUpdatedAt(LocalDateTime.now());
        Tweet savedTweet = tweetRepository.save(tweet);

        // Extract and handle hashtags
        List<String> hashtags = extractHashtags(request.getContent());
        List<UUID> hashtagIds = new ArrayList<>();
        for (String tag : hashtags) {
            Hashtag hashtag = hashtagRepository.findByTag(tag).orElseGet(() -> {
                Hashtag newHashtag = new Hashtag();
                newHashtag.setTag(tag);
                newHashtag.setCreatedAt(LocalDateTime.now());
                newHashtag.insertTweetId(savedTweet.getId());
                return hashtagRepository.save(newHashtag);
            });

            if (hashtag.getId() != null && !hashtag.getTweetIds().contains(savedTweet.getId())) {
                hashtag.insertTweetId(savedTweet.getId());
                hashtagRepository.save(hashtag);
            }

            hashtagIds.add(hashtag.getId());
        }

        savedTweet.setHashtagIds(hashtagIds);
        return tweetRepository.save(savedTweet);
    }

    public static List<String> extractHashtags(String content) {
        Pattern pattern = Pattern.compile("#\\w+");
        Matcher matcher = pattern.matcher(content);
        List<String> hashtags = new ArrayList<>();
        while (matcher.find()) {
            hashtags.add(matcher.group().substring(1).toLowerCase());
        }
        return hashtags;
    }

    public Tweet getTweet(UUID tweetId) {
        return tweetRepository.findById(tweetId)
                .orElseThrow(() -> new RuntimeException("Tweet not found"));
    }

    @Transactional
    public void deleteTweet(UUID tweetId) {
        tweetRepository.deleteById(tweetId);
        likeRepository.deleteByTweetId(tweetId);
        retweetRepository.deleteByOriginalTweetId(tweetId);
        replyRepository.deleteByTweetId(tweetId);
    }

    public void likeTweet(UUID tweetId, UUID userId) {
        if (!likeRepository.existsByTweetIdAndUserId(tweetId, userId)) {
            Like like = new Like();
            like.setTweetId(tweetId);
            like.setUserId(userId);
            like.setCreatedAt(LocalDateTime.now());
            likeRepository.save(like);

            String topic = "notification-topic";
            NotificationEvent event = new NotificationEvent();
            event.setUserId(userId);
            event.setType("Liked");
            event.setContent("liked your tweet");

            kafkaProducerService.sendMessage(topic, event);
        } else {
            throw new RuntimeException("User already liked this tweet");
        }
    }

    @Transactional
    public void removeLike(UUID tweetId, UUID userId) {
        Like like = likeRepository.findByTweetIdAndUserId(tweetId, userId)
                .orElseThrow(() -> new RuntimeException("Like not found"));
        likeRepository.delete(like);
    }

    public void retweet(UUID tweetId, UUID userId) {
        if (!retweetRepository.existsByOriginalTweetIdAndUserId(tweetId, userId)) {
            Retweet retweet = new Retweet();
            retweet.setOriginalTweetId(tweetId);
            retweet.setUserId(userId);
            retweet.setCreatedAt(LocalDateTime.now());
            retweetRepository.save(retweet);
        } else {
            throw new RuntimeException("User already retweeted this tweet");
        }
    }

    @Transactional
    public void removeRetweet(UUID tweetId, UUID userId) {
        Retweet retweet = retweetRepository.findByOriginalTweetIdAndUserId(tweetId, userId)
                .orElseThrow(() -> new RuntimeException("Retweet not found"));
        retweetRepository.delete(retweet);
    }

    public Reply replyToTweet(UUID tweetId, ReplyRequest request) {
        Reply reply = new Reply();
        reply.setTweetId(tweetId);
        reply.setUserId(request.getUserId());
        reply.setContent(request.getContent());
        reply.setCreatedAt(LocalDateTime.now());
        return replyRepository.save(reply);
    }

    @Transactional
    public void deleteReply(UUID replyId) {
        replyRepository.deleteById(replyId);
    }

    public List<Reply> getReplies(UUID tweetId) {
        return replyRepository.findByTweetId(tweetId);
    }

    public List<Tweet> getLikedTweets(UUID userId) {
        return tweetRepository.findTweetsLikedByUser(userId);
    }

    public List<Retweet> getRetweets(UUID tweetId) {
        return retweetRepository.findByOriginalTweetId(tweetId);
    }

    public List<Tweet> getUserTweets(UUID userId) {
        return tweetRepository.findByUserId(userId);
    }

    public ResponseEntity<?> getFollowingTweetsByUserId(UUID userId) {
        String url = "http://USER-SERVICE/users/" + userId + "/following";
        List<Map<String, Object>> following = restTemplate.getForObject(url, List.class);

        // Extract IDs from the following list
        List<UUID> userIds = following.stream()
                .map(user -> UUID.fromString(user.get("id").toString()))
                .collect(Collectors.toList());

        // Fetch tweets based on the extracted user IDs
        List<?> tweets = tweetRepository.fetchTweetsByUserIds(userIds);
        return ResponseEntity.ok(tweets);
    }

    public ResponseEntity<?> getExplore(UUID userId) {
        String url = "http://USER-SERVICE/users/" + userId + "/following";
        List<Map<String, Object>> following = restTemplate.getForObject(url, List.class);

        // Extract IDs from the following list
        List<UUID> userIds = following.stream()
                .map(user -> UUID.fromString(user.get("id").toString()))
                .collect(Collectors.toList());
        List<?> tweets = tweetRepository.fetchTweetsForExplore(userIds);
        return ResponseEntity.ok(tweets);
    }

    public ResponseEntity<?>getTrendingHashtags() {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        List<?> hashtags = hashtagRepository.findTrendingHashtags(oneDayAgo);

        return ResponseEntity.ok(hashtags);
    }

    public ResponseEntity<?> getTweetsByHashtag(String tag) {
        // Find the hashtag by tag
        Optional<Hashtag> optionalHashtag = hashtagRepository.findByTag(tag);

        // If the hashtag exists, find tweets by their IDs
        if (optionalHashtag.isPresent()) {
            Hashtag hashtag = optionalHashtag.get();
            List<UUID> tweetIds = hashtag.getTweetIds();
            if (!tweetIds.isEmpty()) {
                List<Tweet> tweets = tweetRepository.findByIdIn(tweetIds);
                return ResponseEntity.ok(tweets);
            }
        }

        // If the hashtag does not exist or has no associated tweets, return an empty list
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}

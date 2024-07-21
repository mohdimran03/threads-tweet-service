package com.example.tweet_service.services;

import com.example.tweet_service.dtos.ReplyRequest;
import com.example.tweet_service.dtos.TweetRequest;
import com.example.tweet_service.kafka.KafkaProducerService;
import com.example.tweet_service.kafka.events.NotificationEvent;
import com.example.tweet_service.models.Like;
import com.example.tweet_service.models.Reply;
import com.example.tweet_service.models.Retweet;
import com.example.tweet_service.models.Tweet;
import com.example.tweet_service.repository.LikeRepository;
import com.example.tweet_service.repository.ReplyRepository;
import com.example.tweet_service.repository.RetweetRepository;
import com.example.tweet_service.repository.TweetRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

    public Tweet createTweet(TweetRequest request) {
        Tweet tweet = new Tweet();
        tweet.setUserId(request.getUserId());
        tweet.setContent(request.getContent());
        tweet.setCreatedAt(LocalDateTime.now());
        tweet.setUpdatedAt(LocalDateTime.now());
        return tweetRepository.save(tweet);
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
}

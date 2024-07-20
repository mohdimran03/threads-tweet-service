package com.example.tweet_service.services;

import com.example.tweet_service.dtos.ReplyRequest;
import com.example.tweet_service.dtos.TweetRequest;
import com.example.tweet_service.models.Like;
import com.example.tweet_service.models.Reply;
import com.example.tweet_service.models.Retweet;
import com.example.tweet_service.models.Tweet;
import com.example.tweet_service.repository.LikeRepository;
import com.example.tweet_service.repository.ReplyRepository;
import com.example.tweet_service.repository.RetweetRepository;
import com.example.tweet_service.repository.TweetRepository;
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
        } else {
            throw new RuntimeException("User already liked this tweet");
        }
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

    public Reply replyToTweet(UUID tweetId, ReplyRequest request) {
        Reply reply = new Reply();
        reply.setTweetId(tweetId);
        reply.setUserId(request.getUserId());
        reply.setContent(request.getContent());
        reply.setCreatedAt(LocalDateTime.now());
        return replyRepository.save(reply);
    }

    public List<Reply> getReplies(UUID tweetId) {
        return replyRepository.findByTweetId(tweetId);
    }

    public List<Tweet> getLikedTweets(UUID userId) {
        return tweetRepository.findTweetsLikedByUser(userId);
    }
}

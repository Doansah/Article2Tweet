package article2tweet.com.article2tweet.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TweetThread {
    private String threadId;
    private List<Tweet> tweets;
    private String originalArticleUrl;
    private String originalArticleTitle;
    private LocalDateTime createdAt;
    private int totalTweets;
    
    // Constructor for creating a thread from tweets
    public TweetThread(List<Tweet> tweets, String originalArticleUrl, String originalArticleTitle) {
        this.tweets = tweets;
        this.originalArticleUrl = originalArticleUrl;
        this.originalArticleTitle = originalArticleTitle;
        this.totalTweets = tweets.size();
        this.createdAt = LocalDateTime.now();
        this.threadId = generateThreadId();
        
        // Set thread ID for each tweet
        tweets.forEach(tweet -> tweet.setThreadId(this.threadId));
    }
    
    private String generateThreadId() {
        return "thread_" + System.currentTimeMillis();
    }
    
    public boolean isValidThread() {
        return tweets != null && !tweets.isEmpty() && 
               tweets.stream().allMatch(tweet -> tweet.getContent().length() <= 280);
    }
}

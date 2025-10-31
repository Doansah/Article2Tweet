package article2tweet.com.article2tweet.agent;

import article2tweet.com.article2tweet.domain.Article;
import article2tweet.com.article2tweet.domain.Tweet;
import article2tweet.com.article2tweet.domain.TweetThread;
import lombok.extern.slf4j.Slf4j;
//import com.embabel.agent.api.annotation.Agent;
//import com.embabel.agent.api.annotation.Action;
//import com.embabel.agent.api.annotation.AchievesGoal;
//import com.embabel.agent.api.common.OperationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * AI Agent that converts Medium articles into Twitter threads
 * This agent breaks down long-form content into tweet-sized chunks
 * while maintaining coherence and engagement
 */
//@Agent(description = "Convert Medium articles into engaging Twitter threads")
@Component
@Slf4j
public class Article2TweetAgent {
    
    private static final int MAX_TWEET_LENGTH = 280;
    private static final int RECOMMENDED_TWEET_LENGTH = 240; // Leave room for thread numbering
    
    /**
     * Extract key points and main ideas from the article
     */
    //@Action
    public List<String> extractKeyPoints(Article article) {
        log.info("Extracting key points from article: {}", article.getTitle());
        
        // For now, we'll implement a simple extraction method
        // In the full Embabel version, this would use LLM to intelligently extract key points
        String content = article.getContent();
        List<String> keyPoints = new ArrayList<>();
        
        // Simple paragraph-based extraction (will be replaced with LLM)
        String[] paragraphs = content.split("\\n\\s*\\n");
        for (String paragraph : paragraphs) {
            if (paragraph.trim().length() > 50) { // Skip very short paragraphs
                // Extract the main idea (simplified - would use LLM in real implementation)
                String mainIdea = extractMainIdea(paragraph.trim());
                if (mainIdea.length() <= RECOMMENDED_TWEET_LENGTH) {
                    keyPoints.add(mainIdea);
                }
            }
        }
        
        log.info("Extracted {} key points", keyPoints.size());
        return keyPoints;
    }
    
    /**
     * Convert key points into individual tweets
     */
    //@Action
    public List<Tweet> createTweetsFromKeyPoints(List<String> keyPoints, Article article) {
        log.info("Converting {} key points into tweets", keyPoints.size());
        
        List<Tweet> tweets = new ArrayList<>();
        
        // First tweet - introduction with article title and author
        String introTweet = String.format("🧵 Thread: %s by %s\n\nKey insights 👇", 
                                        article.getTitle(), 
                                        article.getAuthor() != null ? article.getAuthor() : "the author");
        tweets.add(new Tweet(1, introTweet));
        
        // Convert key points to tweets
        for (int i = 0; i < keyPoints.size(); i++) {
            String point = keyPoints.get(i);
            String tweetContent = formatTweetContent(point, i + 2, keyPoints.size() + 2);
            tweets.add(new Tweet(i + 2, tweetContent));
        }
        
        // Final tweet with call to action
        String finalTweet = String.format("That's a wrap! 🎬\n\nRead the full article: %s\n\nWhat are your thoughts? 💭", 
                                        article.getUrl());
        tweets.add(new Tweet(keyPoints.size() + 2, finalTweet));
        
        log.info("Created {} tweets total", tweets.size());
        return tweets;
    }
    
    /**
     * Create the final tweet thread
     */
    //@AchievesGoal(description = "Create a complete Twitter thread from a Medium article")
    //@Action
    public TweetThread createTwitterThread(Article article) {
        log.info("Creating Twitter thread for article: {}", article.getTitle());
        
        List<String> keyPoints = extractKeyPoints(article);
        List<Tweet> tweets = createTweetsFromKeyPoints(keyPoints, article);
        
        TweetThread thread = new TweetThread(tweets, article.getUrl(), article.getTitle());
        
        log.info("Created Twitter thread with {} tweets", thread.getTotalTweets());
        return thread;
    }
    
    // Helper methods
    
    private String extractMainIdea(String paragraph) {
        // Simplified extraction - in real implementation, this would use LLM
        // For now, we'll take the first sentence or truncate if too long
        String[] sentences = paragraph.split("\\.");
        String firstSentence = sentences[0].trim();
        
        if (firstSentence.length() <= RECOMMENDED_TWEET_LENGTH) {
            return firstSentence + ".";
        } else {
            // Truncate and add ellipsis
            return firstSentence.substring(0, RECOMMENDED_TWEET_LENGTH - 3) + "...";
        }
    }
    
    private String formatTweetContent(String content, int currentTweet, int totalTweets) {
        // Add thread numbering
        String numbering = String.format("(%d/%d) ", currentTweet, totalTweets);
        
        // Ensure the content fits with numbering
        int availableSpace = MAX_TWEET_LENGTH - numbering.length();
        
        if (content.length() <= availableSpace) {
            return numbering + content;
        } else {
            return numbering + content.substring(0, availableSpace - 3) + "...";
        }
    }
}

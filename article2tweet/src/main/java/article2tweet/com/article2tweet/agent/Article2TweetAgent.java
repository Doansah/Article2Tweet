package article2tweet.com.article2tweet.agent;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import article2tweet.com.article2tweet.domain.Article;
import article2tweet.com.article2tweet.domain.Tweet;
import article2tweet.com.article2tweet.domain.TweetThread;
import article2tweet.com.article2tweet.service.OpenAIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AI Agent that converts Medium articles into Twitter threads
 * This agent breaks down long-form content into tweet-sized chunks
 * while maintaining coherence and engagement
 */
//@Agent(description = "Convert Medium articles into engaging Twitter threads")
@Component
@RequiredArgsConstructor
@Slf4j
public class Article2TweetAgent {
    
    private final OpenAIService openAIService;
    
    private static final int MAX_TWEET_LENGTH = 280;
    private static final int RECOMMENDED_TWEET_LENGTH = 240; // Leave room for thread numbering
    
    /**
     * Extract key insights using OpenAI for intelligent content analysis
     */
    //@Action
    public List<String> extractKeyInsights(Article article) {
        log.info("Extracting key insights from article using AI: {}", article.getTitle());
        
        // Use OpenAI to intelligently extract exactly 3 key insights
        List<String> insights = openAIService.extractKeyInsights(
            article.getContent(), 
            article.getTitle()
        );
        
        log.info("Extracted {} AI-powered insights", insights.size());
        return insights;
    }
    
    /**
     * Create exactly 5 tweets with casual tone: hook + 3 insights + wrap-up
     */
    //@Action
    public List<Tweet> createCasualTweetsFromInsights(List<String> insights, Article article) {
        log.info("Creating 5-tweet thread in casual style for: {}", article.getTitle());
        
        List<Tweet> tweets = new ArrayList<>();
        
        // Tweet 1: AI-generated engaging hook
        String hookTweet = openAIService.generateHookTweet(article.getTitle(), insights.get(0));
        tweets.add(new Tweet(1, hookTweet));
        
        // Tweets 2-4: Convert insights to casual tweets
        for (int i = 0; i < Math.min(insights.size(), 3); i++) {
            String casualTweet = openAIService.generateCasualTweet(insights.get(i), i + 2);
            tweets.add(new Tweet(i + 2, casualTweet));
        }
        
        // Tweet 5: AI-generated wrap-up with article link
        String wrapUpTweet = openAIService.generateWrapUpTweet(article.getTitle(), article.getUrl());
        tweets.add(new Tweet(5, wrapUpTweet));
        
        log.info("Created 5-tweet casual thread");
        return tweets;
    }
    
    /**
     * Create the final tweet thread with AI-powered content generation
     */
    //@AchievesGoal(description = "Create a complete Twitter thread from a Medium article")
    //@Action
    public TweetThread createTwitterThread(Article article) {
        log.info("Creating AI-powered Twitter thread for article: {}", article.getTitle());
        
        // Extract key insights using OpenAI
        List<String> insights = extractKeyInsights(article);
        
        // Create casual, engaging tweets from insights
        List<Tweet> tweets = createCasualTweetsFromInsights(insights, article);
        
        TweetThread thread = new TweetThread(tweets, article.getUrl(), article.getTitle());
        
        log.info("Created AI-powered Twitter thread with {} tweets", thread.getTotalTweets());
        return thread;
    }
    

}

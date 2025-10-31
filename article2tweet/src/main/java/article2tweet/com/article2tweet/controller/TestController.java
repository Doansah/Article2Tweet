package article2tweet.com.article2tweet.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import article2tweet.com.article2tweet.agent.Article2TweetAgent;
import article2tweet.com.article2tweet.domain.Article;
import article2tweet.com.article2tweet.domain.ArticleSummary;
import article2tweet.com.article2tweet.domain.MediumUser;
import article2tweet.com.article2tweet.domain.TweetThread;
import article2tweet.com.article2tweet.service.MediumApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for testing Medium API integration and AI-powered tweet generation
 */
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Slf4j
public class TestController {
    
    private final MediumApiService mediumApiService;
    private final Article2TweetAgent article2TweetAgent;
    
    @GetMapping("/user/{username}")
    public MediumUser getUser(@PathVariable String username) {
        log.info("Testing user lookup for: {}", username);
        return mediumApiService.getUserByUsername(username);
    }
    
    @GetMapping("/user/{username}/articles")
    public List<ArticleSummary> getUserArticles(@PathVariable String username) {
        log.info("Testing article fetch for user: {}", username);
        return mediumApiService.getUserArticlesByUsername(username);
    }
    
    @GetMapping("/status")
    public String getStatus() {
        return "Medium API Service is running! Try /api/test/user/dillondoa";
    }
    
    @GetMapping("/ai-thread/{articleId}")
    public TweetThread createAIThread(@PathVariable String articleId) {
        log.info("Testing AI-powered thread creation for article: {}", articleId);
        
        // Get full article content
        Article article = mediumApiService.getFullArticleContent(articleId);
        
        // Generate AI-powered tweet thread
        return article2TweetAgent.createTwitterThread(article);
    }
    
    @GetMapping("/quick-test")
    public TweetThread quickAITest() {
        log.info("Quick AI test with sample article");
        
        // Create a test article with dillondoa's style content
        Article testArticle = new Article(
            "Building Better APIs: Lessons Learned",
            """
            Building great APIs is both an art and a science. After years of development, I've learned some key principles that make the difference between good and great APIs.
            
            First, consistency is everything. When developers use your API, they should be able to predict how endpoints behave based on patterns they've already learned. This means consistent naming conventions, error handling, and response structures.
            
            Second, documentation isn't optional. Your API is only as good as its documentation. Include examples, explain edge cases, and keep it up to date. Great docs can turn frustrated developers into loyal advocates.
            
            Third, versioning strategy matters from day one. Don't wait until you need to make breaking changes to think about how you'll handle them. Plan for evolution from the start.
            
            Finally, performance and reliability aren't afterthoughts. Design with scale in mind, implement proper caching, and monitor everything. Your users will thank you.
            """,
            "https://medium.com/@dillondoa/building-better-apis"
        );
        testArticle.setAuthor("Dillon Ansah");
        
        return article2TweetAgent.createTwitterThread(testArticle);
    }
}

package article2tweet.com.article2tweet.agent;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import article2tweet.com.article2tweet.domain.Article;
import article2tweet.com.article2tweet.domain.TweetThread;
import article2tweet.com.article2tweet.service.OpenAIService;

class Article2TweetAgentTest {

    private Article2TweetAgent agent;
    private OpenAIService mockOpenAIService;

    @BeforeEach
    void setUp() {
        mockOpenAIService = mock(OpenAIService.class);
        agent = new Article2TweetAgent(mockOpenAIService);
        
        // Setup mock responses
        List<String> mockInsights = Arrays.asList(
            "AI development is accelerating rapidly with new breakthrough models.",
            "Machine learning democratization is making AI accessible to more developers.", 
            "Ethical considerations are becoming central to AI development processes."
        );
        
        when(mockOpenAIService.extractKeyInsights(anyString(), anyString()))
            .thenReturn(mockInsights);
        when(mockOpenAIService.generateHookTweet(anyString(), anyString()))
            .thenReturn("🧵 The future of AI development is here and it's exciting! Let me break down what's happening:");
        when(mockOpenAIService.generateCasualTweet(anyString(), any(Integer.class)))
            .thenReturn("First insight: AI development is moving fast")
            .thenReturn("Second insight: ML is becoming more accessible")
            .thenReturn("Third insight: Ethics matter more than ever");
        when(mockOpenAIService.generateWrapUpTweet(anyString(), anyString()))
            .thenReturn("That's a wrap! What do you think about these AI trends?");
    }

    @Test
    void testCreateTwitterThread() {
        // Given
        Article testArticle = new Article(
            "The Future of AI Development",
            "Artificial intelligence is rapidly evolving.\n\nMachine learning models are becoming more sophisticated.\n\n" +
            "Automation is expanding beyond simple tasks.\n\nEthical AI development is becoming a priority.\n\n" +
            "The integration of AI into everyday applications is seamless.\n\nEdge computing enables AI processing closer to data sources.",
            "https://medium.com/@author/future-of-ai"
        );
        testArticle.setAuthor("Tech Writer");

        // When
        TweetThread result = agent.createTwitterThread(testArticle);

        // Then
        assertNotNull(result);
        assertNotNull(result.getTweets());
        assertTrue(result.getTweets().size() >= 3); // At least intro, content, and final tweet
        
        // Check first tweet is introduction
        assertTrue(result.getTweets().get(0).getContent().contains("Thread:"));
        assertTrue(result.getTweets().get(0).getContent().contains("The Future of AI Development"));
        
        // Check final tweet has call to action
        var lastTweet = result.getTweets().get(result.getTweets().size() - 1);
        assertTrue(lastTweet.getContent().contains("Read the full article"));
        assertTrue(lastTweet.getContent().contains("https://medium.com/@author/future-of-ai"));
        
        // Verify all tweets are under 280 characters
        for (var tweet : result.getTweets()) {
            assertTrue(tweet.getContent().length() <= 280, 
                "Tweet exceeds 280 characters: " + tweet.getContent());
        }
        
        // Verify proper thread numbering
        assertEquals(1, result.getTweets().get(0).getOrder());
        assertEquals(result.getTweets().size(), 
                    result.getTweets().get(result.getTweets().size() - 1).getOrder());
        
        // Verify thread metadata
        assertEquals(testArticle.getUrl(), result.getOriginalArticleUrl());
        assertEquals(testArticle.getTitle(), result.getOriginalArticleTitle());
        assertTrue(result.isValidThread());
    }

    @Test
    void testExtractKeyPoints() {
        // Given
        Article testArticle = new Article(
            "Test Article",
            "First important point about technology.\n\nSecond crucial insight about innovation.\n\nThird key observation about the future.",
            "https://test.com"
        );

        // When
        var insights = agent.extractKeyInsights(testArticle);

        // Then
        assertNotNull(insights);
        assertEquals(3, insights.size());
        
        // Each insight should be meaningful
        for (String insight : insights) {
            assertNotNull(insight);
            assertTrue(insight.length() > 0);
        }
    }

    @Test
    void testAITweetThreadCreation() {
        // Given
        Article testArticle = new Article(
            "AI Development Trends",
            "AI is evolving rapidly with new breakthrough models and democratization efforts making it more accessible.",
            "https://test.com/ai-trends"
        );

        // When
        TweetThread result = agent.createTwitterThread(testArticle);

        // Then
        assertNotNull(result);
        assertEquals(5, result.getTweets().size()); // Hook + 3 insights + wrap-up
        assertEquals("AI Development Trends", result.getOriginalArticleTitle());
        assertEquals("https://test.com/ai-trends", result.getOriginalArticleUrl());
    }
}

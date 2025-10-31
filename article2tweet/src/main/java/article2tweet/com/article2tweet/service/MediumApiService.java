package article2tweet.com.article2tweet.service;

import article2tweet.com.article2tweet.domain.Article;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MediumApiService {
    
    private final ObjectMapper objectMapper;
    private final String apiKey;
    
    public MediumApiService(ObjectMapper objectMapper,
                           @Value("${medium.api.key:}") String apiKey) {
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
    }
    
    /**
     * Fetch article by Medium URL (placeholder - requires WebClient setup)
     */
    public Article fetchArticleByUrl(String mediumUrl) {
        log.info("Fetching article from URL: {}", mediumUrl);
        
        // TODO: Implement actual API call when WebClient is properly configured
        log.warn("Using mock data - API integration not yet implemented");
        return createMockArticle("Sample Article from " + mediumUrl);
    }
    
    /**
     * Search for articles by topic (placeholder - requires WebClient setup)
     */
    public List<Article> searchArticles(String query, int limit) {
        log.info("Searching articles with query: {} (limit: {})", query, limit);
        
        // TODO: Implement actual API call when WebClient is properly configured
        log.warn("Using mock data - API integration not yet implemented");
        List<Article> mockResults = new ArrayList<>();
        
        for (int i = 1; i <= Math.min(limit, 3); i++) {
            mockResults.add(createMockArticle(query + " - Article " + i));
        }
        
        return mockResults;
    }
    
    /**
     * Mock method for testing without API key
     */
    public Article createMockArticle(String title) {
        return new Article(
            title,
            "This is a sample Medium article content that we'll convert into a series of tweets. " +
            "It contains multiple paragraphs and ideas that need to be broken down into digestible " +
            "tweet-sized pieces while maintaining the core message and engaging the audience. " +
            "The content should be informative, engaging, and suitable for social media sharing.",
            "https://medium.com/@author/" + title.toLowerCase().replace(" ", "-")
        );
    }
    
    private String extractArticleIdFromUrl(String url) {
        // Extract article ID from Medium URL
        // Example: https://medium.com/@author/article-title-123abc -> 123abc
        String[] parts = url.split("/");
        return parts[parts.length - 1].split("-")[parts[parts.length - 1].split("-").length - 1];
    }
    
    private Article parseArticleResponse(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            
            return new Article(
                root.path("title").asText(),
                root.path("content").asText(),
                root.path("author").asText(),
                root.path("url").asText(),
                LocalDate.parse(root.path("published_at").asText().substring(0, 10)),
                "Medium",
                parseTagsList(root.path("tags")),
                root.path("reading_time").asInt()
            );
        } catch (Exception e) {
            log.error("Error parsing article response: {}", e.getMessage());
            throw new RuntimeException("Failed to parse article response", e);
        }
    }
    
    private List<Article> parseSearchResponse(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode articles = root.path("articles");
            List<Article> result = new ArrayList<>();
            
            for (JsonNode articleNode : articles) {
                Article article = new Article(
                    articleNode.path("title").asText(),
                    articleNode.path("content").asText(),
                    articleNode.path("author").asText(),
                    articleNode.path("url").asText(),
                    LocalDate.parse(articleNode.path("published_at").asText().substring(0, 10)),
                    "Medium",
                    parseTagsList(articleNode.path("tags")),
                    articleNode.path("reading_time").asInt()
                );
                result.add(article);
            }
            
            return result;
        } catch (Exception e) {
            log.error("Error parsing search response: {}", e.getMessage());
            throw new RuntimeException("Failed to parse search response", e);
        }
    }
    
    private List<String> parseTagsList(JsonNode tagsNode) {
        List<String> tags = new ArrayList<>();
        if (tagsNode.isArray()) {
            for (JsonNode tag : tagsNode) {
                tags.add(tag.asText());
            }
        }
        return tags;
    }
}

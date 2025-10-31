package article2tweet.com.article2tweet.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import article2tweet.com.article2tweet.domain.Article;
import article2tweet.com.article2tweet.domain.ArticleSummary;
import article2tweet.com.article2tweet.domain.MediumUser;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MediumApiService {
    
    private final ObjectMapper objectMapper;
    private final WebClient mediumWebClient;
    private final String apiKey;
    
    public MediumApiService(ObjectMapper objectMapper,
                           WebClient mediumWebClient,
                           @Value("${medium.api.key:}") String apiKey) {
        this.objectMapper = objectMapper;
        this.mediumWebClient = mediumWebClient;
        this.apiKey = apiKey;
    }
    
    /**
     * Create WebClient with RapidAPI headers for Medium2 service
     */
    private WebClient createRapidApiWebClient() {
        if (apiKey == null || apiKey.isEmpty()) {
            return null;
        }
        
        return WebClient.builder()
            .baseUrl("https://medium2.p.rapidapi.com")
            .defaultHeader("Content-Type", "application/json")
            .defaultHeader("Accept", "application/json")
            .defaultHeader("x-rapidapi-key", apiKey)
            .defaultHeader("x-rapidapi-host", "medium2.p.rapidapi.com")
            .build();
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
    
    /**
     * Create mock article summaries for testing
     */
    private List<ArticleSummary> createMockArticleSummaries(String userId) {
        List<ArticleSummary> mockArticles = new ArrayList<>();
        
        // Create some realistic mock articles for dillondoa
        String[] mockTitles = {
            "Building Scalable APIs with Spring Boot",
            "The Future of AI in Software Development", 
            "Microservices Architecture Best Practices",
            "Understanding Docker and Containerization",
            "Modern Java Development Techniques"
        };
        
        for (int i = 0; i < mockTitles.length; i++) {
            ArticleSummary article = new ArticleSummary();
            article.setId("mock_article_" + (i + 1));
            article.setTitle(mockTitles[i]);
            article.setSubtitle("A comprehensive guide to " + mockTitles[i].toLowerCase());
            article.setUrl("https://medium.com/@dillondoa/" + mockTitles[i].toLowerCase().replace(" ", "-"));
            article.setPublishedAt("2024-0" + (i + 1) + "-15");
            article.setWordCount(1200 + (i * 200));
            article.setReadingTime(5 + i);
            article.setClaps(25 + (i * 10));
            mockArticles.add(article);
        }
        
        return mockArticles;
    }
    
    /**
     * Create mock article from ID for testing
     */
    private Article createMockArticleFromId(String articleId) {
        // Map article ID to a more detailed mock article
        String title = "Mock Article " + articleId;
        String content = """
                Building great software is both an art and a science. It requires technical expertise, creative problem-solving, and a deep understanding of user needs.
                
                In today's fast-paced development environment, developers must balance speed with quality. This means adopting best practices while remaining flexible enough to adapt to changing requirements.
                
                One key principle is to write clean, maintainable code. This involves following coding standards, writing comprehensive tests, and documenting your work thoroughly.
                
                Another important aspect is understanding your users. Great software solves real problems and provides genuine value to the people who use it.
                
                Finally, continuous learning is essential. Technology evolves rapidly, and successful developers stay curious and keep their skills up to date.
                
                By focusing on these fundamentals, you can create software that not only works well but also stands the test of time.
                """;
        
        Article article = new Article(title, content, "https://medium.com/@dillondoa/" + articleId);
        article.setAuthor("Dillon Ansah");
        article.setPublishedDate(LocalDate.now().minusDays(7));
        article.setSource("Medium");
        article.setEstimatedReadTime(6);
        
        return article;
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
    
    /**
     * Get Medium user information by username using RapidAPI Medium2 workflow
     * Step 1: Get user ID from username
     * Step 2: Get user info from user ID
     */
    public MediumUser getUserByUsername(String username) {
        log.info("🔍 Fetching Medium user: {}", username);
        
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("❌ Medium API key not configured, using mock data");
            return new MediumUser("mock_user_id_" + username, username, "Mock User for " + username);
        }
        
        try {
            WebClient rapidApiClient = createRapidApiWebClient();
            if (rapidApiClient == null) {
                log.warn("❌ Could not create RapidAPI client, using mock data");
                return new MediumUser("mock_user_id_" + username, username, "Mock User for " + username);
            }
            
            // Step 1: Get user ID from username using /user/id_for/{username}
            log.info("🌐 Step 1: Getting user ID for username: {}", username);
            String userIdResponse = rapidApiClient
                    .get()
                    .uri("/user/id_for/{username}", username)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), clientResponse -> {
                        return clientResponse.bodyToMono(String.class)
                            .map(errorBody -> {
                                log.error("🔍 RapidAPI Medium 4xx error on user ID lookup: {} - {}", clientResponse.statusCode(), errorBody);
                                return new RuntimeException("Medium API user ID lookup failed: " + clientResponse.statusCode());
                            });
                    })
                    .bodyToMono(String.class)
                    .block();
                    
            // Parse user ID from response
            JsonNode userIdJson = objectMapper.readTree(userIdResponse);
            String userId = userIdJson.path("id").asText();
            log.info("✅ Found user ID: {}", userId);
            
            // Step 2: Get full user info using /user/{user_id}
            log.info("🌐 Step 2: Getting user info for ID: {}", userId);
            String userInfoResponse = rapidApiClient
                    .get()
                    .uri("/user/{user_id}", userId)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), clientResponse -> {
                        return clientResponse.bodyToMono(String.class)
                            .map(errorBody -> {
                                log.error("🔍 RapidAPI Medium 4xx error on user info: {} - {}", clientResponse.statusCode(), errorBody);
                                return new RuntimeException("Medium API user info failed: " + clientResponse.statusCode());
                            });
                    })
                    .bodyToMono(String.class)
                    .block();
                    
            // Parse user info from response
            JsonNode userJson = objectMapper.readTree(userInfoResponse);
            MediumUser user = new MediumUser(
                userId,
                userJson.path("username").asText(),
                userJson.path("fullname").asText()
            );
            
            log.info("✅ Successfully fetched user: {} (ID: {})", user.getName(), user.getId());
            return user;
            
        } catch (Exception e) {
            log.error("❌ Error fetching user {}: {}", username, e.getMessage());
            log.info("🔄 Falling back to mock user data");
            return new MediumUser("mock_user_id_" + username, username, "Mock User for " + username);
        }
    }
    
    /**
     * Get articles for a specific user ID using RapidAPI Medium2
     */
    public List<ArticleSummary> getUserArticles(String userId) {
        log.info("📚 Fetching articles for user ID: {}", userId);
        
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("❌ Medium API key not configured, using mock data");
            return createMockArticleSummaries(userId);
        }
        
        try {
            WebClient rapidApiClient = createRapidApiWebClient();
            if (rapidApiClient == null) {
                log.warn("❌ Could not create RapidAPI client, using mock data");
                return createMockArticleSummaries(userId);
            }
            
            // Use RapidAPI Medium2 endpoint: /user/{user_id}/articles
            log.info("🌐 Getting articles for user ID: {}", userId);
            String articlesResponse = rapidApiClient
                    .get()
                    .uri("/user/{user_id}/articles", userId)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), clientResponse -> {
                        return clientResponse.bodyToMono(String.class)
                            .map(errorBody -> {
                                log.error("🔍 RapidAPI Medium 4xx error on articles: {} - {}", clientResponse.statusCode(), errorBody);
                                return new RuntimeException("Medium API articles failed: " + clientResponse.statusCode());
                            });
                    })
                    .bodyToMono(String.class)
                    .block();
                    
            // Parse the articles response
            JsonNode articlesJson = objectMapper.readTree(articlesResponse);
            JsonNode articleIds = articlesJson.path("associated_articles");
            
            List<ArticleSummary> articles = new ArrayList<>();
            
            // For each article ID, we would need to call /article/{article_id} to get full info
            // For now, let's create summaries with the IDs we have
            if (articleIds.isArray()) {
                for (JsonNode articleIdNode : articleIds) {
                    String articleId = articleIdNode.asText();
                    ArticleSummary summary = new ArticleSummary();
                    summary.setId(articleId);
                    summary.setTitle("Article " + articleId); // Would get from /article/{id} endpoint
                    summary.setUrl("https://medium.com/@" + userId + "/" + articleId);
                    articles.add(summary);
                }
            }
            
            log.info("✅ Successfully fetched {} article IDs for user {}", articles.size(), userId);
            return articles;
            
        } catch (Exception e) {
            log.error("❌ Error fetching articles for user {}: {}", userId, e.getMessage());
            log.info("🔄 Falling back to mock article data");
            return createMockArticleSummaries(userId);
        }
    }
    
    /**
     * Get full article content by article ID using RapidAPI Medium2
     */
    public Article getFullArticleContent(String articleId) {
        log.info("📖 Fetching full content for article ID: {}", articleId);
        
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("❌ Medium API key not configured, using mock data");
            return createMockArticleFromId(articleId);
        }
        
        try {
            WebClient rapidApiClient = createRapidApiWebClient();
            if (rapidApiClient == null) {
                log.warn("❌ Could not create RapidAPI client, using mock data");
                return createMockArticleFromId(articleId);
            }
            
            // Step 1: Get article info using /article/{article_id}
            log.info("🌐 Step 1: Getting article info for ID: {}", articleId);
            String articleInfoResponse = rapidApiClient
                    .get()
                    .uri("/article/{article_id}", articleId)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), clientResponse -> {
                        return clientResponse.bodyToMono(String.class)
                            .map(errorBody -> {
                                log.error("🔍 RapidAPI Medium 4xx error on article info: {} - {}", clientResponse.statusCode(), errorBody);
                                return new RuntimeException("Medium API article info failed: " + clientResponse.statusCode());
                            });
                    })
                    .bodyToMono(String.class)
                    .block();
                    
            // Step 2: Get article content using /article/{article_id}/content
            log.info("🌐 Step 2: Getting article content for ID: {}", articleId);
            String articleContentResponse = rapidApiClient
                    .get()
                    .uri("/article/{article_id}/content", articleId)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), clientResponse -> {
                        return clientResponse.bodyToMono(String.class)
                            .map(errorBody -> {
                                log.error("🔍 RapidAPI Medium 4xx error on article content: {} - {}", clientResponse.statusCode(), errorBody);
                                return new RuntimeException("Medium API article content failed: " + clientResponse.statusCode());
                            });
                    })
                    .bodyToMono(String.class)
                    .block();
            
            // Parse both responses
            JsonNode infoJson = objectMapper.readTree(articleInfoResponse);
            JsonNode contentJson = objectMapper.readTree(articleContentResponse);
            
            // Create Article object with combined data
            Article article = new Article(
                infoJson.path("title").asText(),
                contentJson.path("content").asText(),
                infoJson.path("url").asText()
            );
            
            // Set additional properties
            article.setAuthor("Medium Author"); // Would need to get from user ID
            article.setSource("Medium");
            
            // Parse published date if available
            String publishedAt = infoJson.path("published_at").asText();
            if (!publishedAt.isEmpty()) {
                try {
                    article.setPublishedDate(LocalDate.parse(publishedAt.substring(0, 10)));
                } catch (Exception dateEx) {
                    log.warn("Could not parse published date: {}", publishedAt);
                }
            }
            
            // Set reading time
            double readingTime = infoJson.path("reading_time").asDouble();
            article.setEstimatedReadTime((int) Math.ceil(readingTime));
            
            log.info("✅ Successfully fetched article: {}", article.getTitle());
            return article;
            
        } catch (Exception e) {
            log.error("❌ Error fetching article {}: {}", articleId, e.getMessage());
            log.info("🔄 Falling back to mock article data");
            return createMockArticleFromId(articleId);
        }
    }
    
    /**
     * Get user articles by username (convenience method)
     */
    public List<ArticleSummary> getUserArticlesByUsername(String username) {
        log.info("Fetching articles for username: {}", username);
        
        MediumUser user = getUserByUsername(username);
        return getUserArticles(user.getId());
    }
}

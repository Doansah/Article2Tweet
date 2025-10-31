package article2tweet.com.article2tweet.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Service for OpenAI integration to generate intelligent tweet content
 */
@Service
@Slf4j
public class OpenAIService {
    
    private final WebClient openAIWebClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    
    // Constants for dillondoa's casual style
    private static final int TARGET_INSIGHTS = 3;
    private static final int MAX_TWEET_LENGTH = 240; // Leave room for numbering
    
    public OpenAIService(ObjectMapper objectMapper,
                        @Value("${openai.api.key:}") String apiKey) {
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        // Don't create WebClient in constructor - create it dynamically when needed
        this.openAIWebClient = null; // Will be created in getWebClient() method
    }
    
    /**
     * Get or create WebClient with current API key
     */
    private WebClient getWebClient() {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("OpenAI API key is not configured");
        }
        
        return WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
    
    /**
     * Extract exactly 3 key insights from article content using OpenAI
     */
    public List<String> extractKeyInsights(String articleContent, String articleTitle) {
        log.info("🤖 Starting OpenAI insight extraction for article: {}", articleTitle);
        
        // Enhanced API key validation
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("❌ OpenAI API key not configured, falling back to mock insights");
            log.info("💡 To enable AI features, set OPENAI_API_KEY in your .env file");
            return createMockInsights(articleContent, articleTitle);
        }
        
        if (apiKey.startsWith("sk-") && apiKey.length() > 20) {
            log.info("✅ Valid OpenAI API key detected ({}...)", apiKey.substring(0, 8));
        } else {
            log.warn("⚠️ OpenAI API key format looks invalid, attempting anyway...");
        }
        
        String prompt = createInsightExtractionPrompt(articleContent, articleTitle);
        log.debug("📝 Generated prompt length: {} characters", prompt.length());
        
        try {
            log.info("🌐 Making OpenAI API call for insight extraction...");
            String response = callOpenAI(prompt, "gpt-3.5-turbo", 0.7, 300);
            log.info("✅ OpenAI API call successful, parsing response...");
            
            List<String> insights = parseInsightsFromResponse(response);
            
            log.info("🎉 Successfully extracted {} AI-powered insights", insights.size());
            insights.forEach(insight -> log.debug("💡 Insight: {}", insight.substring(0, Math.min(50, insight.length())) + "..."));
            return insights;
            
        } catch (Exception e) {
            log.error("❌ OpenAI API call failed: {}", e.getMessage());
            log.error("🔍 Error details: ", e);
            log.warn("🔄 Falling back to mock insights due to API error");
            return createMockInsights(articleContent, articleTitle);
        }
    }
    
    /**
     * Generate a casual, engaging hook tweet for the thread
     */
    public String generateHookTweet(String articleTitle, String firstInsight) {
        log.info("🎯 Generating AI-powered hook tweet for: {}", articleTitle);
        
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("❌ No OpenAI API key, using mock hook");
            return createMockHook(articleTitle, firstInsight);
        }
        
        String prompt = createHookPrompt(articleTitle, firstInsight);
        
        try {
            log.info("🌐 Calling OpenAI for hook generation...");
            String hook = callOpenAI(prompt, "gpt-3.5-turbo", 0.8, 150);
            String optimizedHook = optimizeForTwitter(hook.trim(), MAX_TWEET_LENGTH);
            log.info("✅ Generated hook tweet ({} chars): {}", optimizedHook.length(), 
                    optimizedHook.substring(0, Math.min(50, optimizedHook.length())) + "...");
            return optimizedHook;
            
        } catch (Exception e) {
            log.error("❌ Hook generation failed: {}", e.getMessage(), e);
            log.warn("🔄 Falling back to mock hook");
            return createMockHook(articleTitle, firstInsight);
        }
    }
    
    /**
     * Convert an insight into a casual Twitter post
     */
    public String generateCasualTweet(String insight, int tweetNumber) {
        log.info("Generating casual tweet #{}", tweetNumber);
        
        if (apiKey == null || apiKey.isEmpty()) {
            return createMockTweet(insight, tweetNumber);
        }
        
        String prompt = createCasualTweetPrompt(insight, tweetNumber);
        
        try {
            String tweet = callOpenAI(prompt, "gpt-3.5-turbo", 0.8, 150);
            return optimizeForTwitter(tweet.trim(), MAX_TWEET_LENGTH);
            
        } catch (Exception e) {
            log.error("Error generating casual tweet: {}", e.getMessage(), e);
            return createMockTweet(insight, tweetNumber);
        }
    }
    
    /**
     * Generate a wrap-up tweet with call-to-action
     */
    public String generateWrapUpTweet(String articleTitle, String articleUrl) {
        log.info("Generating wrap-up tweet for: {}", articleTitle);
        
        if (apiKey == null || apiKey.isEmpty()) {
            return String.format("That's a wrap on %s!\n\nFull article: %s\n\nThoughts?", 
                               articleTitle, articleUrl);
        }
        
        String prompt = createWrapUpPrompt(articleTitle);
        
        try {
            String wrapUp = callOpenAI(prompt, "gpt-3.5-turbo", 0.7, 100);
            // Ensure we include the article URL
            String finalTweet = wrapUp.trim() + "\n\nFull article: " + articleUrl;
            return optimizeForTwitter(finalTweet, MAX_TWEET_LENGTH);
            
        } catch (Exception e) {
            log.error("Error generating wrap-up: {}", e.getMessage(), e);
            return String.format("That's a wrap!\n\nFull article: %s\n\nThoughts?", articleUrl);
        }
    }
    
    // Private helper methods
    
    private String createInsightExtractionPrompt(String content, String title) {
        return String.format("""
                Extract exactly 3 key insights from this article that would be valuable for a casual Twitter audience.
                
                Article: "%s"
                Content: %s
                
                Requirements:
                - Focus on practical, actionable, or surprising points
                - Write in a conversational, casual tone
                - Each insight should be 1-2 sentences max
                - Make them engaging for developers and tech enthusiasts
                - Avoid buzzwords and corporate speak
                
                Return only the 3 insights, numbered 1-3:
                """, title, content);
    }
    
    private String createHookPrompt(String title, String firstInsight) {
        return String.format("""
                Create an engaging Twitter thread hook for this article.
                
                Article Title: "%s"
                First Key Point: "%s"
                
                Requirements:
                - Casual, conversational tone
                - Hook the reader immediately
                - Under 220 characters
                - No corporate buzzwords
                - Include thread emoji (🧵) at start
                - End with something that makes people want to read more
                
                Return only the hook tweet:
                """, title, firstInsight);
    }
    
    private String createCasualTweetPrompt(String insight, int tweetNumber) {
        return String.format("""
                Convert this insight into a casual Twitter post:
                
                Insight: "%s"
                Tweet Position: #%d in thread
                
                Requirements:
                - Conversational, casual tone
                - Under 220 characters
                - No corporate speak or buzzwords
                - Make it engaging and relatable
                - Add personality but stay professional
                - Use minimal emojis (max 1-2)
                
                Return only the tweet:
                """, insight, tweetNumber);
    }
    
    private String createWrapUpPrompt(String title) {
        return String.format("""
                Create a casual wrap-up tweet for this article thread:
                
                Article: "%s"
                
                Requirements:
                - Casual, friendly tone
                - Thank readers or ask for engagement
                - Under 150 characters (need room for article URL)
                - No corporate speak
                - Encourage discussion or questions
                
                Return only the wrap-up text (no URL):
                """, title);
    }
    
    private String callOpenAI(String prompt, String model, double temperature, int maxTokens) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", Arrays.asList(
            Map.of("role", "user", "content", prompt)
        ));
        requestBody.put("temperature", temperature);
        requestBody.put("max_tokens", maxTokens);
        
        log.debug("🔧 Request body: model={}, temperature={}, max_tokens={}", model, temperature, maxTokens);
        log.debug("📝 Prompt preview: {}...", prompt.substring(0, Math.min(200, prompt.length())));
        
        try {
            log.info("📡 Sending request to OpenAI API endpoint: /chat/completions");
            log.info("🔑 Using API key: {}...", apiKey.substring(0, Math.min(15, apiKey.length())));
            
            String response = getWebClient()
                    .post()
                    .uri("/chat/completions")
                    .body(Mono.just(requestBody), Map.class)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), 
                        clientResponse -> {
                            log.error("❌ OpenAI API 4xx error: {}", clientResponse.statusCode());
                            return clientResponse.bodyToMono(String.class)
                                .map(body -> {
                                    log.error("🔍 Error response body: {}", body);
                                    return new RuntimeException("OpenAI API 4xx error: " + clientResponse.statusCode() + " - " + body);
                                });
                        })
                    .onStatus(status -> status.is5xxServerError(),
                        clientResponse -> {
                            log.error("❌ OpenAI API 5xx error: {}", clientResponse.statusCode());
                            return clientResponse.bodyToMono(String.class)
                                .map(body -> {
                                    log.error("🔍 Server error response: {}", body);
                                    return new RuntimeException("OpenAI API 5xx error: " + clientResponse.statusCode() + " - " + body);
                                });
                        })
                    .bodyToMono(String.class)
                    .block();
                    
            log.info("✅ Received response from OpenAI API (length: {} chars)", response != null ? response.length() : 0);
            log.debug("📄 Raw response: {}", response);
                    
            JsonNode root = objectMapper.readTree(response);
            
            // Enhanced response parsing with error checking
            if (root.has("error")) {
                String errorMsg = root.path("error").path("message").asText();
                String errorType = root.path("error").path("type").asText();
                log.error("❌ OpenAI API returned error: {} (type: {})", errorMsg, errorType);
                throw new RuntimeException("OpenAI API error: " + errorType + " - " + errorMsg);
            }
            
            if (!root.has("choices") || root.path("choices").isEmpty()) {
                log.error("❌ OpenAI response missing 'choices' field");
                throw new RuntimeException("Invalid OpenAI response: no choices found");
            }
            
            String content = root.path("choices").get(0).path("message").path("content").asText();
            log.info("🎯 Extracted content from OpenAI (length: {} chars)", content.length());
            
            return content;
            
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
            log.error("❌ WebClient error: {} - {}", e.getStatusCode(), e.getMessage());
            log.error("🔍 Response body: {}", e.getResponseBodyAsString());
            
            if (e.getStatusCode().value() == 401) {
                throw new RuntimeException("OpenAI API authentication failed - check your API key", e);
            } else if (e.getStatusCode().value() == 429) {
                throw new RuntimeException("OpenAI API rate limit exceeded - please try again later", e);
            } else {
                throw new RuntimeException("OpenAI API call failed: " + e.getStatusCode() + " - " + e.getMessage(), e);
            }
        } catch (Exception e) {
            log.error("❌ Unexpected error during OpenAI API call: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to call OpenAI API: " + e.getMessage(), e);
        }
    }
    
    private List<String> parseInsightsFromResponse(String response) {
        List<String> insights = new ArrayList<>();
        String[] lines = response.split("\\n");
        
        for (String line : lines) {
            line = line.trim();
            if (line.matches("^\\d+\\..*")) {
                // Remove the number and period, clean up the insight
                String insight = line.replaceFirst("^\\d+\\.\\s*", "").trim();
                if (!insight.isEmpty()) {
                    insights.add(insight);
                }
            }
        }
        
        // Ensure we have exactly 3 insights
        while (insights.size() < TARGET_INSIGHTS) {
            insights.add("Key insight about the topic that provides value to readers.");
        }
        
        return insights.subList(0, TARGET_INSIGHTS);
    }
    
    private String optimizeForTwitter(String content, int maxLength) {
        if (content.length() <= maxLength) {
            return content;
        }
        
        // Try to truncate at a sentence boundary
        int lastPeriod = content.lastIndexOf('.', maxLength - 3);
        if (lastPeriod > maxLength / 2) {
            return content.substring(0, lastPeriod + 1);
        }
        
        // Truncate at word boundary
        int lastSpace = content.lastIndexOf(' ', maxLength - 3);
        if (lastSpace > 0) {
            return content.substring(0, lastSpace) + "...";
        }
        
        return content.substring(0, maxLength - 3) + "...";
    }
    
    // Mock methods for testing without API key
    
    private List<String> createMockInsights(String content, String title) {
        return Arrays.asList(
            "The key to success is finding the right balance between planning and execution.",
            "Modern tools can dramatically improve productivity when used correctly.",
            "Understanding your audience is crucial for creating valuable content."
        );
    }
    
    private String createMockHook(String title, String insight) {
        return String.format("🧵 Thread: %s\n\nJust learned something interesting about %s. Here's what caught my attention:", 
                           title, insight.toLowerCase().split("\\.")[0]);
    }
    
    private String createMockTweet(String insight, int tweetNumber) {
        return switch (tweetNumber) {
            case 2 -> "First thing that stood out: " + insight;
            case 3 -> "Another important point: " + insight;
            case 4 -> "What really matters: " + insight;
            default -> insight;
        };
    }
}

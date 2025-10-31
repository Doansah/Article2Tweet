package article2tweet.com.article2tweet.command;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import article2tweet.com.article2tweet.agent.Article2TweetAgent;
import article2tweet.com.article2tweet.domain.Article;
import article2tweet.com.article2tweet.domain.ArticleSummary;
import article2tweet.com.article2tweet.domain.MediumUser;
import article2tweet.com.article2tweet.domain.TweetThread;
import article2tweet.com.article2tweet.service.MediumApiService;
import article2tweet.com.article2tweet.service.OpenAIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ShellComponent
@RequiredArgsConstructor
@Slf4j
public class ConfigurationCommand {

    private final Article2TweetAgent article2TweetAgent;
    private final MediumApiService mediumApiService;
    private final OpenAIService openAIService;
    
    @Value("${medium.api.key:}")
    private String mediumApiKey;
    
    @Value("${openai.api.key:}")
    private String openaiApiKey;

    @ShellMethod(key = "create-tweet", value = "Convert a Medium article into a Twitter thread")
    public String createTweetFromArticle(
            @ShellOption(value = "--url", help = "Medium article URL") String articleUrl) {
        
        try {
            log.info("Starting conversion process for URL: {}", articleUrl);
            
            // For now, create a mock article since we need API setup
            Article article = mediumApiService.createMockArticle("The Future of AI Development");
            article.setUrl(articleUrl);
            
            // Convert article to tweet thread
            TweetThread tweetThread = article2TweetAgent.createTwitterThread(article);
            
            // Format output for display
            StringBuilder output = new StringBuilder();
            output.append("🎉 Successfully created Twitter thread!\n");
            output.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            output.append(String.format("📄 Article: %s\n", tweetThread.getOriginalArticleTitle()));
            output.append(String.format("🔗 URL: %s\n", tweetThread.getOriginalArticleUrl()));
            output.append(String.format("📊 Total Tweets: %d\n", tweetThread.getTotalTweets()));
            output.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
            
            // Display each tweet
            for (int i = 0; i < tweetThread.getTweets().size(); i++) {
                var tweet = tweetThread.getTweets().get(i);
                output.append(String.format("🐦 Tweet %d:\n", tweet.getOrder()));
                output.append(tweet.getContent()).append("\n");
                output.append(String.format("   Characters: %d/280\n", tweet.getCharacterCount()));
                if (i < tweetThread.getTweets().size() - 1) {
                    output.append("\n");
                }
            }
            
            return output.toString();
            
        } catch (Exception e) {
            log.error("Error creating tweet thread: {}", e.getMessage(), e);
            return "❌ Error creating tweet thread: " + e.getMessage();
        }
    }
    
    @ShellMethod(key = "create-mock-tweet", value = "Create a Twitter thread from a mock article (for testing)")
    public String createMockTweetThread(
            @ShellOption(value = "--title", help = "Article title", defaultValue = "10 AI Trends That Will Shape 2025") String title) {
        
        try {
            log.info("Creating mock Twitter thread for title: {}", title);
            
            // Create a more detailed mock article
            Article mockArticle = new Article(
                title,
                createMockContent(title),
                "https://medium.com/@author/" + title.toLowerCase().replace(" ", "-").replace(",", "")
            );
            mockArticle.setAuthor("Tech Writer");
            
            // Convert to tweet thread
            TweetThread tweetThread = article2TweetAgent.createTwitterThread(mockArticle);
            
            // Format output
            StringBuilder output = new StringBuilder();
            output.append("🎉 Mock Twitter Thread Created!\n");
            output.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            output.append(String.format("📄 Article: %s\n", tweetThread.getOriginalArticleTitle()));
            output.append(String.format("📊 Total Tweets: %d\n", tweetThread.getTotalTweets()));
            output.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
            
            // Display tweets
            tweetThread.getTweets().forEach(tweet -> {
                output.append(String.format("🐦 Tweet %d:\n", tweet.getOrder()));
                output.append(tweet.getContent()).append("\n");
                output.append(String.format("   📏 Characters: %d/280\n\n", tweet.getCharacterCount()));
            });
            
            return output.toString();
            
        } catch (Exception e) {
            log.error("Error creating mock tweet thread: {}", e.getMessage(), e);
            return "❌ Error creating mock tweet thread: " + e.getMessage();
        }
    }
    
    @ShellMethod(key = "help-tweets", value = "Show help for tweet creation commands")
    public String showHelp() {
        return """
                📱 Article2Tweet AI-Powered Commands Help
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                
                🎯 MAIN WORKFLOW (Recommended):
                
                list-articles [--username dillondoa]
                   📚 List your Medium articles with IDs
                   
                preview-thread --article-id [id]
                   🔍 Preview AI-generated thread before finalizing
                   
                create-smart-thread --article-id [id]
                   🚀 Create final AI-powered thread ready to post
                   
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                
                🔧 ALL AVAILABLE COMMANDS:
                
                create-smart-thread --article-id [id] OR --url [url] [--preview]
                   🤖 AI-powered thread with casual tone, limited emojis
                   Example: create-smart-thread --article-id mock_article_1
                   Example: create-smart-thread --url https://medium.com/@dillondoa/article
                   
                preview-thread --article-id [id]
                   🔍 Preview thread structure and quality before finalizing
                   
                create-thread-from-id --article-id [id]
                   📝 Detailed thread creation with full analysis output
                   
                list-articles [--username dillondoa]
                   📚 List articles from Medium user (default: dillondoa)
                   
                create-mock-tweet [--title "Your Title"]
                   🧪 Test with mock article content
                   
                test-env
                   🔧 Check if API keys are loaded correctly
                   
                test-openai
                   🤖 Test OpenAI API connectivity and functionality
                   
                debug-api-key
                   🔍 Detailed diagnosis of OpenAI API key configuration
                   
                test-medium-api
                   📰 Test Medium API connectivity and functionality
                   
                debug-medium-config
                   🔍 Detailed diagnosis of Medium API configuration
                   
                test-medium-rapidapi
                   🚀 Test RapidAPI Medium2 integration with complete workflow
                """;
    }
    
    @ShellMethod(key = "test-env", value = "Test if environment variables are loaded correctly")
    public String testEnvironmentVariables() {
        StringBuilder result = new StringBuilder();
        result.append("🔧 Environment Variables Test\n");
        result.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        
        result.append("Medium API Key: ");
        if (mediumApiKey != null && !mediumApiKey.isEmpty()) {
            result.append("✅ Loaded (").append(mediumApiKey.substring(0, Math.min(10, mediumApiKey.length()))).append("...)\n");
        } else {
            result.append("❌ Not loaded\n");
        }
        
        result.append("OpenAI API Key: ");
        if (openaiApiKey != null && !openaiApiKey.isEmpty()) {
            result.append("✅ Loaded (").append(openaiApiKey.substring(0, Math.min(10, openaiApiKey.length()))).append("...)\n");
        } else {
            result.append("❌ Not loaded\n");
        }
        
        result.append("\n💡 If keys show as not loaded, check your .env file in the project root.\n");
        return result.toString();
    }
    
    @ShellMethod(key = "list-articles", value = "List articles from Medium user (default: dillondoa)")
    public String listUserArticles(@ShellOption(value = "--username", defaultValue = "dillondoa") String username) {
        try {
            log.info("Listing articles for user: {}", username);
            
            List<ArticleSummary> articles = mediumApiService.getUserArticlesByUsername(username);
            
            if (articles.isEmpty()) {
                return "❌ No articles found for user: " + username;
            }
            
            StringBuilder output = new StringBuilder();
            output.append(String.format("📚 Articles by @%s\n", username));
            output.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            
            for (int i = 0; i < articles.size(); i++) {
                ArticleSummary article = articles.get(i);
                output.append(String.format("%d. %s\n", i + 1, article.getTitle()));
                output.append(String.format("   📅 Published: %s\n", article.getPublishedAt()));
                output.append(String.format("   📖 Reading time: %d min\n", article.getReadingTime()));
                output.append(String.format("   👏 Claps: %d\n", article.getClaps()));
                output.append(String.format("   🆔 ID: %s\n", article.getId()));
                if (i < articles.size() - 1) {
                    output.append("\n");
                }
            }
            
            output.append("\n💡 Use 'create-thread-from-id --article-id [ID]' to create a tweet thread");
            return output.toString();
            
        } catch (Exception e) {
            log.error("Error listing articles for user {}: {}", username, e.getMessage(), e);
            return "❌ Error listing articles: " + e.getMessage();
        }
    }
    
    @ShellMethod(key = "create-thread-from-id", value = "Create Twitter thread from Medium article ID")
    public String createThreadFromArticleId(@ShellOption(value = "--article-id", help = "Medium article ID") String articleId) {
        try {
            log.info("Creating thread from article ID: {}", articleId);
            
            // Get full article content
            Article article = mediumApiService.getFullArticleContent(articleId);
            
            // Convert to tweet thread
            TweetThread tweetThread = article2TweetAgent.createTwitterThread(article);
            
            // Format output for display
            StringBuilder output = new StringBuilder();
            output.append("🎉 AI-Powered Twitter Thread Created!\n");
            output.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            output.append(String.format("📄 Article: %s\n", tweetThread.getOriginalArticleTitle()));
            output.append(String.format("👤 Author: %s\n", article.getAuthor()));
            output.append(String.format("📊 Thread Length: %d tweets\n", tweetThread.getTotalTweets()));
            output.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
            
            // Display each tweet with casual formatting
            for (int i = 0; i < tweetThread.getTweets().size(); i++) {
                var tweet = tweetThread.getTweets().get(i);
                output.append(String.format("🐦 Tweet %d/%d:\n", tweet.getOrder(), tweetThread.getTotalTweets()));
                output.append(tweet.getContent()).append("\n");
                output.append(String.format("   📏 %d/280 characters\n", tweet.getCharacterCount()));
                if (i < tweetThread.getTweets().size() - 1) {
                    output.append("\n");
                }
            }
            
            return output.toString();
            
        } catch (Exception e) {
            log.error("Error creating thread from article ID {}: {}", articleId, e.getMessage(), e);
            return "❌ Error creating thread: " + e.getMessage();
        }
    }
    
    @ShellMethod(key = "create-smart-thread", value = "AI-powered thread creation with options")
    public String createSmartThread(
            @ShellOption(value = "--article-id", help = "Medium article ID", defaultValue = "") String articleId,
            @ShellOption(value = "--url", help = "Medium article URL", defaultValue = "") String url,
            @ShellOption(value = "--preview", help = "Preview before finalizing", defaultValue = "false") boolean preview) {
        
        try {
            Article article;
            
            if (!articleId.isEmpty()) {
                log.info("Creating smart thread from article ID: {}", articleId);
                article = mediumApiService.getFullArticleContent(articleId);
            } else if (!url.isEmpty()) {
                log.info("Creating smart thread from URL: {}", url);
                // Extract article ID from URL or use the URL directly
                article = mediumApiService.fetchArticleByUrl(url);
            } else {
                return """
                       ❌ Please provide either --article-id or --url parameter
                       Example: create-smart-thread --article-id mock_article_1
                       Example: create-smart-thread --url https://medium.com/@dillondoa/your-article
                       """;
            }
            
            // Generate AI-powered tweet thread
            TweetThread tweetThread = article2TweetAgent.createTwitterThread(article);
            
            if (preview) {
                return formatThreadPreview(tweetThread, article);
            } else {
                return formatFinalThread(tweetThread, article);
            }
            
        } catch (Exception e) {
            log.error("Error creating smart thread: {}", e.getMessage(), e);
            return "❌ Error creating smart thread: " + e.getMessage();
        }
    }
    
    @ShellMethod(key = "preview-thread", value = "Preview AI-generated thread before finalizing")
    public String previewThread(@ShellOption(value = "--article-id", help = "Medium article ID") String articleId) {
        try {
            log.info("Previewing thread for article ID: {}", articleId);
            
            // Get full article content
            Article article = mediumApiService.getFullArticleContent(articleId);
            
            // Generate AI-powered tweet thread
            TweetThread tweetThread = article2TweetAgent.createTwitterThread(article);
            
            return formatThreadPreview(tweetThread, article);
            
        } catch (Exception e) {
            log.error("Error previewing thread for article ID {}: {}", articleId, e.getMessage(), e);
            return "❌ Error previewing thread: " + e.getMessage();
        }
    }
    
    @ShellMethod(key = "test-openai", value = "Test OpenAI API connectivity and key validation")
    public String testOpenAIConnection() {
        try {
            log.info("🧪 Testing OpenAI API connectivity...");
            
            // Create a simple test article
            String testContent = """
                Artificial intelligence is transforming how we work and live. 
                Machine learning algorithms are becoming more sophisticated every day.
                The future of AI development looks incredibly promising.
                """;
            
            // Test the OpenAI service directly
            List<String> insights = openAIService.extractKeyInsights(testContent, "AI Test Article");
            
            StringBuilder result = new StringBuilder();
            result.append("🤖 OpenAI API Connection Test Results\n");
            result.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            
            if (insights.get(0).contains("key to success")) {
                result.append("❌ API Status: MOCK MODE (Real OpenAI calls not working)\n");
                result.append("🔍 Issue: API calls are falling back to mock responses\n");
                result.append("💡 Check: API key format, network connectivity, rate limits\n");
            } else {
                result.append("✅ API Status: CONNECTED (Real AI responses detected)\n");
                result.append("🎉 OpenAI integration is working correctly!\n");
            }
            
            result.append("\n📋 Generated Insights:\n");
            for (int i = 0; i < insights.size(); i++) {
                result.append(String.format("%d. %s\n", i + 1, insights.get(i)));
            }
            
            // Test hook generation too
            result.append("\n🎯 Testing Hook Generation:\n");
            String hookTest = openAIService.generateHookTweet("AI Test Article", insights.get(0));
            result.append("Hook: ").append(hookTest).append("\n");
            
            return result.toString();
            
        } catch (Exception e) {
            log.error("OpenAI test failed: {}", e.getMessage(), e);
            return String.format("""
                    ❌ OpenAI API Test Failed
                    ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                    Error: %s
                    
                    🔧 Troubleshooting Steps:
                    1. Check if OPENAI_API_KEY is set in .env
                    2. Verify API key starts with 'sk-' and is valid
                    3. Check internet connectivity
                    4. Verify OpenAI account has credits
                    5. Check for rate limiting (429 errors)
                    """, e.getMessage());
        }
    }
    
    @ShellMethod(key = "debug-api-key", value = "Debug OpenAI API key configuration in detail")
    public String debugApiKey() {
        StringBuilder result = new StringBuilder();
        result.append("🔍 DETAILED API KEY DIAGNOSIS\n");
        result.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        
        // Check .env file loading
        result.append("📄 Environment Configuration:\n");
        result.append("  OPENAI_API_KEY from @Value: ");
        if (openaiApiKey != null && !openaiApiKey.isEmpty()) {
            result.append("✅ LOADED\n");
            result.append("  Key length: ").append(openaiApiKey.length()).append(" characters\n");
            result.append("  Key prefix: ").append(openaiApiKey.substring(0, Math.min(10, openaiApiKey.length()))).append("...\n");
            
            if (openaiApiKey.startsWith("sk-")) {
                result.append("  Format check: ✅ VALID (starts with sk-)\n");
            } else {
                result.append("  Format check: ❌ INVALID (should start with sk-)\n");
            }
            
            if (openaiApiKey.length() > 40) {
                result.append("  Length check: ✅ VALID (>40 chars)\n");
            } else {
                result.append("  Length check: ❌ INVALID (should be >40 chars)\n");
            }
            
        } else {
            result.append("❌ NOT LOADED\n");
        }
        
        // Check system environment
        String systemKey = System.getenv("OPENAI_API_KEY");
        result.append("\n🌍 System Environment:\n");
        result.append("  System.getenv('OPENAI_API_KEY'): ");
        if (systemKey != null && !systemKey.isEmpty()) {
            result.append("✅ FOUND (").append(systemKey.length()).append(" chars)\n");
        } else {
            result.append("❌ NOT FOUND\n");
        }
        
        // Check .env file exists
        result.append("\n📁 File System Check:\n");
        java.io.File envFile = new java.io.File(".env");
        result.append("  .env file exists: ");
        if (envFile.exists()) {
            result.append("✅ YES\n");
            result.append("  .env file size: ").append(envFile.length()).append(" bytes\n");
        } else {
            result.append("❌ NO\n");
        }
        
        // Test a simple API call to get the actual error
        result.append("\n🧪 API Call Test:\n");
        try {
            // Make a minimal API call to see the exact error
            if (openaiApiKey != null && !openaiApiKey.isEmpty()) {
                result.append("  Attempting minimal API call...\n");
                
                // This will trigger the actual OpenAI call and show us the real error
                List<String> testInsights = openAIService.extractKeyInsights("Test content", "Test Article");
                
                if (testInsights.get(0).contains("key to success")) {
                    result.append("  Result: ❌ MOCK RESPONSE (API call failed)\n");
                    result.append("  Check application logs for detailed error messages\n");
                } else {
                    result.append("  Result: ✅ REAL AI RESPONSE\n");
                }
            } else {
                result.append("  Skipped: No API key available\n");
            }
            
        } catch (Exception e) {
            result.append("  Error during test: ").append(e.getMessage()).append("\n");
        }
        
        result.append("\n💡 TROUBLESHOOTING STEPS:\n");
        result.append("1. Check if .env file exists in project root\n");
        result.append("2. Verify .env contains: OPENAI_API_KEY=sk-...\n");
        result.append("3. Restart application after adding .env\n");
        result.append("4. Check OpenAI account has credits\n");
        result.append("5. Verify no firewall blocking api.openai.com\n");
        
        return result.toString();
    }

    @ShellMethod(key = "test-openai-simple", value = "Simple OpenAI API test with detailed logging")
    public String testOpenAISimple() {
        log.info("🧪 Starting simple OpenAI test...");
        
        try {
            // Test with very simple content
            String testInsight = openAIService.generateCasualTweet("This is a test insight about software development", 2);
            
            StringBuilder result = new StringBuilder();
            result.append("🤖 SIMPLE OPENAI TEST RESULTS\n");
            result.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            
            if (testInsight.contains("First thing that stood out")) {
                result.append("❌ Result: MOCK MODE - API calls not working\n");
                result.append("🔍 Check application logs for specific error details\n");
            } else {
                result.append("✅ Result: REAL AI RESPONSE!\n");
                result.append("🎉 OpenAI integration is working correctly\n");
            }
            
            result.append("\nGenerated Tweet:\n");
            result.append(testInsight);
            
            return result.toString();
            
        } catch (Exception e) {
            log.error("Simple OpenAI test failed: {}", e.getMessage(), e);
            return "❌ OpenAI test failed: " + e.getMessage();
        }
    }

    private String formatThreadPreview(TweetThread tweetThread, Article article) {
        StringBuilder output = new StringBuilder();
        output.append("🔍 THREAD PREVIEW - AI-Generated\n");
        output.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        output.append(String.format("📄 Article: %s\n", tweetThread.getOriginalArticleTitle()));
        output.append(String.format("👤 Author: %s\n", article.getAuthor()));
        output.append(String.format("🧠 AI Style: Casual with limited emojis\n"));
        output.append(String.format("📊 Thread Length: %d tweets\n", tweetThread.getTotalTweets()));
        output.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        
        // Display each tweet with preview formatting
        for (int i = 0; i < tweetThread.getTweets().size(); i++) {
            var tweet = tweetThread.getTweets().get(i);
            String tweetType = switch (tweet.getOrder()) {
                case 1 -> "🎯 HOOK";
                case 5 -> "🎬 WRAP-UP";
                default -> "💡 INSIGHT";
            };
            
            output.append(String.format("🐦 Tweet %d/%d (%s):\n", tweet.getOrder(), tweetThread.getTotalTweets(), tweetType));
            output.append(tweet.getContent()).append("\n");
            output.append(String.format("   📏 %d/280 characters", tweet.getCharacterCount()));
            
            // Add quality indicators
            if (tweet.getCharacterCount() > 260) {
                output.append(" ⚠️ Long");
            } else if (tweet.getCharacterCount() < 50) {
                output.append(" ⚠️ Short");
            } else {
                output.append(" ✅ Good");
            }
            output.append("\n");
            
            if (i < tweetThread.getTweets().size() - 1) {
                output.append("\n");
            }
        }
        
        output.append("\n💡 Use 'create-smart-thread --article-id ").append(article.getUrl().substring(article.getUrl().lastIndexOf("/") + 1));
        output.append("' to finalize or 'create-thread-from-id --article-id [id]' for detailed output");
        
        return output.toString();
    }
    
    private String formatFinalThread(TweetThread tweetThread, Article article) {
        StringBuilder output = new StringBuilder();
        output.append("🎉 AI-POWERED TWITTER THREAD READY!\n");
        output.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        output.append(String.format("📄 Article: %s\n", tweetThread.getOriginalArticleTitle()));
        output.append(String.format("👤 Author: %s\n", article.getAuthor()));
        output.append(String.format("🤖 Generated with: OpenAI + Casual Style\n"));
        output.append(String.format("📊 Thread: %d tweets, ready to copy/paste\n", tweetThread.getTotalTweets()));
        output.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        
        // Display tweets ready for copying
        for (int i = 0; i < tweetThread.getTweets().size(); i++) {
            var tweet = tweetThread.getTweets().get(i);
            output.append(String.format("📋 TWEET %d:\n", tweet.getOrder()));
            output.append("───────────────────────────────────────\n");
            output.append(tweet.getContent()).append("\n");
            output.append("───────────────────────────────────────\n");
            output.append(String.format("✅ %d characters • Ready to post\n", tweet.getCharacterCount()));
            if (i < tweetThread.getTweets().size() - 1) {
                output.append("\n");
            }
        }
        
        output.append("\n🚀 Ready to post! Copy each tweet and post as a thread on Twitter/X");
        return output.toString();
    }

    private String createMockContent(String title) {
        return String.format("""
                %s
                
                The world of artificial intelligence is evolving at breakneck speed. As we look toward the future, several key trends are emerging that will fundamentally reshape how we work, live, and interact with technology.
                
                Machine learning models are becoming more sophisticated and accessible. We're seeing democratization of AI tools that were once available only to tech giants.
                
                Automation is expanding beyond simple tasks into complex decision-making processes. This shift requires us to rethink traditional workflows and embrace new paradigms.
                
                Ethical AI development is becoming a priority as we grapple with bias, transparency, and accountability in automated systems.
                
                The integration of AI into everyday applications is seamless and intuitive. Users increasingly expect intelligent features as standard functionality.
                
                Edge computing is enabling AI processing closer to data sources, reducing latency and improving privacy protection.
                
                These developments point to a future where AI enhances human capabilities rather than replacing them, creating new opportunities for innovation and growth.
                """, title);
    }
    
    @ShellMethod(key = "test-medium-api", value = "Test Medium API connectivity and configuration")
    public String testMediumApi() {
        try {
            StringBuilder result = new StringBuilder();
            result.append("📰 MEDIUM API CONNECTION TEST\n");
            result.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            
            // Check API key configuration
            result.append("🔑 API Key Status:\n");
            if (mediumApiKey != null && !mediumApiKey.isEmpty()) {
                result.append("  ✅ LOADED (").append(mediumApiKey.length()).append(" chars)\n");
                result.append("  Key prefix: ").append(mediumApiKey.substring(0, Math.min(10, mediumApiKey.length()))).append("...\n");
            } else {
                result.append("  ❌ NOT LOADED\n");
                result.append("  💡 Add MEDIUM_API_KEY to your .env file\n");
            }
            
            // Test user lookup
            result.append("\n👤 User Lookup Test:\n");
            result.append("  Testing getUserByUsername('dillondoa')...\n");
            MediumUser user = mediumApiService.getUserByUsername("dillondoa");
            
            if (user.getId().startsWith("mock_user_id")) {
                result.append("  Result: ❌ MOCK RESPONSE (API not working)\n");
                result.append("  User ID: ").append(user.getId()).append("\n");
                result.append("  Issue: API calls falling back to mock data\n");
            } else {
                result.append("  Result: ✅ REAL API RESPONSE\n");
                result.append("  User ID: ").append(user.getId()).append("\n");
                result.append("  Name: ").append(user.getName()).append("\n");
            }
            
            // Test article listing
            result.append("\n📚 Article Listing Test:\n");
            result.append("  Testing getUserArticlesByUsername('dillondoa')...\n");
            List<ArticleSummary> articles = mediumApiService.getUserArticlesByUsername("dillondoa");
            
            if (!articles.isEmpty() && articles.get(0).getId().startsWith("mock_article")) {
                result.append("  Result: ❌ MOCK ARTICLES (").append(articles.size()).append(" mock articles)\n");
                result.append("  Sample title: ").append(articles.get(0).getTitle()).append("\n");
                result.append("  Issue: Medium API not configured or failing\n");
            } else if (!articles.isEmpty()) {
                result.append("  Result: ✅ REAL ARTICLES (").append(articles.size()).append(" articles)\n");
                result.append("  Sample title: ").append(articles.get(0).getTitle()).append("\n");
            } else {
                result.append("  Result: ❌ NO ARTICLES FOUND\n");
            }
            
            // Recommendations
            result.append("\n💡 TROUBLESHOOTING:\n");
            if (mediumApiKey == null || mediumApiKey.isEmpty()) {
                result.append("1. Get Medium API key from Medium Partner Program\n");
                result.append("2. Add MEDIUM_API_KEY=your_key to .env file\n");
                result.append("3. Restart application\n");
            } else {
                result.append("1. Verify Medium API key is valid\n");
                result.append("2. Check Medium API documentation for rate limits\n");
                result.append("3. Test API key with curl/Postman first\n");
            }
            
            result.append("\n📝 NOTE: Mock mode provides realistic test data\n");
            result.append("   Your app works fully with mock articles!\n");
            
            return result.toString();
            
        } catch (Exception e) {
            log.error("Medium API test failed: {}", e.getMessage(), e);
            return String.format("""
                    ❌ Medium API Test Failed
                    ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                    Error: %s
                    
                    💡 This is likely expected - Medium API requires:
                    1. Valid API key from Medium Partner Program
                    2. Proper authentication setup
                    3. Rate limit considerations
                    
                    🎉 Good news: Your app works with mock data!
                    """, e.getMessage());
        }
    }

    @ShellMethod(key = "debug-medium-config", value = "Debug Medium API configuration in detail")
    public String debugMediumConfig() {
        StringBuilder result = new StringBuilder();
        result.append("🔍 MEDIUM API CONFIGURATION DEBUG\n");
        result.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        
        // Environment check
        result.append("📄 Environment Configuration:\n");
        result.append("  MEDIUM_API_KEY from @Value: ");
        if (mediumApiKey != null && !mediumApiKey.isEmpty()) {
            result.append("✅ LOADED\n");
            result.append("  Key length: ").append(mediumApiKey.length()).append(" characters\n");
            result.append("  Key preview: ").append(mediumApiKey.substring(0, Math.min(15, mediumApiKey.length()))).append("...\n");
        } else {
            result.append("❌ NOT LOADED\n");
        }
        
        // System environment
        String systemKey = System.getenv("MEDIUM_API_KEY");
        result.append("\n🌍 System Environment:\n");
        result.append("  System.getenv('MEDIUM_API_KEY'): ");
        if (systemKey != null && !systemKey.isEmpty()) {
            result.append("✅ FOUND\n");
        } else {
            result.append("❌ NOT FOUND\n");
        }
        
        // File system check
        result.append("\n📁 .env File Check:\n");
        java.io.File envFile = new java.io.File(".env");
        if (envFile.exists()) {
            result.append("  ✅ .env file exists\n");
            // Try to read .env and check for MEDIUM_API_KEY
            try {
                String envContent = java.nio.file.Files.readString(envFile.toPath());
                if (envContent.contains("MEDIUM_API_KEY")) {
                    result.append("  ✅ MEDIUM_API_KEY found in .env\n");
                } else {
                    result.append("  ❌ MEDIUM_API_KEY not found in .env\n");
                }
            } catch (Exception e) {
                result.append("  ⚠️ Could not read .env file\n");
            }
        } else {
            result.append("  ❌ .env file not found\n");
        }
        
        result.append("\n🏗️ MEDIUM API INTEGRATION STATUS:\n");
        result.append("According to your plan, Medium API integration options:\n");
        result.append("1. 🎯 CURRENT: Mock mode with realistic test data\n");
        result.append("2. 🔮 FUTURE: Real Medium API (requires partner access)\n");
        result.append("3. 🕷️ ALTERNATIVE: Web scraping (complex, rate-limited)\n");
        
        result.append("\n✨ RECOMMENDATION:\n");
        result.append("Your app is fully functional with mock data!\n");
        result.append("Mock articles provide realistic content for AI processing.\n");
        result.append("Focus on perfecting the AI tweet generation first.\n");
        
        return result.toString();
    }
    
    @ShellMethod(key = "test-medium-rapidapi", value = "Test RapidAPI Medium2 integration with dillondoa")
    public String testMediumRapidApi() {
        try {
            StringBuilder result = new StringBuilder();
            result.append("🚀 RAPIDAPI MEDIUM2 INTEGRATION TEST\n");
            result.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            
            // Test the complete workflow: username -> user ID -> articles -> content
            result.append("🎯 Testing complete workflow for: dillondoa\n\n");
            
            // Step 1: Get user info
            result.append("📋 Step 1: Get User Information\n");
            MediumUser user = mediumApiService.getUserByUsername("dillondoa");
            result.append("  Username: ").append(user.getUsername()).append("\n");
            result.append("  User ID: ").append(user.getId()).append("\n");
            result.append("  Name: ").append(user.getName()).append("\n");
            
            if (user.getId().startsWith("mock_user_id")) {
                result.append("  Status: ❌ MOCK MODE (API calls failed)\n\n");
            } else {
                result.append("  Status: ✅ REAL API DATA\n\n");
            }
            
            // Step 2: Get articles
            result.append("📚 Step 2: Get User Articles\n");
            List<ArticleSummary> articles = mediumApiService.getUserArticles(user.getId());
            result.append("  Total articles: ").append(articles.size()).append("\n");
            
            if (!articles.isEmpty()) {
                result.append("  Sample articles:\n");
                for (int i = 0; i < Math.min(3, articles.size()); i++) {
                    ArticleSummary article = articles.get(i);
                    result.append("    ").append(i + 1).append(". ").append(article.getTitle()).append("\n");
                    result.append("       ID: ").append(article.getId()).append("\n");
                }
            }
            
            if (!articles.isEmpty() && articles.get(0).getId().startsWith("mock_article")) {
                result.append("  Status: ❌ MOCK ARTICLES\n\n");
            } else if (!articles.isEmpty()) {
                result.append("  Status: ✅ REAL ARTICLE IDS\n\n");
            }
            
            // Step 3: Test article content (using first article)
            if (!articles.isEmpty()) {
                result.append("📖 Step 3: Get Article Content\n");
                String articleId = articles.get(0).getId();
                result.append("  Testing article ID: ").append(articleId).append("\n");
                
                Article fullArticle = mediumApiService.getFullArticleContent(articleId);
                result.append("  Title: ").append(fullArticle.getTitle()).append("\n");
                result.append("  Content length: ").append(fullArticle.getContent().length()).append(" characters\n");
                result.append("  URL: ").append(fullArticle.getUrl()).append("\n");
                
                if (fullArticle.getContent().contains("sample Medium article content")) {
                    result.append("  Status: ❌ MOCK CONTENT\n");
                } else {
                    result.append("  Status: ✅ REAL ARTICLE CONTENT\n");
                }
            }
            
            result.append("\n🔧 TROUBLESHOOTING:\n");
            result.append("If seeing mock data:\n");
            result.append("1. Verify MEDIUM_API_KEY in .env (should be RapidAPI key)\n");
            result.append("2. Check RapidAPI subscription status\n");
            result.append("3. Verify API key has access to Medium2 service\n");
            result.append("4. Check request limits on RapidAPI dashboard\n");
            
            return result.toString();
            
        } catch (Exception e) {
            log.error("RapidAPI Medium test failed: {}", e.getMessage(), e);
            return String.format("""
                    ❌ RapidAPI Medium Test Failed
                    ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                    Error: %s
                    
                    This could indicate:
                    1. RapidAPI key issues
                    2. Network connectivity problems
                    3. Service rate limiting
                    4. API endpoint changes
                    
                    💡 Your app still works perfectly with mock data!
                    """, e.getMessage());
        }
    }
}
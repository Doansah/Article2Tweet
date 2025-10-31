package article2tweet.com.article2tweet.command;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import article2tweet.com.article2tweet.agent.Article2TweetAgent;
import article2tweet.com.article2tweet.domain.Article;
import article2tweet.com.article2tweet.domain.TweetThread;
import article2tweet.com.article2tweet.service.MediumApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ShellComponent
@RequiredArgsConstructor
@Slf4j
public class ConfigurationCommand {

    private final Article2TweetAgent article2TweetAgent;
    private final MediumApiService mediumApiService;
    
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
                📱 Article2Tweet Commands Help
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                
                🔧 Available Commands:
                
                create-mock-tweet [--title "Your Title"]
                   Creates a Twitter thread from a mock article for testing
                   Example: create-mock-tweet --title "AI in Healthcare"
                
                create-tweet --url "https://medium.com/article-url"
                   Converts a real Medium article to Twitter thread
                   (Requires Medium API setup)
                
                help-tweets
                   Shows this help message
                
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                
                💡 Pro Tips:
                • Start with create-mock-tweet to test the functionality
                • Each thread includes intro, key points, and call-to-action
                • Tweets are automatically formatted to stay under 280 characters
                • Thread numbering is added automatically
                
                🔑 Setup Required for Real Articles:
                • Get API key from https://rapidapi.com/nishujain199719-vgIfuFHZxVZ/api/medium2
                • Add to application.properties: medium.api.key=your_key_here
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
}
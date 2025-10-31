# Article2Tweet - AI-Powered Twitter Thread Generator

An AI-powered application built with **Embabel Agent Framework** that converts Medium articles into engaging Twitter threads.

## 🚀 Features

- **Smart Article Analysis**: Extracts key points from Medium articles
- **Twitter Thread Generation**: Creates properly formatted tweet threads with character limits
- **Interactive Shell Interface**: Easy-to-use Spring Shell commands
- **Mock API Support**: Test functionality without requiring API keys
- **Extensible Architecture**: Built with Embabel for advanced AI agent capabilities

## 🛠️ Tech Stack

- **Java 21** - Core language
- **Spring Boot 3.5.7** - Application framework  
- **Spring Shell** - Interactive command-line interface
- **Embabel Agent Framework 0.1.3** - AI agent orchestration
- **Lombok** - Reduces boilerplate code
- **Jackson** - JSON processing
- **Maven** - Build tool

## 📋 Prerequisites

- Java 21 or higher
- Maven 3.9+ (or use included Maven wrapper)
- Optional: Medium API key from [RapidAPI](https://rapidapi.com/nishujain199719-vgIfuFHZxVZ/api/medium2)

## ⚡ Quick Start

### 1. Clone and Build
```bash
git clone <your-repo-url>
cd article2tweet
./mvnw clean compile
```

### 2. Run the Application
```bash
./mvnw spring-boot:run
```

### 3. Use Interactive Commands
Once the application starts, you'll see a Spring Shell prompt. Try these commands:

```bash
# Create a mock Twitter thread for testing
create-mock-tweet --title "10 AI Trends That Will Shape 2025"

# Create thread from a Medium URL (requires API setup)
create-tweet --url "https://medium.com/@author/article-title"

# Show help
help-tweets
```

## 🎯 Available Commands

| Command | Description | Example |
|---------|-------------|---------|
| `create-mock-tweet` | Generate thread from mock article | `create-mock-tweet --title "AI in Healthcare"` |
| `create-tweet` | Convert real Medium article to thread | `create-tweet --url "https://medium.com/article-url"` |
| `help-tweets` | Show detailed help | `help-tweets` |

## 🔧 Configuration

### Medium API Setup (Optional)
1. Get API key from [RapidAPI Medium API](https://rapidapi.com/nishujain199719-vgIfuFHZxVZ/api/medium2)
2. Set environment variable:
   ```bash
   export MEDIUM_API_KEY="your_api_key_here"
   ```
3. Or add to `application.properties`:
   ```properties
   medium.api.key=your_api_key_here
   ```

### AI Configuration (Future)
When Embabel is fully configured, you can add:
```properties
openai.api.key=${OPENAI_API_KEY:}
anthropic.api.key=${ANTHROPIC_API_KEY:}
```

## 📁 Project Structure

```
article2tweet/
├── src/main/java/article2tweet/com/article2tweet/
│   ├── Article2tweetApplication.java          # Main application
│   ├── agent/
│   │   └── Article2TweetAgent.java            # Core AI agent
│   ├── command/
│   │   └── ConfigurationCommand.java          # Shell commands
│   ├── config/
│   │   └── ApplicationConfig.java             # Configuration
│   ├── domain/
│   │   ├── Article.java                       # Article model
│   │   ├── Tweet.java                         # Tweet model
│   │   └── TweetThread.java                   # Thread model
│   └── service/
│       └── MediumApiService.java              # API service
├── src/main/resources/
│   └── application.properties                 # App configuration
└── pom.xml                                    # Maven configuration
```

## 🧠 How It Works

1. **Article Input**: Accepts Medium article URL or uses mock content
2. **Content Analysis**: Extracts key points from article content
3. **Thread Generation**: 
   - Creates introduction tweet with article title
   - Converts key points to individual tweets
   - Adds thread numbering (1/n, 2/n, etc.)
   - Ensures tweets stay under 280 characters
   - Adds call-to-action with original article link
4. **Output**: Displays formatted Twitter thread ready for posting

## 🎨 Example Output

```
🎉 Successfully created Twitter thread!
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📄 Article: 10 AI Trends That Will Shape 2025
🔗 URL: https://medium.com/@author/10-ai-trends-that-will-shape-2025
📊 Total Tweets: 8
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🐦 Tweet 1:
🧵 Thread: 10 AI Trends That Will Shape 2025 by Tech Writer

Key insights 👇
Characters: 89/280

🐦 Tweet 2:
(2/8) The world of artificial intelligence is evolving at breakneck speed...
Characters: 123/280
```

## 🔮 Future Enhancements

- **Full Embabel Integration**: Complete AI agent with LLM-powered content analysis
- **Advanced Thread Strategies**: Different threading approaches based on content type  
- **Social Media Integration**: Direct posting to Twitter/X
- **Content Optimization**: A/B testing for engagement
- **Multi-Platform Support**: LinkedIn, Threads, Mastodon
- **Analytics**: Track thread performance

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [Embabel Agent Framework](https://docs.embabel.com/) - AI agent orchestration
- [Spring Boot](https://spring.io/projects/spring-boot) - Application framework
- [Medium API](https://mediumapi.com/) - Article content access

---

**Happy Threading! 🐦✨**

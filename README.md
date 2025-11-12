# Article2Tweet - AI-Powered Twitter Thread Generator

Hi, this is Article2Tweet, it uses the Spring Shell CLI and it gives you the ability to generate Tweets, from Articles!
I write Medium articles all the time, and I wanted to automate posting them on twitter as well!



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


## 📋 Prerequisites

- Java 21 or higher
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


## 🧠 How It Works

1. **Article Input**: Accepts Medium article URL 
2. **Content Analysis**: Extracts key points from article content
3. **Thread Generation**: 
   - Creates introduction tweet with article title
   - Converts key points to individual tweets
   - Adds thread numbering (1/n, 2/n, etc.)
   - Ensures tweets stay under 280 characters
   - Adds call-to-action with original article link
4. **Output**: Displays formatted Twitter thread ready for posting


Check out my articles:
[![Foo](https://github.com/user-attachments/assets/8019e38f-6a78-4658-a659-beb6b319594c)](https://medium.com/@dillondoa)


---


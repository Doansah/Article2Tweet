package article2tweet.com.article2tweet.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;

/**
 * Test configuration that excludes Embabel agent platform configuration
 * to avoid LLM dependency issues during testing
 */
@TestConfiguration
@Profile("test")
public class TestConfig {
    // This configuration is used to override beans for testing
    // without requiring full Embabel setup
}

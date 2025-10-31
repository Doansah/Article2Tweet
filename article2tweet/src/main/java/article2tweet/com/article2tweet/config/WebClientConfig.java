package article2tweet.com.article2tweet.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    
    @Value("${medium.api.key:}")
    private String mediumApiKey;
    
    @Bean
    public WebClient mediumWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.mediumapi.com/v1")
                .defaultHeader("X-RapidAPI-Key", mediumApiKey)
                .defaultHeader("X-RapidAPI-Host", "medium2.p.rapidapi.com")
                .build();
    }
}

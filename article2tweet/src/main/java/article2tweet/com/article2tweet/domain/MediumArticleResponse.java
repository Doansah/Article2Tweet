package article2tweet.com.article2tweet.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response wrapper for Medium API user articles endpoint
 */
@Data
@NoArgsConstructor
public class MediumArticleResponse {
    
    @JsonProperty("associated_articles")
    private List<ArticleSummary> articles;
    
    public MediumArticleResponse(List<ArticleSummary> articles) {
        this.articles = articles;
    }
}

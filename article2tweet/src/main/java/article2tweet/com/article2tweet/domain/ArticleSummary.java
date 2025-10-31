package article2tweet.com.article2tweet.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a simplified article summary from Medium API
 * Used for listing user's articles before fetching full content
 */
@Data
@NoArgsConstructor
public class ArticleSummary {
    
    private String id;
    private String title;
    private String subtitle;
    private String url;
    
    @JsonProperty("published_at")
    private String publishedAt;
    
    @JsonProperty("last_modified_at")
    private String lastModifiedAt;
    
    private List<String> tags;
    private String topics;
    private Integer claps;
    private Integer voters;
    
    @JsonProperty("word_count")
    private Integer wordCount;
    
    @JsonProperty("reading_time")
    private Integer readingTime;
    
    public ArticleSummary(String id, String title, String url) {
        this.id = id;
        this.title = title;
        this.url = url;
    }
}

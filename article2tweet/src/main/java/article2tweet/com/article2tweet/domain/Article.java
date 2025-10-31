package article2tweet.com.article2tweet.domain;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Article {
    private String title;
    private String content;
    private String author;
    private String url;
    private LocalDate publishedDate;
    private String source;
    private List<String> tags;
    private int estimatedReadTime;
    
    // Simplified constructor for basic article creation
    public Article(String title, String content, String url) {
        this.title = title;
        this.content = content;
        this.url = url;
        this.source = "Medium";
    }
}
package article2tweet.com.article2tweet.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tweet {
    private int order; 
    private String content;
    private int characterCount;
    private boolean hasHashtags;
    private String threadId; // For linking tweets in a thread
    
    // Constructor for simple tweet creation
    public Tweet(int order, String content) {
        this.order = order;
        this.content = content;
        this.characterCount = content.length();
        this.hasHashtags = content.contains("#");
    }
}
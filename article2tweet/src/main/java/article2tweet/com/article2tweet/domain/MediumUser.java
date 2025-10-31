package article2tweet.com.article2tweet.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a Medium user from the API response
 */
@Data
@NoArgsConstructor
public class MediumUser {
    
    private String id;
    private String username;
    private String name;
    private String bio;
    
    @JsonProperty("image_url")
    private String imageUrl;
    
    @JsonProperty("twitter_username")
    private String twitterUsername;
    
    @JsonProperty("followers_count")
    private Integer followersCount;
    
    @JsonProperty("following_count")
    private Integer followingCount;
    
    @JsonProperty("is_writer_program_enrolled")
    private Boolean isWriterProgramEnrolled;
    
    public MediumUser(String id, String username, String name) {
        this.id = id;
        this.username = username;
        this.name = name;
    }
}

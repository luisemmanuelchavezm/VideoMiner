package aiss.videominer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * @author Juan C. Alonso
 */
@Entity
@Table(name = "Video")
public class Video {

    @Id
    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    @NotEmpty(message = "Video name cannot be empty")
    private String name;

    @JsonProperty("description")
    @Column(columnDefinition="TEXT")
    private String description;

    @JsonProperty("releaseTime")
    @NotEmpty(message = "Video release time cannot be empty")
    private String releaseTime;

    @JsonProperty("comments")
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "videoId")
    @NotNull(message = "Video comments cannot be null")
    private List<Comment> comments;

    @JsonProperty("captions")
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "videoId")
    @NotNull(message = "Video captions cannot be null")
    private List<Caption> captions;

    public Video(String id, String name, String description, String releaseTime) {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Caption> getCaptions() {
        return captions;
    }

    public void setCaptions(List<Caption> captions) {
        this.captions = captions;
    }

    @Override
    public String toString() {
        return "Video{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", releaseTime='" + releaseTime + '\'' +
                ", comments=" + comments +
                ", captions=" + captions +
                '}';
    }
    public Video(){

    }
    public Video(String id, String name, String description, String releaseTime, List<Comment> comments, List<Caption> captions) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseTime = releaseTime;
        this.comments = comments;
        this.captions = captions;
    }
}

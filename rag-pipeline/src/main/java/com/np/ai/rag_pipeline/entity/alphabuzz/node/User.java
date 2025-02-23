package com.np.ai.rag_pipeline.entity.alphabuzz.node;

import com.np.ai.rag_pipeline.entity.alphabuzz.edge.Follow;
import com.np.ai.rag_pipeline.entity.alphabuzz.edge.Like;
import com.np.ai.rag_pipeline.entity.alphabuzz.edge.Publish;
import com.np.ai.rag_pipeline.entity.alphabuzz.edge.Republish;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Node("User")
public class User {

    @Id
    private UUID userId;
    private String username;
    private String displayName;
    private LocalDate registeredAt;

    @Relationship(type = "FOLLOW", direction = Relationship.Direction.OUTGOING)
    private Set<Follow> follow;

    @Relationship(type = "PUBLISH", direction = Relationship.Direction.OUTGOING)
    private Set<Publish> publish;

    @Relationship(type = "LIKE", direction = Relationship.Direction.OUTGOING)
    private Set<Like> like;

    @Relationship(type = "REPUBLISH", direction = Relationship.Direction.OUTGOING)
    private Set<Republish> republish;

    public User() {

    }

    public User(UUID userId, String username, String displayName, LocalDate registeredAt) {
        this.userId = userId;
        this.username = username;
        this.displayName = displayName;
        this.registeredAt = registeredAt;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public LocalDate getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDate registeredAt) {
        this.registeredAt = registeredAt;
    }

    public Set<Follow> getFollow() {
        return follow;
    }

    public void setFollow(Set<Follow> follow) {
        this.follow = follow;
    }

    public Set<Publish> getPublish() {
        return publish;
    }

    public void setPublish(Set<Publish> publish) {
        this.publish = publish;
    }

    public Set<Like> getLike() {
        return like;
    }

    public void setLike(Set<Like> like) {
        this.like = like;
    }

    public Set<Republish> getRepublish() {
        return republish;
    }

    public void setRepublish(Set<Republish> republish) {
        this.republish = republish;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", displayName='" + displayName + '\'' +
                ", registeredAt=" + registeredAt +
                '}';
    }
}

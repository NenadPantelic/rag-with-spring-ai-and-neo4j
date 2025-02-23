package com.np.ai.rag_pipeline.entity.alphabuzz.node;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Node("Buzz")
public class Buzz {

    @Id
    private UUID buzzId;
    private String htmlContent;
    private OffsetDateTime createdAt;

    @Relationship(type = "HAS_TAG", direction = Relationship.Direction.OUTGOING)
    private Set<Tag> tag;

    @Relationship(type = "HAS_LINK", direction = Relationship.Direction.OUTGOING)
    private Set<Link> link;

    @Relationship(type = "HAS_IMAGE", direction = Relationship.Direction.OUTGOING)
    private Image image;

    public Buzz() {
    }

    public Buzz(UUID buzzId, String htmlContent, OffsetDateTime createdAt) {
        this.buzzId = buzzId;
        this.htmlContent = htmlContent;
        this.createdAt = createdAt;
    }

    public UUID getBuzzId() {
        return buzzId;
    }

    public void setBuzzId(UUID buzzId) {
        this.buzzId = buzzId;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<Tag> getTag() {
        return tag;
    }

    public void setTag(Set<Tag> tag) {
        this.tag = tag;
    }

    public Set<Link> getLink() {
        return link;
    }

    public void setLink(Set<Link> link) {
        this.link = link;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}

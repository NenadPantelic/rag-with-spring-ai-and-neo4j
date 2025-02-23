package com.np.ai.rag_pipeline.entity.alphabuzz.node;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.UUID;

@Node("Tag")
public class Tag {

    @Id
    private UUID tagId;

    private String url;

    public Tag() {
    }

    public Tag(UUID tagId, String url) {
        this.tagId = tagId;
        this.url = url;
    }

    public UUID getTagId() {
        return tagId;
    }

    public void setTagId(UUID tagId) {
        this.tagId = tagId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

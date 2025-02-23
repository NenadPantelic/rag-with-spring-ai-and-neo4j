package com.np.ai.rag_pipeline.entity.alphabuzz.node;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.UUID;

@Node("Link")
public class Link {

    @Id
    private UUID linkId;

    private String url;

    public Link() {
    }

    public Link(UUID linkId, String url) {
        this.linkId = linkId;
        this.url = url;
    }

    public UUID getLinkId() {
        return linkId;
    }

    public void setLinkId(UUID linkId) {
        this.linkId = linkId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

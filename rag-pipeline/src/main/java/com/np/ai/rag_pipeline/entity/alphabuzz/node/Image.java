package com.np.ai.rag_pipeline.entity.alphabuzz.node;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.UUID;

@Node("Image")
public class Image {

    @Id
    private UUID imageId;

    private String url;

    public Image() {
    }

    public Image(UUID imageId, String url) {
        this.imageId = imageId;
        this.url = url;
    }

    public UUID getImageId() {
        return imageId;
    }

    public void setImageId(UUID imageId) {
        this.imageId = imageId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

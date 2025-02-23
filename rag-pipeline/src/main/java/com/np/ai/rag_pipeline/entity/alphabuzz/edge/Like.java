package com.np.ai.rag_pipeline.entity.alphabuzz.edge;

import com.np.ai.rag_pipeline.entity.alphabuzz.node.Buzz;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.OffsetDateTime;

@RelationshipProperties
public class Like {

    @Id
    @GeneratedValue
    private String id;

    @TargetNode
    private Buzz target;

    private OffsetDateTime likedAt;

    public Like() {
    }

    public Like(Buzz target, OffsetDateTime likedAt) {
        this.target = target;
        this.likedAt = likedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Buzz getTarget() {
        return target;
    }

    public void setTarget(Buzz target) {
        this.target = target;
    }

    public OffsetDateTime getLikedAt() {
        return likedAt;
    }

    public void setLikedAt(OffsetDateTime likedAt) {
        this.likedAt = likedAt;
    }
}

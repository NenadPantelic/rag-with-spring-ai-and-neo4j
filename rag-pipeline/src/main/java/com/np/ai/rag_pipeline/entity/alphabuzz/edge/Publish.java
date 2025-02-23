package com.np.ai.rag_pipeline.entity.alphabuzz.edge;

import com.np.ai.rag_pipeline.entity.alphabuzz.node.Buzz;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
public class Publish {

    @Id
    @GeneratedValue
    private String id;

    @TargetNode
    private Buzz target;

    public Publish() {
    }

    public Publish(Buzz target) {
        this.target = target;
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
}

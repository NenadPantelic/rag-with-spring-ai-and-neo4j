package com.np.ai.rag_pipeline.repository.alphabuzz;

import com.np.ai.rag_pipeline.entity.alphabuzz.node.Buzz;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface Neo4jBuzzRepository extends Neo4jRepository<Buzz, UUID> {

    @Query("""
            MATCH (b:Buzz {buzzId: $buzzId})
            MATCH (i:Image)
            WHERE toLower(i.url) = toLower($url)
            MERGE (b)-[:HAS_IMAGE]->(i)
            """)
    void upsertImageRelationship(@Param("buzzId") UUID buzzId, @Param("url") String url);

    @Query("""
            MATCH (b:Buzz {buzzId: $buzzId})
            MATCH (l:Link)
            WHERE toLower(l.url) = toLower($url)
            MERGE (b)-[:HAS_LINK]->(l)
            """)
    void upsertLinkRelationship(@Param("buzzId") UUID buzzId, @Param("url") String url);

    @Query("""
            MATCH (b:Buzz {buzzId: $buzzId})
            MATCH (t:Tag)
            WHERE toLower(t.text) = toLower($text)
            MERGE (b)-[:HAS_TAG]->(t)          
            """)
    void upsertTagRelationship(@Param("buzzId") UUID buzzId, @Param("text") String text);

    @Query("""
            MATCH (b:Buzz {buzzId: $buzzId})
            MATCH (u:User {userId: $userId})
            MERGE (b)-[r:MENTION]->(u)
            """)
    void upsertMentionRelationship(@Param("buzzId") UUID buzzId, @Param("userId") UUID userId);

}
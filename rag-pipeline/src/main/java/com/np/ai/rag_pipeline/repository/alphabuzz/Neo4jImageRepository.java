package com.np.ai.rag_pipeline.repository.alphabuzz;

import com.np.ai.rag_pipeline.entity.alphabuzz.node.Image;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface Neo4jImageRepository extends Neo4jRepository<Image, UUID> {

    @Query("""
            MATCH (i:Image)
            WHERE toLower(i.url) = toLower($url)
            RETURN i
            LIMIT 1
            """)
    Optional<Image> findByUrl(@Param("url") String url);

}
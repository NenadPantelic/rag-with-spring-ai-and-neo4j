package com.np.ai.rag_pipeline.repository.alphabuzz;

import com.np.ai.rag_pipeline.entity.alphabuzz.node.Link;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface Neo4jLinkRepository extends Neo4jRepository<Link, UUID> {

    @Query("""
            MATCH (l:Link)
            WHERE toLower(l.url) = toLower($url)
            RETURN l
            LIMIT 1
            """)
    Optional<Link> findByUrl(@Param("url") String url);

}

package com.np.ai.rag_pipeline.repository.alphabuzz;

import com.np.ai.rag_pipeline.entity.alphabuzz.node.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface Neo4jUserRepository extends Neo4jRepository<User, UUID> {

    @Query("MATCH (u:User) " +
            "WHERE u.username = toLower($username) " +
            "RETURN u " +
            "LIMIT 1")
    Optional<User> findByUsername(@Param("username") String username);

    @Query("MATCH (u:User {userId: $userId}) " +
            "MATCH (f:User {userId: $followUserId}) " +
            "MERGE (u)-[r:FOLLOW]->(f) " +
            "ON CREATE SET r.since = $since " +
            "ON MATCH SET r.since = $since")
    void upsertFollowRelationship(
            @Param("userId") UUID userId, @Param("followUserId") UUID followUserId,
            @Param("since") LocalDate since);

    @Query("MATCH (u:User {userId: $userId}) " +
            "MATCH (b:Buzz {buzzId: $buzzId}) " +
            "MERGE (u)-[r:PUBLISH]->(b)")
    void upsertPublishRelationship(
            @Param("userId") UUID userId, @Param("buzzId") UUID buzzId);

    @Query("MATCH (u:User {userId: $userId}) " +
            "MATCH (b:Buzz {buzzId: $buzzId}) " +
            "MERGE (u)-[r:LIKE]->(b) " +
            "ON CREATE SET r.likeAt = $likeAt " +
            "ON MATCH SET r.likeAt = $likeAt")
    void upsertLikeRelationship(
            @Param("userId") UUID userId, @Param("buzzId") UUID buzzId,
            @Param("likeAt") OffsetDateTime likeAt);

    @Query("MATCH (u:User {userId: $userId}) " +
            "MATCH (b:Buzz {buzzId: $buzzId}) " +
            "MERGE (u)-[r:REPUBLISH]->(b) " +
            "ON CREATE SET r.republishAt = $republishAt " +
            "ON MATCH SET r.republishAt = $republishAt")
    void upsertRepublishRelationship(
            @Param("userId") UUID userId, @Param("buzzId") UUID buzzId,
            @Param("republishAt") OffsetDateTime republishAt);
}

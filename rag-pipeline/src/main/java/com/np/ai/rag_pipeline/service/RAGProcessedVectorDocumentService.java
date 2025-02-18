package com.np.ai.rag_pipeline.service;

import com.np.ai.rag_pipeline.entity.RAGProcessedVectorDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RAGProcessedVectorDocumentService {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private static final String SQL_FIND_BY_SOURCE_PATH = """
            SELECT processed_document_id, source_path, hash, first_processed_at, last_processed_at
            FROM rag_processed_vector_documents
            WHERE LOWER(source_path) = LOWER(:sourcePath)
            LIMIT 1
            """;

    private static final String SQL_UPSERT = """
            INSERT INTO rag_processed_vector_documents (
            processed_document_id, source_path,
            hash, first_processed_at, last_processed_at
            )
            VALUES (:processedDocumentId, LOWER(:sourcePath), :hash, :firstProcessedAt, :lastProcessedAt)
            ON CONFLICT (LOWER(source_path)) DO UPDATE
            SET hash = :hash,
            last_processed_at = :lastProcessedAt
            """;

    public Optional<RAGProcessedVectorDocument> findBySourcePath(String sourcePath) {
        var params = new MapSqlParameterSource();
        params.addValue("sourcePath", sourcePath);

        return jdbcTemplate.query(SQL_FIND_BY_SOURCE_PATH, params, rs -> {
            if (rs.next()) {
                return Optional.of(mapRowToRAGProcessedVectorDocument(rs));
            } else {
                return Optional.empty();
            }
        });
    }

    private RAGProcessedVectorDocument mapRowToRAGProcessedVectorDocument(ResultSet rs) throws SQLException {
        return new RAGProcessedVectorDocument(
                UUID.fromString(rs.getString("processed_document_id")),
                rs.getString("source_path"),
                rs.getString("hash"),
                rs.getObject("first_processed_at", OffsetDateTime.class),
                rs.getObject("last_processed_at", OffsetDateTime.class));
    }

    public RAGProcessedVectorDocument save(RAGProcessedVectorDocument element) {
        var params = new MapSqlParameterSource();
        params.addValue("processedDocumentId", element.getProcessedDocumentId());
        params.addValue("sourcePath", element.getSourcePath());
        params.addValue("hash", element.getHash());
        params.addValue("firstProcessedAt", element.getFirstProcessedAt());
        params.addValue("lastProcessedAt", element.getLastProcessedAt());

        jdbcTemplate.update(SQL_UPSERT, params);

        return element;
    }

}
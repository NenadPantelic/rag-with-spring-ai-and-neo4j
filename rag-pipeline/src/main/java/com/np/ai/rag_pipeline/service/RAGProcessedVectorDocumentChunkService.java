package com.np.ai.rag_pipeline.service;

import java.util.List;
import java.util.UUID;

import com.np.ai.rag_pipeline.entity.RAGProcessedVectorDocumentChunk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class RAGProcessedVectorDocumentChunkService {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private static final String SQL_FIND_BY_PROCESSED_DOCUMENT_ID = """
            SELECT chunk_id, processed_document_id
            FROM rag_processed_vector_document_chunks
            WHERE processed_document_id = :processedDocumentId
            """;

    private static final String SQL_DELETE_BY_CHUNK_ID = """
            DELETE FROM rag_processed_vector_document_chunks
            WHERE chunk_id = :chunkId
            """;

    private static final String SQL_UPSERT = """
            INSERT INTO rag_processed_vector_document_chunks (
            chunk_id, processed_document_id
            )
            VALUES (:chunkId, :processedDocumentId)
            ON CONFLICT (chunk_id) DO NOTHING
            """;

    public List<RAGProcessedVectorDocumentChunk> findByProcessedDocumentId(UUID processedDocumentId) {
        var params = new MapSqlParameterSource();
        params.addValue("processedDocumentId", processedDocumentId);

        return jdbcTemplate.query(SQL_FIND_BY_PROCESSED_DOCUMENT_ID, params, (rs, rowNum) -> {
            return new RAGProcessedVectorDocumentChunk(
                    rs.getString("chunk_id"),
                    UUID.fromString(rs.getString("processed_document_id")));
        });
    }

    public void deleteById(String chunkId) {
        var params = new MapSqlParameterSource();
        params.addValue("chunkId", chunkId);

        jdbcTemplate.update(SQL_DELETE_BY_CHUNK_ID, params);
    }

    public void save(RAGProcessedVectorDocumentChunk chunkEntity) {
        var params = new MapSqlParameterSource();
        params.addValue("chunkId", chunkEntity.getChunkId());
        params.addValue("processedDocumentId", chunkEntity.getProcessedDocumentId());

        jdbcTemplate.update(SQL_UPSERT, params);
    }

}
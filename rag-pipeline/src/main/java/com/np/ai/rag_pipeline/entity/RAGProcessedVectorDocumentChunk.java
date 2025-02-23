package com.np.ai.rag_pipeline.entity;

import jakarta.persistence.Id;
import org.springframework.data.domain.Persistable;

import java.util.UUID;

public class RAGProcessedVectorDocumentChunk implements Persistable<String> {

    @Id
    private String chunkId;

    private UUID processedDocumentId;


    public RAGProcessedVectorDocumentChunk() {
    }

    public RAGProcessedVectorDocumentChunk(String chunkId, UUID processedDocumentId) {
        this.chunkId = chunkId;
        this.processedDocumentId = processedDocumentId;
    }

    public String getChunkId() {
        return chunkId;
    }

    public void setChunkId(String chunkId) {
        this.chunkId = chunkId;
    }

    public UUID getProcessedDocumentId() {
        return processedDocumentId;
    }

    public void setProcessedDocumentId(UUID processedDocumentId) {
        this.processedDocumentId = processedDocumentId;
    }

    @Override
    public String toString() {
        return "RAGProcessedVectorDocumentChunk{" +
                "chunkId='" + chunkId + '\'' +
                ", processedDocumentId=" + processedDocumentId +
                '}';
    }

    @Override
    public String getId() {
        if (chunkId == null) {
            chunkId = UUID.randomUUID().toString();
        }

        return chunkId;
    }

    @Override
    public boolean isNew() {
        return chunkId == null;
    }
}
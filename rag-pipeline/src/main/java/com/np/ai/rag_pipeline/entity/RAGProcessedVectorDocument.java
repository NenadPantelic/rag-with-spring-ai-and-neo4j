package com.np.ai.rag_pipeline.entity;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.data.domain.Persistable;

import java.time.OffsetDateTime;
import java.util.UUID;

public class RAGProcessedVectorDocument implements Persistable<UUID> {

    @Id
    private UUID processedDocumentId;

    private String sourcePath;

    private String hash;

    private OffsetDateTime firstProcessedAt;

    private OffsetDateTime lastProcessedAt;

    public RAGProcessedVectorDocument(UUID processedDocumentId,
                                      String sourcePath,
                                      String hash,
                                      OffsetDateTime firstProcessedAt,
                                      OffsetDateTime lastProcessedAt) {
        this.processedDocumentId = processedDocumentId;
        this.sourcePath = sourcePath;
        this.hash = hash;
        this.firstProcessedAt = firstProcessedAt;
        this.lastProcessedAt = lastProcessedAt;
    }

    public RAGProcessedVectorDocument() {

    }

    public UUID getProcessedDocumentId() {
        return processedDocumentId;
    }

    public void setProcessedDocumentId(UUID processedDocumentId) {
        this.processedDocumentId = processedDocumentId;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public OffsetDateTime getFirstProcessedAt() {
        return firstProcessedAt;
    }

    public void setFirstProcessedAt(OffsetDateTime firstProcessedAt) {
        this.firstProcessedAt = firstProcessedAt;
    }

    public OffsetDateTime getLastProcessedAt() {
        return lastProcessedAt;
    }

    public void setLastProcessedAt(OffsetDateTime lastProcessedAt) {
        this.lastProcessedAt = lastProcessedAt;
    }

    @Override
    public UUID getId() {
        if (processedDocumentId == null) {
            processedDocumentId = UUID.randomUUID();
        }

        return processedDocumentId;
    }

    @Override
    public boolean isNew() {
        return processedDocumentId == null;
    }

    @Override
    public String toString() {
        return "RAGProcessedVectorDocument{" +
                "processedDocumentId=" + processedDocumentId +
                ", sourcePath='" + sourcePath + '\'' +
                ", hash='" + hash + '\'' +
                ", firstProcessedAt=" + firstProcessedAt +
                ", lastProcessedAt=" + lastProcessedAt +
                '}';
    }
}
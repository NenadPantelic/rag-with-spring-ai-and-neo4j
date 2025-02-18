package com.np.ai.repository;

import com.np.ai.entity.RAGProcessedVectorDocument;
import com.np.ai.entity.RAGProcessedVectorDocumentChunk;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RAGProcessedVectorDocumentChunkRepository extends R2dbcRepository<RAGProcessedVectorDocumentChunk, String> {

    Flux<RAGProcessedVectorDocumentChunk> findByProcessedDocumentId(UUID processedDocumentId);
}

package com.np.ai.rag_pipeline.service;

import com.np.ai.rag_pipeline.entity.RAGProcessedVectorDocument;
import com.np.ai.rag_pipeline.entity.RAGProcessedVectorDocumentChunk;
import com.np.ai.rag_pipeline.rag.indexing.RAGTikaDocumentReader;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.vectorstore.neo4j.Neo4jVectorStore;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RAGVectorIndexingService {

    private static final Logger LOG = LoggerFactory.getLogger(RAGVectorIndexingService.class);

    private static final String CUSTOM_KEYWORDS_METADATA_KEY = "custom_keywords";


    private final RAGTikaDocumentReader documentReader;
    private final TextSplitter textSplitter;
    private final Neo4jVectorStore vectorStore;
    private final RAGProcessedVectorDocumentService processedVectorDocumentService;
    private final RAGProcessedVectorDocumentChunkService processedVectorDocumentChunkService;

    public RAGVectorIndexingService(RAGTikaDocumentReader documentReader,
                                    TextSplitter textSplitter,
                                    Neo4jVectorStore vectorStore,
                                    RAGProcessedVectorDocumentService processedVectorDocumentService,
                                    RAGProcessedVectorDocumentChunkService processedVectorDocumentChunkService) {
        this.documentReader = documentReader;
        this.textSplitter = textSplitter;
        this.vectorStore = vectorStore;
        this.processedVectorDocumentService = processedVectorDocumentService;
        this.processedVectorDocumentChunkService = processedVectorDocumentChunkService;
    }

    private void addCustomMetadata(Document document, List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return;
        }

        Assert.notNull(document, "Document must not be null");
        document.getMetadata().put(CUSTOM_KEYWORDS_METADATA_KEY, keywords);
    }

    private String calculateHash(Resource resource) {
        var lastModified = 0L;

        try {
            lastModified = resource.lastModified();
        } catch (Exception e) {
            LOG.warn("Could not get lastModified for resource {}", resource, e);
        }

        var original = resource.getDescription().toLowerCase() + "//" + lastModified;
        return DigestUtils.sha256Hex(original);
    }

    @SuppressWarnings("null")
    @Transactional
    public List<Document> processDocument(Resource resource, List<String> keywords) {
        Assert.isTrue(resource != null && resource.exists(), "Resource must exist");

        var existingFromDb = processedVectorDocumentService.findBySourcePath(resource.getDescription());
        String resourceHash;

        try {
            resourceHash = calculateHash(resource);
        } catch (Exception e) {
            resourceHash = StringUtils.EMPTY;
        }

        if (existingFromDb.isPresent() && StringUtils.equals(existingFromDb.get().getHash(), resourceHash)) {
            LOG.info("Document already indexed: {}", resource.getDescription());
            return List.of();
        }

        var now = OffsetDateTime.now();
        var element = existingFromDb.orElse(new RAGProcessedVectorDocument(
                UUID.randomUUID(),
                resource.getDescription(),
                resourceHash,
                now,
                now
        ));

        var parsedDocument = documentReader.readFrom(resource);
        var splitDocuments = textSplitter.split(parsedDocument);

        splitDocuments.forEach(document -> addCustomMetadata(document, keywords));
        vectorStore.add(splitDocuments);

        LOG.info("Original document split into {} chunks and saved to vector store", splitDocuments.size());

        element.setHash(resourceHash);
        element.setLastProcessedAt(now);

        processedVectorDocumentChunkService.findByProcessedDocumentId(element.getProcessedDocumentId())
                .forEach(chunk -> {
                    processedVectorDocumentChunkService.deleteById(chunk.getChunkId());
                    vectorStore.delete(List.of(chunk.getChunkId()));
                });
        var savedDocument = processedVectorDocumentService.save(element);

        splitDocuments.forEach(chunk -> {
            var chunkEntity = new RAGProcessedVectorDocumentChunk(
                    chunk.getId(),
                    savedDocument.getProcessedDocumentId()
            );
            processedVectorDocumentChunkService.save(chunkEntity);
        });

        return splitDocuments;
    }

    public List<Document> indexDocumentFromFilesystem(String sourcePath, List<String> keywords) {
        var resource = new FileSystemResource(sourcePath);

        return processDocument(resource, keywords);
    }

    public List<Document> indexDocumentFromURL(String url, List<String> keywords) {
        try {
            var resource = new UrlResource(url);

            return processDocument(resource, keywords);
        } catch (Exception e) {
            throw new IllegalArgumentException("URL cannot be processed: " + url, e);
        }
    }
}

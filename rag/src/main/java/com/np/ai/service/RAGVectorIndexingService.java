package com.np.ai.service;

import com.np.ai.entity.RAGProcessedVectorDocument;
import com.np.ai.entity.RAGProcessedVectorDocumentChunk;
import com.np.ai.rag.indexing.RAGTikaDocumentReader;
import com.np.ai.repository.RAGProcessedVectorDocumentChunkRepository;
import com.np.ai.repository.RAGProcessedVectorDocumentRepository;
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
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

import java.net.MalformedURLException;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class RAGVectorIndexingService {

    private static final Logger LOG = LoggerFactory.getLogger(RAGVectorIndexingService.class);
    private static final String CUSTOM_KEYWORDS_METADATA_KEY = "custom_keywords";

    private final RAGTikaDocumentReader documentReader;
    private final TextSplitter textSplitter;
    private final Neo4jVectorStore vectorStore;

    private final RAGProcessedVectorDocumentRepository processedVectorDocumentRepository;
    private final RAGProcessedVectorDocumentChunkRepository processedVectorDocumentChunkRepository;

    public RAGVectorIndexingService(RAGTikaDocumentReader documentReader,
                                    TextSplitter textSplitter,
                                    Neo4jVectorStore vectorStore,
                                    RAGProcessedVectorDocumentRepository processedVectorDocumentRepository,
                                    RAGProcessedVectorDocumentChunkRepository processedVectorDocumentChunkRepository) {
        this.documentReader = documentReader;
        this.textSplitter = textSplitter;
        this.vectorStore = vectorStore;
        this.processedVectorDocumentRepository = processedVectorDocumentRepository;
        this.processedVectorDocumentChunkRepository = processedVectorDocumentChunkRepository;
    }

    public List<Document> indexDocumentFromFilesystem(String sourcePath,
                                                      List<String> keywords) {
        var resource = new FileSystemResource(sourcePath);
        return processDocument(resource, keywords);
    }

    public List<Document> indexDocumentFromUrl(String sourcePath,
                                               List<String> keywords) {
        try {
            var resource = new UrlResource(sourcePath);
            return processDocument(resource, keywords);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Invalid URL %s", sourcePath));
        }
    }

    private void addMetadata(Document document, List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return;
        }

        Assert.notNull(document, "Document must not be null");

        document.getMetadata().put(CUSTOM_KEYWORDS_METADATA_KEY, keywords);
    }

    private List<Document> processDocument(Resource resource,
                                           List<String> keywords) {

        Assert.isTrue(resource != null && resource.exists(), "Resource must not be null and must exist");
        var parsedDocuments = documentReader.readFrom(resource);

        var splitDocuments = textSplitter.split(parsedDocuments);
        splitDocuments.forEach(document -> addMetadata(document, keywords));

        vectorStore.add(splitDocuments); // will automatically use the embedding model to add the embedding to each doc
        LOG.info("The original document split into {} chunks and saved to vector store", splitDocuments.size());

        return splitDocuments;
    }

    private String calculateHash(Resource resource) {
        var lastModified = 0L;

        try {
            lastModified = resource.lastModified();
        } catch (Exception e) {
            LOG.warn("Failed to get last modified time for resource {}", resource, e);
        }

        var original = resource.getDescription() + "//" + lastModified;
        return DigestUtils.sha256Hex(original);
    }

    private Mono<List<Document>> processDocumentReactive(Resource resource, List<String> keywords) {
        Assert.isTrue(resource != null && resource.exists(), "Resource must not be null and must exist");

        var existingFromDb = processedVectorDocumentRepository.findBySourcePath(resource.getDescription());
        var now = OffsetDateTime.now();

        return existingFromDb.defaultIfEmpty(
                        new RAGProcessedVectorDocument(null, resource.getDescription(), StringUtils.EMPTY, now, now))
                .flatMap(element -> {
                    var hash = calculateHash(resource);

                    if (StringUtils.equals(element.getHash(), hash)) {
                        LOG.info("Document with hash {} already indexed", hash);
                        return Mono.empty();
                    }

                    var parsedDocuments = documentReader.readFrom(resource);
                    var splitDocuments = textSplitter.split(parsedDocuments);

                    splitDocuments.forEach(document -> addMetadata(document, keywords));
                    vectorStore.add(splitDocuments);

                    LOG.info("Original document split into {} chunks and saved to vector store", splitDocuments.size());

                    element.setHash(hash);
                    element.setLastProcessedAt(now);

                    try {
                        processedVectorDocumentChunkRepository.findByProcessedDocumentId(element.getProcessedDocumentId())
                                .subscribe(chunk -> {
                                    processedVectorDocumentChunkRepository.deleteById(chunk.getChunkId()).subscribe();
                                    vectorStore.delete(List.of(chunk.getChunkId()));
                                });
                        processedVectorDocumentRepository.save(element).subscribe(savedDocument -> {
                            splitDocuments.forEach(chunk -> {
                                var chunkEntity = new RAGProcessedVectorDocumentChunk(
                                        chunk.getId(), savedDocument.getProcessedDocumentId()
                                );

                                processedVectorDocumentChunkRepository.save(chunkEntity).subscribe();
                            });
                        });
                    } catch (Exception e) {
                        LOG.error("Failed to save processed document to database", e);
                        return Mono.empty();
                    }

                    return Mono.just(splitDocuments);
                });
    }

    public Mono<List<Document>> indexDocumentsFromFilesystemReactive(String sourcePath, List<String> keywords) {
        var resource = new FileSystemResource(sourcePath);
        return processDocumentReactive(resource, keywords);
    }

    public Mono<List<Document>> indexDocumentsFromURLReactive(String url, List<String> keywords) {
        try {
            UrlResource resource = new UrlResource(url);
            return processDocumentReactive(resource, keywords);

        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(String.format("Invalid URL %s", url));
        }
    }
}

package com.np.ai.service;

import com.np.ai.rag.indexing.RAGDocumentFileWriter;
import com.np.ai.rag.indexing.RAGTikaDocumentReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class RAGBasicIndexingService {

    private static final Logger LOG = LoggerFactory.getLogger(RAGBasicIndexingService.class);
    private static final String CUSTOM_KEYWORDS_METADATA_KEY = "custom_keywords";

    private final RAGTikaDocumentReader documentReader;
    private final TextSplitter textSplitter;
    private final RAGDocumentFileWriter documentFileWriter;

    public RAGBasicIndexingService(RAGTikaDocumentReader documentReader,
                                   TextSplitter textSplitter,
                                   RAGDocumentFileWriter documentFileWriter) {
        this.documentReader = documentReader;
        this.textSplitter = textSplitter;
        this.documentFileWriter = documentFileWriter;
    }

    public List<Document> indexDocumentFromFilesystem(String sourcePath,
                                                      String outputFilename,
                                                      boolean appendIfFileExists,
                                                      List<String> keywords) {
        var resource = new FileSystemResource(sourcePath);
        return processDocument(resource, outputFilename, appendIfFileExists, keywords);
    }

    public List<Document> indexDocumentFromUrl(String sourcePath,
                                               String outputFilename,
                                               boolean appendIfFileExists,
                                               List<String> keywords) {
        try {
            var resource = new UrlResource(sourcePath);
            return processDocument(resource, outputFilename, appendIfFileExists, keywords);
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
                                           String outputFilename,
                                           boolean appendIfFileExists,
                                           List<String> keywords) {

        Assert.isTrue(resource != null && resource.exists(), "Resource must not be null and must exist");
        var parsedDocuments = documentReader.readFrom(resource);

        var splitDocuments = textSplitter.split(parsedDocuments);
        splitDocuments.forEach(document -> addMetadata(document, keywords));

        documentFileWriter.writeDocumentsToFile(splitDocuments, outputFilename, appendIfFileExists);
        LOG.info("The original document split into {} chunks", splitDocuments.size());

        return splitDocuments;
    }
}

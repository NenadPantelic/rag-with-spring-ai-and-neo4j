package com.np.ai.rag.indexing;

import org.springframework.ai.document.Document;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.writer.FileDocumentWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RAGDocumentFileWriter {

    public void writeDocumentsToFile(List<Document> documents, String filename, boolean appendIfFileExists) {
        var writer = new FileDocumentWriter(filename, true, MetadataMode.ALL, appendIfFileExists);
        writer.accept(documents);
    }
}

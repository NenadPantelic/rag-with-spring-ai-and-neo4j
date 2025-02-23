package com.np.ai.rag_pipeline.temporal.activity;

import com.np.ai.rag_pipeline.temporal.constant.TemporalTaskQueues;
import com.np.ai.rag_pipeline.service.RAGVectorIndexingService;
import io.temporal.spring.boot.ActivityImpl;
import org.springframework.ai.document.Document;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ActivityImpl(taskQueues = TemporalTaskQueues.FINANCIAL_INDEXING)
public class FinancialIndexingActivityImpl implements FinancialIndexingActivity {

    private final RAGVectorIndexingService vectorIndexingService;

    public FinancialIndexingActivityImpl(RAGVectorIndexingService vectorIndexingService) {
        this.vectorIndexingService = vectorIndexingService;
    }

    @Override
    public List<Document> processDocument(String resourcePath, List<String> keywords) {
        var resource = new FileSystemResource(resourcePath);
        return vectorIndexingService.processDocument(resource, keywords);
    }
}

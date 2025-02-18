package com.np.ai.rag_pipeline.temporal.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import org.springframework.ai.document.Document;

import java.util.List;

@ActivityInterface // we could have two activities
// 1. fetch the resource
// 2. process it
// but since the Resource type in Spring is not serializable, we only have one activity
public interface FinancialIndexingActivity {

    @ActivityMethod
    List<Document> processDocument(String resourcePath, List<String> keywords);
}

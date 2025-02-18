package com.np.ai.rag_pipeline.temporal.workflow;

import com.np.ai.rag_pipeline.temporal.workflow.input.IndexingWorkflowInput;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import org.springframework.ai.document.Document;

import java.util.List;

@WorkflowInterface
public interface FinancialIndexingWorkflow {

    @WorkflowMethod
    List<Document> indexFinancialData(IndexingWorkflowInput source);
}

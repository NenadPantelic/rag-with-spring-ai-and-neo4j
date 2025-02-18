package com.np.ai.rag_pipeline.api.server;

import com.np.ai.rag_pipeline.constant.TemporalTaskQueues;
import com.np.ai.rag_pipeline.temporal.workflow.FinancialIndexingWorkflow;
import com.np.ai.rag_pipeline.temporal.workflow.input.IndexingWorkflowInput;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/temporal")
public class TemporalWorkflowApi {

    private final WorkflowClient workflowClient;

    public TemporalWorkflowApi(WorkflowClient workflowClient) {
        this.workflowClient = workflowClient;
    }

    @PostMapping(path = "/v1/financial", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> financialSample() {
        var workflow = workflowClient.newWorkflowStub(
                FinancialIndexingWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setTaskQueue(TemporalTaskQueues.FINANCIAL_INDEXING)
                        .setWorkflowId("FinancialSample")
                        .build()
        );

        var input = new IndexingWorkflowInput(
                "src/main/resources/documents/company-financial-statement.html",
                List.of("financial-statement")
        );
        var documents = workflow.indexFinancialData(input);
        return ResponseEntity.ok().body(String.format("%s indexed as %d documents", input.resourcePath(), documents.size()));
    }
}

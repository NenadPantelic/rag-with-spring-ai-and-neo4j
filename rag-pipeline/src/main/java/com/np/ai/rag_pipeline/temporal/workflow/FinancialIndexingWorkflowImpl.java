package com.np.ai.rag_pipeline.temporal.workflow;

import com.np.ai.rag_pipeline.constant.TemporalTaskQueues;
import com.np.ai.rag_pipeline.temporal.activity.FinancialIndexingActivity;
import com.np.ai.rag_pipeline.temporal.workflow.input.IndexingWorkflowInput;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;
import org.springframework.ai.document.Document;

import java.time.Duration;
import java.util.List;

@WorkflowImpl(taskQueues = TemporalTaskQueues.FINANCIAL_INDEXING)
public class FinancialIndexingWorkflowImpl implements FinancialIndexingWorkflow {

    private final RetryOptions RETRY_OPTIONS = RetryOptions.newBuilder()
            .setInitialInterval(Duration.ofSeconds(2))
            .setBackoffCoefficient(1.5)
            .setMaximumAttempts(10)
            .build();

    private final ActivityOptions ACTIVITY_OPTIONS = ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(120))
            .setScheduleToCloseTimeout(Duration.ofSeconds(300))
            .setTaskQueue(TemporalTaskQueues.FINANCIAL_INDEXING)
            .setRetryOptions(RETRY_OPTIONS)
            .build();

    private final FinancialIndexingActivity ACTIVITY = Workflow.newActivityStub(
            FinancialIndexingActivity.class,
            ACTIVITY_OPTIONS
    );

    @Override
    public List<Document> indexFinancialData(IndexingWorkflowInput source) {
        return ACTIVITY.processDocument(source.resourcePath(), source.keywords());
    }
}

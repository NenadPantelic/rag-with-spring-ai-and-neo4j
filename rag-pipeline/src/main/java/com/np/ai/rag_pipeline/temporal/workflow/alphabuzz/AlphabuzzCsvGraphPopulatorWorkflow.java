package com.np.ai.rag_pipeline.temporal.workflow.alphabuzz;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface AlphabuzzCsvGraphPopulatorWorkflow {

    @WorkflowMethod
    void populateGraph();
}

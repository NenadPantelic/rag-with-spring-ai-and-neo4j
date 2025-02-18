package com.np.ai.rag_pipeline.temporal.workflow.input;

import java.util.List;

public record IndexingWorkflowInput(String resourcePath,
                                    List<String> keywords) {
}

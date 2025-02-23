package com.np.ai.rag_pipeline.temporal.workflow.alphabuzz;

import com.np.ai.rag_pipeline.temporal.activity.alphabuzz.AlphabuzzCsvGraphPopulatorActivities;
import com.np.ai.rag_pipeline.temporal.constant.TemporalTaskQueues;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;

import java.time.Duration;

@WorkflowImpl(taskQueues = TemporalTaskQueues.ALPHABUZZ_CSV_GRAPH_POPULATOR)
public class AlphabuzzCsvGraphPopulatorWorkflowImpl implements AlphabuzzCsvGraphPopulatorWorkflow {

    private final RetryOptions RETRY_OPTIONS = RetryOptions.newBuilder()
            .setInitialInterval(Duration.ofSeconds(2))
            .setBackoffCoefficient(1.5)
            .setMaximumAttempts(10)
            .build();

    private final ActivityOptions ACTIVITY_OPTIONS = ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofMinutes(60))
            .setScheduleToCloseTimeout(Duration.ofMinutes(90))
            .setTaskQueue(TemporalTaskQueues.ALPHABUZZ_CSV_GRAPH_POPULATOR)
            .setRetryOptions(RETRY_OPTIONS)
            .build();

    private final AlphabuzzCsvGraphPopulatorActivities ACTIVITIES = Workflow.newActivityStub(
            AlphabuzzCsvGraphPopulatorActivities.class,
            ACTIVITY_OPTIONS);

    @Override
    public void populateGraph() {
        var populateUserPromise = Async.procedure(ACTIVITIES::populateUser);
        var populateBuzzPromise = Async.procedure(ACTIVITIES::populateBuzz);

        Promise.allOf(populateUserPromise, populateBuzzPromise).get();

        var populateRelationship_Follow = Async.procedure(ACTIVITIES::populateRelationship_Follow);
        var populateRelationship_Publish = Async.procedure(ACTIVITIES::populateRelationship_Publish);
        var populateRelationship_Like = Async.procedure(ACTIVITIES::populateRelationship_Like);
        var populateRelationship_Republish = Async.procedure(ACTIVITIES::populateRelationship_Republish);
        var populateBuzzChildNodes = Async.procedure(ACTIVITIES::populateBuzzChildNodes);

        Promise.allOf(populateRelationship_Follow, populateRelationship_Publish, populateRelationship_Like,
                populateRelationship_Republish, populateBuzzChildNodes).get();
    }
}

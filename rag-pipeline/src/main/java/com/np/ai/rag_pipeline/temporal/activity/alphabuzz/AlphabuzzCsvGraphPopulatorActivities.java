package com.np.ai.rag_pipeline.temporal.activity.alphabuzz;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface AlphabuzzCsvGraphPopulatorActivities {

    @ActivityMethod
    int populateUser();

    @ActivityMethod
    int populateBuzz();

    @ActivityMethod
    int populateRelationship_Follow();

    @ActivityMethod
    int populateRelationship_Publish();

    @ActivityMethod
    int populateRelationship_Like();

    @ActivityMethod
    int populateRelationship_Republish();

    @ActivityMethod
    int populateBuzzChildNodes();
}

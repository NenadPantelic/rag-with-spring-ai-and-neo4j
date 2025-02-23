package com.np.ai.rag_pipeline.temporal.activity.alphabuzz;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface AlphabuzzStaticGraphPopulatorActivities {

    @ActivityMethod
    void populateUser();

    @ActivityMethod
    void populateBuzz();

    @ActivityMethod
    void populateRelationship_Follow();

    @ActivityMethod
    void populateRelationship_Publish();

    @ActivityMethod
    void populateRelationship_Like();

    @ActivityMethod
    void populateRelationship_Republish();
}

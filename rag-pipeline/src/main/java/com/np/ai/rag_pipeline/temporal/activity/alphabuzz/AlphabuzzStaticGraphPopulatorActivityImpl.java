package com.np.ai.rag_pipeline.temporal.activity.alphabuzz;

import com.np.ai.rag_pipeline.temporal.constant.TemporalTaskQueues;
import com.np.ai.rag_pipeline.service.alphabuzz.StaticGraphPopulatorService;
import io.temporal.spring.boot.ActivityImpl;
import org.springframework.stereotype.Component;

@Component
@ActivityImpl(taskQueues = TemporalTaskQueues.ALPHABUZZ_STATIC_GRAPH_POPULATOR)
public class AlphabuzzStaticGraphPopulatorActivityImpl implements AlphabuzzStaticGraphPopulatorActivities {

    private final StaticGraphPopulatorService staticGraphPopulatorService;

    public AlphabuzzStaticGraphPopulatorActivityImpl(StaticGraphPopulatorService staticGraphPopulatorService) {
        this.staticGraphPopulatorService = staticGraphPopulatorService;
    }

    @Override
    public void populateUser() {
        staticGraphPopulatorService.populateStaticUser();
    }

    @Override
    public void populateBuzz() {
        staticGraphPopulatorService.populateStaticBuzz();
    }

    @Override
    public void populateRelationship_Follow() {
        staticGraphPopulatorService.populateStaticRelationship_Follow();
    }

    @Override
    public void populateRelationship_Publish() {
        staticGraphPopulatorService.populateStaticRelationship_Publish();
    }

    @Override
    public void populateRelationship_Like() {
        staticGraphPopulatorService.populateStaticRelationship_Like();
    }

    @Override
    public void populateRelationship_Republish() {
        staticGraphPopulatorService.populateStaticRelationship_Republish();
    }
}

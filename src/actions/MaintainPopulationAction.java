package actions;

import actions.planner.AbstractSpeciesPopulationPlanner;
import actions.planner.GrassPopulationPlanner;
import actions.planner.HerbivorePopulationPlanner;
import actions.planner.PredatorPopulationPlanner;
import world.WorldMap;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class MaintainPopulationAction implements Action {

    private final SpawnBalanceConfig config;
    private final List<AbstractSpeciesPopulationPlanner> speciesPlanners;

    public MaintainPopulationAction() {
        this(SpawnBalanceConfig.DEFAULT);
    }

    public MaintainPopulationAction(SpawnBalanceConfig config) {
        this(config, new DefaultSpawnEntityFactory());
    }

    MaintainPopulationAction(SpawnBalanceConfig config, SpawnEntityFactory entityFactory) {
        this.config = config;
        this.speciesPlanners = List.of(
                new GrassPopulationPlanner(config.grass(), entityFactory),
                new HerbivorePopulationPlanner(config.herbivores(), entityFactory),
                new PredatorPopulationPlanner(config.predators(), entityFactory)
        );
    }

    @Override
    public void execute(WorldMap worldMap) {
        Random random = ThreadLocalRandom.current();

        for (AbstractSpeciesPopulationPlanner speciesPlanner : speciesPlanners) {
            speciesPlanner.spawnIfNeeded(worldMap, random, config.maxSpawnAttemptsPerEntity());
        }
    }
}
package actions.planner;

import actions.SpawnBalanceConfig.PredatorSettings;
import actions.SpawnEntityFactory;
import entity.Herbivore;
import entity.Predator;
import world.WorldMap;

public final class PredatorPopulationPlanner extends AbstractSpeciesPopulationPlanner {

    private final PredatorSettings settings;

    public PredatorPopulationPlanner(PredatorSettings settings, SpawnEntityFactory entityFactory) {
        super(settings.spawnCapPerTurn(), entityFactory::createPredator);
        this.settings = settings;
    }

    @Override
    protected int currentCount(WorldMap worldMap) {
        return count(worldMap, Predator.class, Predator::isAlive);
    }

    @Override
    protected int minimumCount(WorldMap worldMap) {
        int minimumByArea = atLeastOne(worldMap.getArea() / settings.minimumByAreaDivisor());
        int herbivoreCount = count(worldMap, Herbivore.class, Herbivore::isAlive);
        int minimumByHerbivores = atLeastOne(herbivoreCount / settings.minimumByHerbivoresDivisor());

        return Math.max(minimumByArea, minimumByHerbivores);
    }

    @Override
    protected int targetCount(WorldMap worldMap) {
        int targetByArea = atLeastOne(worldMap.getArea() / settings.targetByAreaDivisor());
        int herbivoreCount = count(worldMap, Herbivore.class, Herbivore::isAlive);
        int targetByHerbivores = atLeastOne(herbivoreCount / settings.targetByHerbivoresDivisor());

        return Math.min(targetByArea, targetByHerbivores);
    }
}
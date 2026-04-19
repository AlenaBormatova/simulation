package actions.planner;

import actions.SpawnBalanceConfig.HerbivoreSettings;
import actions.SpawnEntityFactory;
import entity.Herbivore;
import world.WorldMap;

public final class HerbivorePopulationPlanner extends AbstractSpeciesPopulationPlanner {

    private final HerbivoreSettings settings;

    public HerbivorePopulationPlanner(HerbivoreSettings settings, SpawnEntityFactory entityFactory) {
        super(settings.spawnCapPerTurn(), entityFactory::createHerbivore);
        this.settings = settings;
    }

    @Override
    protected int currentCount(WorldMap worldMap) {
        return count(worldMap, Herbivore.class, Herbivore::isAlive);
    }

    @Override
    protected int minimumCount(WorldMap worldMap) {
        return atLeastOne(worldMap.getArea() / settings.minimumByAreaDivisor());
    }

    @Override
    protected int targetCount(WorldMap worldMap) {
        return worldMap.getArea() / settings.targetByAreaDivisor();
    }
}
package actions.planner;

import actions.SpawnBalanceConfig.GrassSettings;
import actions.SpawnEntityFactory;
import entity.Grass;
import entity.Herbivore;
import world.WorldMap;

public final class GrassPopulationPlanner extends AbstractSpeciesPopulationPlanner {

    private final GrassSettings settings;

    public GrassPopulationPlanner(GrassSettings settings, SpawnEntityFactory entityFactory) {
        super(settings.spawnCapPerTurn(), entityFactory::createGrass);
        this.settings = settings;
    }

    @Override
    protected int currentCount(WorldMap worldMap) {
        return count(worldMap, Grass.class);
    }

    @Override
    protected int minimumCount(WorldMap worldMap) {
        int minimumByArea = Math.max(settings.minimumFloor(), worldMap.getArea() / settings.minimumByAreaDivisor());
        int herbivoreCount = count(worldMap, Herbivore.class, Herbivore::isAlive);
        return Math.max(minimumByArea, herbivoreCount);
    }

    @Override
    protected int targetCount(WorldMap worldMap) {
        int targetByArea = Math.max(settings.minimumFloor(), worldMap.getArea() / settings.targetByAreaDivisor());
        int herbivoreCount = count(worldMap, Herbivore.class, Herbivore::isAlive);
        int targetByHerbivores = herbivoreCount * settings.targetPerHerbivoreMultiplier();
        return Math.max(targetByArea, targetByHerbivores);
    }
}
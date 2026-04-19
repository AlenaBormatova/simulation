package actions.planner;

import actions.EntitySpawner;
import actions.EntitySpawner.EntityFactory;
import entity.Entity;
import world.WorldMap;
import world.WorldMapStatistics;

import java.util.Random;
import java.util.function.Predicate;

public abstract class AbstractSpeciesPopulationPlanner {

    private final int spawnCapPerTurn;
    private final EntityFactory spawnFactory;

    public AbstractSpeciesPopulationPlanner(int spawnCapPerTurn, EntityFactory spawnFactory) {
        this.spawnCapPerTurn = spawnCapPerTurn;
        this.spawnFactory = spawnFactory;
    }

    public final void spawnIfNeeded(WorldMap worldMap, Random random, int maxSpawnAttemptsPerEntity) {
        int spawnLimitThisTurn = calculateSpawnLimit(worldMap);
        if (spawnLimitThisTurn <= 0) {
            return;
        }

        EntitySpawner.spawnUpTo(worldMap, random, spawnLimitThisTurn, maxSpawnAttemptsPerEntity, spawnFactory);
    }

    protected abstract int currentCount(WorldMap worldMap);

    protected abstract int minimumCount(WorldMap worldMap);

    protected abstract int targetCount(WorldMap worldMap);

    protected final int atLeastOne(int value) {
        return Math.max(1, value);
    }

    protected final <T extends Entity> int count(WorldMap worldMap, Class<T> type) {
        return WorldMapStatistics.count(worldMap, type);
    }

    protected final <T extends Entity> int count(WorldMap worldMap, Class<T> type, Predicate<T> filter) {
        return WorldMapStatistics.count(worldMap, type, filter);
    }

    private int calculateSpawnLimit(WorldMap worldMap) {
        int currentCount = currentCount(worldMap);
        int minimumCount = minimumCount(worldMap);
        if (currentCount >= minimumCount) {
            return 0;
        }

        int targetCount = Math.max(minimumCount, targetCount(worldMap));
        int missingToTarget = targetCount - currentCount;
        return Math.min(missingToTarget, spawnCapPerTurn);
    }
}
package actions;

import world.WorldMap;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class InitPopulateAction implements Action {

    private static final int ROCK_DENSITY_DIVISOR = 20;
    private static final int TREE_DENSITY_DIVISOR = 33;
    private static final int GRASS_DENSITY_DIVISOR = 12;
    private static final int HERBIVORE_DENSITY_DIVISOR = 25;
    private static final int PREDATOR_DENSITY_DIVISOR = 75;
    private static final int MAX_PLACEMENT_ATTEMPTS_MULTIPLIER = 50;

    private final SpawnEntityFactory entityFactory;

    public InitPopulateAction() {
        this(new DefaultSpawnEntityFactory());
    }

    InitPopulateAction(SpawnEntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    @Override
    public void execute(WorldMap worldMap) {
        Random random = ThreadLocalRandom.current();
        int area = worldMap.getArea();
        int maxAttempts = worldMap.getArea() * MAX_PLACEMENT_ATTEMPTS_MULTIPLIER;

        spawnMany(worldMap, random, area, ROCK_DENSITY_DIVISOR, maxAttempts, entityFactory::createRock);
        spawnMany(worldMap, random, area, TREE_DENSITY_DIVISOR, maxAttempts, entityFactory::createTree);
        spawnMany(worldMap, random, area, GRASS_DENSITY_DIVISOR, maxAttempts, entityFactory::createGrass);
        spawnMany(worldMap, random, area, HERBIVORE_DENSITY_DIVISOR, maxAttempts, entityFactory::createHerbivore);
        spawnMany(worldMap, random, area, PREDATOR_DENSITY_DIVISOR, maxAttempts, entityFactory::createPredator);
    }

    private void spawnMany(WorldMap worldMap, Random random, int area, int divisor, int maxAttempts,
                           EntitySpawner.EntityFactory factory) {
        int entityCount = Math.max(1, area / divisor);
        EntitySpawner.spawnUpTo(worldMap, random, entityCount, maxAttempts, factory);
    }
}
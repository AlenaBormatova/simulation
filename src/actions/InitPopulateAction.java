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

        int rocks = density(area, ROCK_DENSITY_DIVISOR);
        int trees = density(area, TREE_DENSITY_DIVISOR);
        int grass = density(area, GRASS_DENSITY_DIVISOR);
        int herbivores = density(area, HERBIVORE_DENSITY_DIVISOR);
        int predators = density(area, PREDATOR_DENSITY_DIVISOR);

        spawnMany(worldMap, random, rocks, entityFactory::createRock);
        spawnMany(worldMap, random, trees, entityFactory::createTree);
        spawnMany(worldMap, random, grass, entityFactory::createGrass);
        spawnMany(worldMap, random, herbivores, entityFactory::createHerbivore);
        spawnMany(worldMap, random, predators, entityFactory::createPredator);
    }

    private void spawnMany(WorldMap worldMap,
                           Random random,
                           int entityCount,
                           EntitySpawner.EntityFactory factory) {
        int maxAttempts = worldMap.getArea() * MAX_PLACEMENT_ATTEMPTS_MULTIPLIER;
        EntitySpawner.spawnUpTo(worldMap, random, entityCount, maxAttempts, factory);
    }

    private int density(int area, int divisor) {
        return Math.max(1, area / divisor);
    }
}
package actions;

import entity.Coordinates;
import entity.Entity;
import entity.Grass;
import entity.Herbivore;
import entity.Predator;
import world.WorldMap;

import java.util.Random;

public final class EnsureMinimumSpawnsAction implements Action {

    private static final int MAX_SPAWN_ATTEMPTS_PER_ENTITY = 3000;

    private static final int MIN_GRASS_FLOOR = 1;
    private static final int GRASS_SPAWN_CAP_PER_TURN = 20;
    private static final int GRASS_MIN_BY_AREA_DIVISOR = 15;
    private static final int GRASS_TARGET_BY_AREA_DIVISOR = 10;
    private static final int GRASS_TARGET_PER_HERBIVORE_MULTIPLIER = 1;

    private static final int HERBIVORES_SPAWN_CAP_PER_TURN = 3;
    private static final int HERBIVORE_MIN_DENSITY_DIVISOR = 35;
    private static final int HERBIVORE_TARGET_DENSITY_DIVISOR = 25;
    private static final int HERBIVORE_SPAWN_HP = 18;
    private static final int HERBIVORE_SPAWN_SPEED = 2;

    private static final int PREDATORS_SPAWN_CAP_PER_TURN = 4;
    private static final int PREDATOR_MIN_BY_AREA_DIVISOR = 100;
    private static final int PREDATOR_TARGET_BY_AREA_DIVISOR = 70;
    private static final int PREDATOR_MIN_BY_HERBIVORES_DIVISOR = 6;
    private static final int PREDATOR_TARGET_BY_HERBIVORES_DIVISOR = 4;
    private static final int PREDATOR_SPAWN_HP = 40;
    private static final int PREDATOR_SPAWN_SPEED = 2;
    private static final int PREDATOR_SPAWN_ATTACK = 8;

    @Override
    public void execute(WorldMap map, Random random, int turn) {
        ensureGrass(map, random);
        ensureHerbivores(map, random);
        ensurePredators(map, random);
    }

    private void ensureGrass(WorldMap map, Random random) {
        int currentGrassCount = map.countGrass();
        int herbivoreCount = map.countAliveHerbivores();
        int mapArea = map.getArea();

        int minGrassByArea = Math.max(MIN_GRASS_FLOOR, mapArea / GRASS_MIN_BY_AREA_DIVISOR);
        int minGrassCount = Math.max(minGrassByArea, herbivoreCount);

        if (currentGrassCount >= minGrassCount) {
            return;
        }

        int targetGrassByArea = Math.max(MIN_GRASS_FLOOR, mapArea / GRASS_TARGET_BY_AREA_DIVISOR);
        int targetGrassByHerbivores = herbivoreCount * GRASS_TARGET_PER_HERBIVORE_MULTIPLIER;
        int targetGrassCount = Math.max(targetGrassByArea, targetGrassByHerbivores);
        targetGrassCount = Math.max(targetGrassCount, minGrassCount);

        int neededToTarget = targetGrassCount - currentGrassCount;
        int toSpawnThisTurn = Math.min(neededToTarget, GRASS_SPAWN_CAP_PER_TURN);

        for (int spawned = 0; spawned < toSpawnThisTurn; spawned++) {
            if (!tryPlace(map, random, Grass::new)) {
                break;
            }
        }
    }

    private void ensureHerbivores(WorldMap map, Random random) {
        int mapArea = map.getArea();
        int currentHerbivoreCount = map.countAliveHerbivores();

        int minHerbivores = Math.max(1, mapArea / HERBIVORE_MIN_DENSITY_DIVISOR);
        int targetHerbivores = Math.max(minHerbivores, mapArea / HERBIVORE_TARGET_DENSITY_DIVISOR);

        if (currentHerbivoreCount >= minHerbivores) {
            return;
        }

        int neededToTarget = targetHerbivores - currentHerbivoreCount;
        int toSpawnThisTurn = Math.min(neededToTarget, HERBIVORES_SPAWN_CAP_PER_TURN);

        for (int spawned = 0; spawned < toSpawnThisTurn; spawned++) {
            if (!tryPlace(map, random, this::createHerbivore)) {
                break;
            }
        }
    }

    private void ensurePredators(WorldMap map, Random random) {
        int mapArea = map.getArea();
        int herbivoreCount = map.countAliveHerbivores();
        int currentPredatorCount = map.countAlivePredators();

        int minPredatorsByArea = Math.max(1, mapArea / PREDATOR_MIN_BY_AREA_DIVISOR);
        int minPredatorsByHerbivores = Math.max(1, herbivoreCount / PREDATOR_MIN_BY_HERBIVORES_DIVISOR);
        int minPredators = Math.max(minPredatorsByArea, minPredatorsByHerbivores);

        int targetPredatorsByArea = Math.max(1, mapArea / PREDATOR_TARGET_BY_AREA_DIVISOR);
        int targetPredatorsByHerbivores = Math.max(1, herbivoreCount / PREDATOR_TARGET_BY_HERBIVORES_DIVISOR);
        int targetPredators = Math.max(
                minPredators,
                Math.min(targetPredatorsByArea, targetPredatorsByHerbivores)
        );

        if (currentPredatorCount >= minPredators) {
            return;
        }

        int neededToTarget = targetPredators - currentPredatorCount;
        int toSpawnThisTurn = Math.min(neededToTarget, PREDATORS_SPAWN_CAP_PER_TURN);

        for (int spawned = 0; spawned < toSpawnThisTurn; spawned++) {
            if (!tryPlace(map, random, this::createPredator)) {
                break;
            }
        }
    }

    private Entity createHerbivore(Coordinates spawnPosition) {
        return new Herbivore(spawnPosition, HERBIVORE_SPAWN_HP, HERBIVORE_SPAWN_SPEED);
    }

    private Entity createPredator(Coordinates spawnPosition) {
        return new Predator(spawnPosition, PREDATOR_SPAWN_HP, PREDATOR_SPAWN_SPEED, PREDATOR_SPAWN_ATTACK);
    }

    private boolean tryPlace(WorldMap map, Random random, SpawnFactory factory) {
        for (int attempt = 0; attempt < MAX_SPAWN_ATTEMPTS_PER_ENTITY; attempt++) {
            Coordinates spawnPosition = new Coordinates(
                    random.nextInt(map.getWidth()),
                    random.nextInt(map.getHeight())
            );

            if (map.isEmpty(spawnPosition)) {
                map.place(factory.create(spawnPosition));
                return true;
            }
        }
        return false;
    }

    @FunctionalInterface
    private interface SpawnFactory {
        Entity create(Coordinates position);
    }
}
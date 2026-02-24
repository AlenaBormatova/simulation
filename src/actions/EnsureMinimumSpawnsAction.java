package actions;

import entity.Coordinates;
import entity.Entity;
import entity.Grass;
import entity.Herbivore;
import world.WorldMap;

import java.util.Random;

public final class EnsureMinimumSpawnsAction implements Action {

    // ===== Общие параметры спавна =====
    private static final int MAX_SPAWN_ATTEMPTS_PER_ENTITY = 3000;

    // ===== Трава =====
    private static final int MIN_GRASS_FLOOR = 1;
    private static final int GRASS_SPAWN_CAP_PER_TURN = 40;

    // minGrass = max(1, area / divisor, herbivores)
    private static final int GRASS_MIN_BY_AREA_DIVISOR = 15;

    // targetGrass = max(1, area / divisor, herbivores * multiplier)
    private static final int GRASS_TARGET_BY_AREA_DIVISOR = 10;
    private static final int GRASS_TARGET_PER_HERBIVORE_MULTIPLIER = 2;

    // ===== Травоядные (зайцы) =====
    private static final int HERBIVORES_MIN_COUNT = 8;
    private static final int HERBIVORES_TARGET_COUNT = 12;
    private static final int HERBIVORES_SPAWN_CAP_PER_TURN = 3;

    // Параметры создаваемого травоядного
    private static final int HERBIVORE_SPAWN_HP = 18;
    private static final int HERBIVORE_SPAWN_SPEED = 2;

    @Override
    public void execute(WorldMap map, Random random, int turn) {
        ensureGrass(map, random);
        ensureHerbivores(map, random);
    }

    private void ensureGrass(WorldMap map, Random random) {
        int currentGrassCount = map.countGrass();
        int herbivoreCount = map.countAliveHerbivores();
        int mapArea = map.getWidth() * map.getHeight();

        int minGrassByArea = Math.max(MIN_GRASS_FLOOR, mapArea / GRASS_MIN_BY_AREA_DIVISOR);
        int minGrassCount = Math.max(minGrassByArea, herbivoreCount);
        if (currentGrassCount >= minGrassCount) return;

        int targetGrassByArea = Math.max(MIN_GRASS_FLOOR, mapArea / GRASS_TARGET_BY_AREA_DIVISOR);
        int targetGrassByHerbivores = herbivoreCount * GRASS_TARGET_PER_HERBIVORE_MULTIPLIER;

        int targetGrassCount = Math.max(targetGrassByArea, targetGrassByHerbivores);
        targetGrassCount = Math.max(targetGrassCount, minGrassCount);

        int neededToTarget = targetGrassCount - currentGrassCount;
        int toSpawnThisTurn = Math.min(neededToTarget, GRASS_SPAWN_CAP_PER_TURN);

        for (int spawned = 0; spawned < toSpawnThisTurn; spawned++) {
            if (!tryPlace(map, random, Grass::new)) break;
        }
    }

    private void ensureHerbivores(WorldMap map, Random random) {
        int currentHerbivoreCount = map.countAliveHerbivores();
        if (currentHerbivoreCount >= HERBIVORES_MIN_COUNT) return;

        int neededToTarget = HERBIVORES_TARGET_COUNT - currentHerbivoreCount;
        int toSpawnThisTurn = Math.min(neededToTarget, HERBIVORES_SPAWN_CAP_PER_TURN);

        for (int spawned = 0; spawned < toSpawnThisTurn; spawned++) {
            if (!tryPlace(map, random, this::createHerbivore)) break;
        }
    }

    private Entity createHerbivore(Coordinates spawnPosition) {
        return new Herbivore(spawnPosition, HERBIVORE_SPAWN_HP, HERBIVORE_SPAWN_SPEED);
    }

    private boolean tryPlace(WorldMap map, Random random, EntityFactory factory) {
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
    private interface EntityFactory {
        Entity create(Coordinates position);
    }
}

        /*int area = map.getWidth() * map.getHeight();
        int herbivores = map.countAliveHerbivores();

        int minGrass = Math.max(1, Math.max(area / 15, herbivores));
        int targetGrass = Math.max(minGrass, Math.max(area / 10, herbivores * 2));

        int current = map.countGrass();
        if (current >= minGrass) return;

        int need = targetGrass - current;

        for (int i = 0; i < need; i++) {
            for (int tries = 0; tries < 3000; tries++) {
                Coordinates spawnPosition = new Coordinates(random.nextInt(map.getWidth()), random.nextInt(map.getHeight()));
                if (map.isEmpty(spawnPosition)) {
                    map.place(new Grass(spawnPosition));
                    break;
                }
            }
        }
    }*/
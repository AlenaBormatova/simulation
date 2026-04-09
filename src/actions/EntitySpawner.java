package actions;

import entity.Coordinates;
import entity.Entity;
import world.WorldMap;

import java.util.Random;

public final class EntitySpawner {

    public static void spawnUpTo(WorldMap worldMap,
                                 Random random,
                                 int entitiesToSpawn,
                                 int maxAttemptsPerEntity,
                                 EntityFactory factory) {
        for (int spawnedCount = 0; spawnedCount < entitiesToSpawn; spawnedCount++) {
            if (!putRandomly(worldMap, random, maxAttemptsPerEntity, factory)) {
                break;
            }
        }
    }

    public static boolean putRandomly(WorldMap worldMap,
                                      Random random,
                                      int maxAttempts,
                                      EntityFactory factory) {
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            Coordinates spawnPosition = new Coordinates(
                    random.nextInt(worldMap.getWidth()),
                    random.nextInt(worldMap.getHeight())
            );

            if (worldMap.isEmpty(spawnPosition)) {
                worldMap.put(spawnPosition, factory.create());
                return true;
            }
        }

        return false;
    }

    @FunctionalInterface
    public interface EntityFactory {
        Entity create();
    }

    private EntitySpawner() {
    }
}
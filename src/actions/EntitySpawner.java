package actions;

import entity.Coordinates;
import entity.Entity;
import world.WorldMap;

import java.util.Random;

public final class EntitySpawner {

    public static void spawnUpTo(WorldMap map,
                                 Random random,
                                 int entitiesToSpawn,
                                 int maxAttemptsPerEntity,
                                 EntityFactory factory) {
        for (int spawnedCount = 0; spawnedCount < entitiesToSpawn; spawnedCount++) {
            if (!tryPlace(map, random, maxAttemptsPerEntity, factory)) {
                break;
            }
        }
    }

    public static boolean tryPlace(WorldMap map,
                                   Random random,
                                   int maxAttempts,
                                   EntityFactory factory) {
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            Coordinates spawnPosition = new Coordinates(
                    random.nextInt(map.getWidth()),
                    random.nextInt(map.getHeight())
            );

            if (map.isEmpty(spawnPosition)) {
                map.placeEntity(spawnPosition, factory.create());
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
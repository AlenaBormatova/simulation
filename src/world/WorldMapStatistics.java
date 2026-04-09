package world;

import entity.Entity;

import java.util.function.Predicate;

public final class WorldMapStatistics {

    public static <T extends Entity> int count(WorldMap worldMap, Class<T> type) {
        return count(worldMap, type, entity -> true);
    }

    public static <T extends Entity> int count(WorldMap worldMap,
                                               Class<T> type,
                                               Predicate<T> filter) {
        int count = 0;

        for (Entity entity : worldMap.getEntities()) {
            if (!type.isInstance(entity)) {
                continue;
            }

            T typedEntity = type.cast(entity);
            if (filter.test(typedEntity)) {
                count++;
            }
        }
        return count;
    }

    private WorldMapStatistics() {
    }
}
package world;

import entity.Entity;

import java.util.function.Predicate;

public final class WorldMapStatistics {

    public static <T extends Entity> int count(WorldMap map, Class<T> type) {
        return count(map, type, entity -> true);
    }

    public static <T extends Entity> int count(WorldMap map,
                                               Class<T> type,
                                               Predicate<T> filter) {
        int count = 0;

        for (Entity entity : map.getEntitiesSnapshot()) {
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
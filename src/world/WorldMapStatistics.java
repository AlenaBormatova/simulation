package world;

import entity.Creature;
import entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class WorldMapStatistics {

    public static List<Creature> getAliveCreaturesSnapshot(WorldMap map) {
        List<Creature> creatures = new ArrayList<>();

        for (Entity entity : map.getEntitiesSnapshot()) {
            if (entity instanceof Creature creature && creature.isAlive()) {
                creatures.add(creature);
            }
        }
        return creatures;
    }

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
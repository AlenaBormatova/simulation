package world;

import entity.Coordinates;
import entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public final class WorldMapNeighborhoods {

    public static List<Coordinates> neighbors8(WorldMap map, Coordinates position) {
        List<Coordinates> neighbors = new ArrayList<>(8);

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) {
                    continue;
                }

                Coordinates neighborPosition = new Coordinates(position.x() + dx, position.y() + dy);
                if (map.isValid(neighborPosition)) {
                    neighbors.add(neighborPosition);
                }
            }
        }
        return neighbors;
    }

    public static List<Coordinates> freeNeighbors8(WorldMap map, Coordinates position) {
        List<Coordinates> freeNeighbors = new ArrayList<>();

        for (Coordinates neighborPosition : neighbors8(map, position)) {
            if (map.isEmpty(neighborPosition)) {
                freeNeighbors.add(neighborPosition);
            }
        }
        return freeNeighbors;
    }

    public static <T extends Entity> Optional<T> findAdjacent(WorldMap map,
                                                              Coordinates position,
                                                              Class<T> type) {
        return findAdjacent(map, position, type, entity -> true);
    }

    public static <T extends Entity> Optional<T> findAdjacent(WorldMap map,
                                                              Coordinates position,
                                                              Class<T> type,
                                                              Predicate<T> filter) {
        for (Coordinates neighborPosition : neighbors8(map, position)) {
            Entity neighborEntity = map.get(neighborPosition);

            if (!type.isInstance(neighborEntity)) {
                continue;
            }

            T typedEntity = type.cast(neighborEntity);
            if (filter.test(typedEntity)) {
                return Optional.of(typedEntity);
            }
        }
        return Optional.empty();
    }

    public static <T extends Entity> boolean isAdjacentTo(WorldMap map,
                                                          Coordinates position,
                                                          Class<T> type) {
        return findAdjacent(map, position, type).isPresent();
    }

    public static <T extends Entity> boolean isAdjacentTo(WorldMap map,
                                                          Coordinates position,
                                                          Class<T> type,
                                                          Predicate<T> filter) {
        return findAdjacent(map, position, type, filter).isPresent();
    }

    private WorldMapNeighborhoods() {
    }
}
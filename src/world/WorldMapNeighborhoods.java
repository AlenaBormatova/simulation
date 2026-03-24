package world;

import entity.Coordinates;
import entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public final class WorldMapNeighborhoods {

    public record Positioned<T extends Entity>(Coordinates position, T entity) {
    }

    public static List<Coordinates> neighbors8(WorldMap map, Coordinates center) {
        List<Coordinates> neighbors = new ArrayList<>(8);

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) {
                    continue;
                }

                Coordinates neighborPosition = new Coordinates(center.x() + dx, center.y() + dy);
                if (map.isValid(neighborPosition)) {
                    neighbors.add(neighborPosition);
                }
            }
        }
        return neighbors;
    }

    public static List<Coordinates> emptyNeighbors8(WorldMap map, Coordinates center) {
        List<Coordinates> emptyNeighbors = new ArrayList<>();

        for (Coordinates neighborPosition : neighbors8(map, center)) {
            if (map.isEmpty(neighborPosition)) {
                emptyNeighbors.add(neighborPosition);
            }
        }
        return emptyNeighbors;
    }

    public static <T extends Entity> Optional<Positioned<T>> findAdjacent(WorldMap map,
                                                                          Coordinates center,
                                                                          Class<T> type) {
        return findAdjacent(map, center, type, entity -> true);
    }

    public static <T extends Entity> Optional<Positioned<T>> findAdjacent(WorldMap map,
                                                                          Coordinates center,
                                                                          Class<T> type,
                                                                          Predicate<T> filter) {
        for (Coordinates neighborPosition : neighbors8(map, center)) {
            Entity neighborEntity = map.get(neighborPosition);

            if (!type.isInstance(neighborEntity)) {
                continue;
            }

            T typedEntity = type.cast(neighborEntity);
            if (filter.test(typedEntity)) {
                return Optional.of(new Positioned<>(neighborPosition, typedEntity));
            }
        }
        return Optional.empty();
    }

    public static <T extends Entity> boolean isAdjacentTo(WorldMap map,
                                                          Coordinates center,
                                                          Class<T> type) {
        return findAdjacent(map, center, type).isPresent();
    }

    public static <T extends Entity> boolean isAdjacentTo(WorldMap map,
                                                          Coordinates center,
                                                          Class<T> type,
                                                          Predicate<T> filter) {
        return findAdjacent(map, center, type, filter).isPresent();
    }

    private WorldMapNeighborhoods() {
    }
}
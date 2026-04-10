package world;

import entity.Coordinates;
import entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public final class WorldMapNeighborhoods {

    public static List<Coordinates> neighbors8(WorldMap worldMap, Coordinates center) {
        worldMap.validate(center);

        List<Coordinates> neighbors = new ArrayList<>(8);

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) {
                    continue;
                }

                Coordinates neighborPosition = new Coordinates(center.x() + dx, center.y() + dy);
                if (worldMap.isValid(neighborPosition)) {
                    neighbors.add(neighborPosition);
                }
            }
        }
        return neighbors;
    }

    public static List<Coordinates> emptyNeighbors8(WorldMap worldMap, Coordinates center) {
        List<Coordinates> emptyNeighbors = new ArrayList<>();

        for (Coordinates neighborPosition : neighbors8(worldMap, center)) {
            if (worldMap.isEmpty(neighborPosition)) {
                emptyNeighbors.add(neighborPosition);
            }
        }
        return emptyNeighbors;
    }

    public static <T extends Entity> Optional<Positioned<T>> findAdjacent(WorldMap worldMap,
                                                                          Coordinates center,
                                                                          Class<T> type) {
        return findAdjacent(worldMap, center, type, entity -> true);
    }

    public static <T extends Entity> Optional<Positioned<T>> findAdjacent(WorldMap worldMap,
                                                                          Coordinates center,
                                                                          Class<T> type,
                                                                          Predicate<T> filter) {
        for (Coordinates neighborPosition : neighbors8(worldMap, center)) {
            Optional<T> typedNeighbor = worldMap.get(neighborPosition)
                    .filter(type::isInstance)
                    .map(type::cast)
                    .filter(filter);

            if (typedNeighbor.isPresent()) {
                return Optional.of(new Positioned<>(neighborPosition, typedNeighbor.orElseThrow()));
            }
        }
        return Optional.empty();
    }

    public static <T extends Entity> boolean isAdjacentTo(WorldMap worldMap,
                                                          Coordinates center,
                                                          Class<T> type) {
        return findAdjacent(worldMap, center, type).isPresent();
    }

    public static <T extends Entity> boolean isAdjacentTo(WorldMap worldMap,
                                                          Coordinates center,
                                                          Class<T> type,
                                                          Predicate<T> filter) {
        return findAdjacent(worldMap, center, type, filter).isPresent();
    }

    public record Positioned<T extends Entity>(Coordinates position, T entity) {
    }

    private WorldMapNeighborhoods() {
    }
}
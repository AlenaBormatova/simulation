package path;

import entity.Coordinates;
import world.WorldMap;
import world.WorldMapNeighborhoods;

import java.util.*;
import java.util.function.Predicate;

public final class PathFinder {

    public static List<Coordinates> find(WorldMap worldMap,
                                         Coordinates startPosition,
                                         Predicate<Coordinates> goalPredicate) {
        if (!worldMap.isValid(startPosition)) {
            return List.of();
        }

        if (goalPredicate.test(startPosition)) {
            return List.of(startPosition);
        }

        Queue<Coordinates> queue = new ArrayDeque<>();
        Set<Coordinates> visited = new HashSet<>();
        Map<Coordinates, Coordinates> parent = new HashMap<>();

        queue.add(startPosition);
        visited.add(startPosition);

        while (!queue.isEmpty()) {
            Coordinates currentPosition = queue.poll();

            for (Coordinates neighborPosition : WorldMapNeighborhoods.neighbors8(worldMap, currentPosition)) {
                if (visited.contains(neighborPosition)) {
                    continue;
                }

                if (!worldMap.isEmpty(neighborPosition)) {
                    continue;
                }

                visited.add(neighborPosition);
                parent.put(neighborPosition, currentPosition);

                if (goalPredicate.test(neighborPosition)) {
                    return reconstructPath(parent, startPosition, neighborPosition);
                }
                queue.add(neighborPosition);
            }
        }
        return List.of();
    }

    private static List<Coordinates> reconstructPath(Map<Coordinates, Coordinates> parent,
                                                     Coordinates startPosition,
                                                     Coordinates goal) {
        List<Coordinates> path = new ArrayList<>();
        Coordinates currentPosition = goal;

        while (currentPosition != null) {
            path.add(currentPosition);
            currentPosition = parent.get(currentPosition);
        }

        Collections.reverse(path);

        if (!path.isEmpty() && !path.getFirst().equals(startPosition)) {
            path.addFirst(startPosition);
        }
        return path;
    }

    private PathFinder() {
    }
}
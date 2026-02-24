package path;

import entity.Coordinates;
import world.WorldMap;

import java.util.*;
import java.util.function.Predicate;

public final class PathFinder {

    /**
     * BFS по 8 направлениям до ближайшей клетки, удовлетворяющей goalPredicate.
     * Двигаемся только по "проходимым" клеткам: start + empty.
     * Возвращает путь [start, ..., goal] или null, если цели нет.
     */
    public static List<Coordinates> findPathToNearest(WorldMap map,
                                                      Coordinates startPosition,
                                                      Predicate<Coordinates> goalPredicate) {
        if (!map.isValid(startPosition)) return null;

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

            for (Coordinates neighborPosition : map.neighbors8(currentPosition)) {
                if (visited.contains(neighborPosition)) continue;

                // Проходимость: можно заходить в пустые клетки (и старт уже добавлен)
                if (!map.isEmpty(neighborPosition)) continue;

                visited.add(neighborPosition);
                parent.put(neighborPosition, currentPosition);

                if (goalPredicate.test(neighborPosition)) {
                    return reconstructPath(parent, startPosition, neighborPosition);
                }

                queue.add(neighborPosition);
            }
        }

        return null;
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
        // гарантируем, что начало = start
        if (!path.isEmpty() && !path.getFirst().equals(startPosition)) {
            path.addFirst(startPosition);
        }
        return path;
    }

    private PathFinder() {}
}

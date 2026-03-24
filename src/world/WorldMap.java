package world;

import entity.Coordinates;
import entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class WorldMap {

    public record Occupant(Coordinates position, Entity entity) {
    }

    private final Map<Coordinates, Entity> cells = new HashMap<>();
    private final int width;
    private final int height;

    public WorldMap(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Invalid map size");
        }
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getArea() {
        return width * height;
    }

    public boolean isValid(Coordinates position) {
        return position.x() >= 0
                && position.x() < width
                && position.y() >= 0
                && position.y() < height;
    }

    public Entity get(Coordinates position) {
        return cells.get(position);
    }

    public Entity get(int x, int y) {
        return cells.get(new Coordinates(x, y));
    }

    public boolean isEmpty(Coordinates position) {
        return !cells.containsKey(position);
    }

    public boolean place(Coordinates position, Entity entity) {
        if (!isValid(position) || !isEmpty(position)) {
            return false;
        }

        cells.put(position, entity);
        return true;
    }

    public void remove(Coordinates position) {
        cells.remove(position);
    }

    public void move(Coordinates from, Coordinates to) {
        if (!isValid(from)) {
            throw new IllegalArgumentException("Invalid source: " + from);
        }

        if (!isValid(to)) {
            throw new IllegalArgumentException("Invalid destination: " + to);
        }

        if (isEmpty(from)) {
            throw new IllegalStateException("No entity at source: " + from);
        }

        if (!isEmpty(to)) {
            throw new IllegalStateException("Destination is occupied: " + to);
        }

        Entity entity = cells.remove(from);
        cells.put(to, entity);
    }

    public List<Occupant> getOccupantsSnapshot() {
        List<Occupant> snapshot = new ArrayList<>(cells.size());

        for (Map.Entry<Coordinates, Entity> entry : cells.entrySet()) {
            snapshot.add(new Occupant(entry.getKey(), entry.getValue()));
        }

        return snapshot;
    }

    public List<Entity> getEntitiesSnapshot() {
        return new ArrayList<>(cells.values());
    }
}
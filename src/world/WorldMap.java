package world;

import entity.Coordinates;
import entity.Creature;
import entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class WorldMap {

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
        return position.x >= 0
                && position.x < width
                && position.y >= 0
                && position.y < height;
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

    public boolean place(Entity entity) {
        Coordinates position = entity.getPosition();

        if (!isValid(position) || !isEmpty(position)) {
            return false;
        }

        cells.put(position, entity);
        return true;
    }

    public void remove(Coordinates position) {
        cells.remove(position);
    }

    public void moveEntity(Creature creature, Coordinates destination) {
        if (!isValid(destination)) {
            throw new IllegalArgumentException("Invalid destination: " + destination);
        }

        if (!isEmpty(destination)) {
            throw new IllegalStateException("Destination is occupied: " + destination);
        }

        Coordinates from = creature.getPosition();
        Entity occupant = cells.get(from);

        if (occupant != creature) {
            throw new IllegalStateException("Creature position out of sync with WorldMap: " + from);
        }

        cells.remove(from);
        creature.setPosition(destination);
        cells.put(destination, creature);
    }

    public List<Entity> getEntitiesSnapshot() {
        return new ArrayList<>(cells.values());
    }
}
package world;

import entity.Coordinates;
import entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class WorldMap {

    private final Map<Coordinates, Entity> cells = new HashMap<>();
    private final int width;
    private final int height;

    public WorldMap(int width, int height) {
        validateMapSize(width, height);
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
        if (position == null) {
            return false;
        }

        return position.x() >= 0
                && position.x() < width
                && position.y() >= 0
                && position.y() < height;
    }

    public void validate(Coordinates position) {
        Objects.requireNonNull(position, "Coordinates must not be null");

        if (!isValid(position)) {
            String message = "Coordinates %s are out of bounds for map %dx%d"
                    .formatted(position, width, height);
            throw new IndexOutOfBoundsException(message);
        }
    }

    public Optional<Entity> get(Coordinates position) {
        validate(position);
        return Optional.ofNullable(cells.get(position));
    }

    public Optional<Entity> get(int x, int y) {
        return get(new Coordinates(x, y));
    }

    public boolean isEmpty(Coordinates position) {
        validate(position);
        return !cells.containsKey(position);
    }

    public void placeEntity(Coordinates position, Entity entity) {
        validate(position);
        Objects.requireNonNull(entity, "Entity must not be null");

        if (!isEmpty(position)) {
            String message = "Cannot place %s at %s: cell is already occupied"
                    .formatted(entity.getClass().getSimpleName(), position);
            throw new IllegalStateException(message);
        }

        cells.put(position, entity);
    }

    public Optional<Coordinates> findPositionOf(Entity entity) {
        Objects.requireNonNull(entity, "Entity must not be null");

        for (Map.Entry<Coordinates, Entity> entry : cells.entrySet()) {
            if (entry.getValue() == entity) {
                return Optional.of(entry.getKey());
            }
        }

        return Optional.empty();
    }

    public List<PositionedEntity> getPositionedEntities() {
        List<PositionedEntity> positionedEntities = new ArrayList<>(cells.size());

        for (Map.Entry<Coordinates, Entity> entry : cells.entrySet()) {
            positionedEntities.add(new PositionedEntity(entry.getKey(), entry.getValue()));
        }

        return positionedEntities;
    }

    public List<Entity> getEntities() {
        return new ArrayList<>(cells.values());
    }

    public void removeEntity(Coordinates position) {
        validate(position);

        if (isEmpty(position)) {
            String message = "Cannot remove entity from %s: cell is already empty"
                    .formatted(position);
            throw new IllegalStateException(message);
        }

        cells.remove(position);
    }

    private void validateMapSize(int width, int height) {
        if (width <= 0 || height <= 0) {
            String message = "Invalid map size: width=%d, height=%d".formatted(width, height);
            throw new IllegalArgumentException(message);
        }
    }

    public record PositionedEntity(Coordinates position, Entity entity) {
    }
}
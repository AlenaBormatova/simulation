package world;

import entity.Coordinates;
import entity.Creature;
import entity.Entity;
import entity.Grass;
import entity.Herbivore;
import entity.Predator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    public List<Creature> getAliveCreaturesSnapshot() {
        List<Creature> creatures = new ArrayList<>();

        for (Entity entity : cells.values()) {
            if (entity instanceof Creature creature && creature.isAlive()) {
                creatures.add(creature);
            }
        }
        return creatures;
    }

    public List<Coordinates> neighbors8(Coordinates position) {
        List<Coordinates> neighbors = new ArrayList<>(8);

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) {
                    continue;
                }

                Coordinates neighborPosition = new Coordinates(position.x + dx, position.y + dy);

                if (isValid(neighborPosition)) {
                    neighbors.add(neighborPosition);
                }
            }
        }
        return neighbors;
    }

    public List<Coordinates> freeNeighbors8(Coordinates position) {
        List<Coordinates> freeNeighborPositions = new ArrayList<>();

        for (Coordinates neighborPosition : neighbors8(position)) {
            if (isEmpty(neighborPosition)) {
                freeNeighborPositions.add(neighborPosition);
            }
        }
        return freeNeighborPositions;
    }

    public Optional<Grass> findAdjacentGrass8(Coordinates position) {
        for (Coordinates neighborPosition : neighbors8(position)) {
            Entity neighborEntity = get(neighborPosition);

            if (neighborEntity instanceof Grass grass) {
                return Optional.of(grass);
            }
        }
        return Optional.empty();
    }

    public Optional<Herbivore> findAdjacentHerbivore8(Coordinates position) {
        for (Coordinates neighborPosition : neighbors8(position)) {
            Entity neighborEntity = get(neighborPosition);

            if (neighborEntity instanceof Herbivore herbivore && herbivore.isAlive()) {
                return Optional.of(herbivore);
            }
        }
        return Optional.empty();
    }

    public boolean isAdjacentToGrass8(Coordinates position) {
        return findAdjacentGrass8(position).isPresent();
    }

    public boolean isAdjacentToAliveHerbivore8(Coordinates position) {
        return findAdjacentHerbivore8(position).isPresent();
    }

    public int countGrass() {
        int grassCount = 0;

        for (Entity entity : cells.values()) {
            if (entity instanceof Grass) {
                grassCount++;
            }
        }
        return grassCount;
    }

    public int countAliveHerbivores() {
        int herbivoreCount = 0;

        for (Entity entity : cells.values()) {
            if (entity instanceof Herbivore herbivore && herbivore.isAlive()) {
                herbivoreCount++;
            }
        }
        return herbivoreCount;
    }

    public int countAlivePredators() {
        int predatorCount = 0;
        for (Entity entity : cells.values()) {
            if (entity instanceof Predator predator && predator.isAlive()) {
                predatorCount++;
            }
        }
        return predatorCount;
    }
}
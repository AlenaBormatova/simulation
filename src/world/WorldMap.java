package world;

import entity.*;

import java.util.*;

public final class WorldMap {

    private final Map<Coordinates, Entity> cells = new HashMap<>();
    private final int width;
    private final int height;

    public WorldMap(int width, int height) {
        if (width <= 0 || height <= 0) throw new IllegalArgumentException("Invalid map size");
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }

    public boolean isValid(Coordinates position) {
        return position.x >= 0 && position.x < width && position.y >= 0 && position.y < height;
    }

    public Entity get(Coordinates position) {
        return cells.get(position);
    }

    public boolean isEmpty(Coordinates position) {
        return !cells.containsKey(position);
    }

    public boolean place(Entity entity) {
        Coordinates position = entity.getPosition();
        if (!isValid(position) || !isEmpty(position)) return false;
        cells.put(position, entity);
        return true;
    }

    public void remove(Coordinates position) {
        cells.remove(position);
    }

    public boolean moveEntity(Creature creature, Coordinates destination) {
        if (!isValid(destination) || !isEmpty(destination)) return false;

        Coordinates from = creature.getPosition();
        Entity occupant = cells.get(from);
        if (occupant != creature) {
            throw new IllegalStateException("Creature position out of sync with WorldMap: " + from);
        }

        cells.remove(from);
        creature.setPosition(destination);
        cells.put(destination, creature);
        return true;
    }

    public List<Creature> getAliveCreaturesSnapshot() {
        List<Creature> aliveCreatures = new ArrayList<>();
        for (Entity entity : cells.values()) {
            if (entity instanceof Creature creature && creature.isAlive()) {
                aliveCreatures.add(creature);
            }
        }
        return aliveCreatures;
    }

    // 8-направленные соседи (движение/поиск/соседство согласованы)
    public List<Coordinates> neighbors8(Coordinates p) {
        List<Coordinates> neighbors = new ArrayList<>(8);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                Coordinates neighborPosition = new Coordinates(p.x + dx, p.y + dy);
                if (isValid(neighborPosition)) neighbors.add(neighborPosition);
            }
        }
        return neighbors;
    }

    public List<Coordinates> freeNeighbors8(Coordinates position) {
        List<Coordinates> freeNeighborPositions = new ArrayList<>();
        for (Coordinates neighborPosition : neighbors8(position)) {
            if (isEmpty(neighborPosition)) freeNeighborPositions.add(neighborPosition);
        }
        return freeNeighborPositions;
    }

    public boolean areNeighbors8(Coordinates a, Coordinates b) {
        int dx = Math.abs(a.x - b.x);
        int dy = Math.abs(a.y - b.y);
        return (dx <= 1 && dy <= 1) && !(dx == 0 && dy == 0);
    }

    // Утилиты "рядом" для правильной логики "есть/атаковать через соседство"
    public Grass findAdjacentGrass8(Coordinates position) {
        for (Coordinates neighborPosition : neighbors8(position)) {
            Entity neighborEntity = get(neighborPosition);
            if (neighborEntity instanceof Grass grass) return grass;
        }
        return null;
    }

    public Herbivore findAdjacentHerbivore8(Coordinates position) {
        for (Coordinates neighborPosition : neighbors8(position)) {
            Entity neighborEntity = get(neighborPosition);
            if (neighborEntity instanceof Herbivore herbivore && herbivore.isAlive()) {
                return herbivore;
            }
        }
        return null;
    }

    public boolean isAdjacentToGrass8(Coordinates position) {
        for (Coordinates neighborPosition : neighbors8(position)) {
            if (get(neighborPosition) instanceof Grass) return true;
        }
        return false;
    }

    public boolean isAdjacentToAliveHerbivore8(Coordinates position) {
        for (Coordinates neighborPosition : neighbors8(position)) {
            Entity neighborEntity = get(neighborPosition);
            if (neighborEntity instanceof Herbivore herbivore && herbivore.isAlive()) {
                return true;
            }
        }
        return false;
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

    public Entity get(int x, int y) {
        return cells.get(new Coordinates(x, y));
    }
}

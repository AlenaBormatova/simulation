package entity;

import path.PathFinder;
import world.WorldMap;
import world.WorldMapNeighborhoods;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public final class Herbivore extends Creature {

    private static final int METABOLISM_PER_TURN = 1;
    private static final int HEAL_FROM_GRASS = 6;

    private static final double REPRODUCTION_HP_RATIO = 0.92;
    private static final int REPRODUCTION_HP_COST = 20;
    private static final double REPRODUCTION_CHANCE = 0.05;

    private final int maxHp;

    public Herbivore(int hp, int speed) {
        this(hp, hp, speed);
    }

    public Herbivore(int hp, int maxHp, int speed) {
        super(hp, speed);
        this.maxHp = Math.max(1, maxHp);
        setHp(hp);
    }

    @Override
    public void setHp(int hp) {
        this.hp = Math.max(0, Math.min(maxHp, hp));
    }

    @Override
    public void makeMove(WorldMap map, Coordinates position, Random random) {
        setHp(getHp() - METABOLISM_PER_TURN);
        if (!isAlive()) {
            map.remove(position);
            return;
        }

        Optional<WorldMapNeighborhoods.Located<Grass>> adjacent =
                WorldMapNeighborhoods.findAdjacent(map, position, Grass.class);

        if (adjacent.isPresent()) {
            map.remove(adjacent.orElseThrow().position());
            setHp(getHp() + HEAL_FROM_GRASS);
            tryReproduce(map, position, random);
            return;
        }

        List<Coordinates> path = PathFinder.findPathToNearest(
                map,
                position,
                candidate -> WorldMapNeighborhoods.isAdjacentTo(map, candidate, Grass.class)
        );

        if (path.size() > 1) {
            int steps = Math.min(speed, path.size() - 1);
            Coordinates newPos = path.get(steps);
            map.move(position, newPos);
            return;
        }

        List<Coordinates> freeNeighbors = WorldMapNeighborhoods.freeNeighbors8(map, position);
        if (!freeNeighbors.isEmpty()) {
            Coordinates nextPosition = freeNeighbors.get(random.nextInt(freeNeighbors.size()));
            map.move(position, nextPosition);
        }
    }

    private void tryReproduce(WorldMap map, Coordinates position, Random random) {
        int currentHp = getHp();
        int reproductionThreshold = (int) Math.ceil(maxHp * REPRODUCTION_HP_RATIO);

        if (currentHp < reproductionThreshold
                || currentHp <= REPRODUCTION_HP_COST
                || random.nextDouble() >= REPRODUCTION_CHANCE) {
            return;
        }

        List<Coordinates> freeNeighborPositions = WorldMapNeighborhoods.freeNeighbors8(map, position);
        if (freeNeighborPositions.isEmpty()) {
            return;
        }

        Coordinates childPosition = freeNeighborPositions.get(random.nextInt(freeNeighborPositions.size()));

        Herbivore child = new Herbivore(maxHp, maxHp, speed);
        if (map.place(childPosition, child)) {
            setHp(getHp() - REPRODUCTION_HP_COST);
        }
    }
}
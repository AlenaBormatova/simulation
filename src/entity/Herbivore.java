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

    public Herbivore(Coordinates position, int hp, int speed) {
        this(position, hp, hp, speed);
    }

    public Herbivore(Coordinates position, int hp, int maxHp, int speed) {
        super(position, hp, speed);
        this.maxHp = Math.max(1, maxHp);
        setHp(hp);
    }

    @Override
    public void setHp(int hp) {
        this.hp = Math.max(0, Math.min(maxHp, hp));
    }

    @Override
    public void makeMove(WorldMap map, Random random) {
        setHp(getHp() - METABOLISM_PER_TURN);
        if (!isAlive()) {
            map.remove(getPosition());
            return;
        }

        Optional<Grass> adjacent = WorldMapNeighborhoods.findAdjacent(map, getPosition(), Grass.class);
        if (adjacent.isPresent()) {
            Grass grass = adjacent.orElseThrow();
            map.remove(grass.getPosition());
            setHp(getHp() + HEAL_FROM_GRASS);
            tryReproduce(map, random);
            return;
        }

        List<Coordinates> path = PathFinder.findPathToNearest(
                map,
                getPosition(),
                position -> WorldMapNeighborhoods.isAdjacentTo(map, position, Grass.class)
        );

        if (path.size() > 1) {
            int steps = Math.min(speed, path.size() - 1);
            Coordinates newPos = path.get(steps);
            map.moveEntity(this, newPos);
            return;
        }

        List<Coordinates> freeNeighbors = WorldMapNeighborhoods.freeNeighbors8(map, getPosition());
        if (!freeNeighbors.isEmpty()) {
            Coordinates nextPosition = freeNeighbors.get(random.nextInt(freeNeighbors.size()));
            map.moveEntity(this, nextPosition);
        }
    }

    private void tryReproduce(WorldMap map, Random random) {
        int currentHp = getHp();
        int reproductionThreshold = (int) Math.ceil(maxHp * REPRODUCTION_HP_RATIO);

        if (currentHp < reproductionThreshold
                || currentHp <= REPRODUCTION_HP_COST
                || random.nextDouble() >= REPRODUCTION_CHANCE) {
            return;
        }

        List<Coordinates> freeNeighborPositions = WorldMapNeighborhoods.freeNeighbors8(map, getPosition());
        if (freeNeighborPositions.isEmpty()) {
            return;
        }

        Coordinates childPosition = freeNeighborPositions.get(random.nextInt(freeNeighborPositions.size()));

        Herbivore child = new Herbivore(childPosition, maxHp, maxHp, speed);
        if (map.place(child)) {
            setHp(getHp() - REPRODUCTION_HP_COST);
        }
    }
}
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

    public Herbivore(int hp, int speed) {
        this(hp, hp, speed);
    }

    public Herbivore(int hp, int maxHp, int speed) {
        super(hp, maxHp, speed);
    }

    @Override
    public void makeMove(WorldMap map, Coordinates currentPosition, Random random) {
        setHp(getHp() - METABOLISM_PER_TURN);
        if (!isAlive()) {
            map.remove(currentPosition);
            return;
        }

        Optional<WorldMapNeighborhoods.Positioned<Grass>> adjacentGrass =
                WorldMapNeighborhoods.findAdjacent(map, currentPosition, Grass.class);

        if (adjacentGrass.isPresent()) {
            WorldMapNeighborhoods.Positioned<Grass> grassTarget = adjacentGrass.orElseThrow();
            map.remove(grassTarget.position());
            setHp(getHp() + HEAL_FROM_GRASS);
            tryReproduce(map, currentPosition, random);
            return;
        }

        List<Coordinates> path = PathFinder.findPathToNearest(
                map,
                currentPosition,
                position -> WorldMapNeighborhoods.isAdjacentTo(map, position, Grass.class)
        );

        if (path.size() > 1) {
            int steps = Math.min(speed, path.size() - 1);
            Coordinates destination = path.get(steps);
            map.moveEntity(currentPosition, destination);
            return;
        }

        List<Coordinates> emptyNeighborPositions =
                WorldMapNeighborhoods.emptyNeighbors8(map, currentPosition);

        if (!emptyNeighborPositions.isEmpty()) {
            Coordinates destination =
                    emptyNeighborPositions.get(random.nextInt(emptyNeighborPositions.size()));
            map.moveEntity(currentPosition, destination);
        }
    }

    @Override
    protected double getReproductionHpRatio() {
        return REPRODUCTION_HP_RATIO;
    }

    @Override
    protected int getReproductionHpCost() {
        return REPRODUCTION_HP_COST;
    }

    @Override
    protected double getReproductionChance() {
        return REPRODUCTION_CHANCE;
    }

    @Override
    protected Creature createChild() {
        return new Herbivore(maxHp, maxHp, speed);
    }
}
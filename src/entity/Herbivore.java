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
    public void makeMove(WorldMap worldMap, Coordinates currentPosition, Random random) {
        setHp(getHp() - METABOLISM_PER_TURN);
        if (!isAlive()) {
            worldMap.remove(currentPosition);
            return;
        }

        Optional<WorldMapNeighborhoods.Positioned<Grass>> adjacentGrass =
                WorldMapNeighborhoods.findAdjacent(worldMap, currentPosition, Grass.class);

        if (adjacentGrass.isPresent()) {
            WorldMapNeighborhoods.Positioned<Grass> grassTarget = adjacentGrass.orElseThrow();
            worldMap.remove(grassTarget.position());
            setHp(getHp() + HEAL_FROM_GRASS);
            if (isReadyToReproduce()
                    && hasEmptyNeighbor(worldMap, currentPosition)
                    && random.nextDouble() < getReproductionChance()) {
                reproduce(worldMap, currentPosition, random);
            }
            return;
        }

        List<Coordinates> path = PathFinder.find(
                worldMap,
                currentPosition,
                position -> WorldMapNeighborhoods.isAdjacentTo(worldMap, position, Grass.class)
        );

        if (path.size() > 1) {
            int steps = Math.min(speed, path.size() - 1);
            Coordinates destination = path.get(steps);
            worldMap.moveEntity(currentPosition, destination);
            return;
        }

        List<Coordinates> emptyNeighborPositions =
                WorldMapNeighborhoods.emptyNeighbors8(worldMap, currentPosition);

        if (!emptyNeighborPositions.isEmpty()) {
            Coordinates destination =
                    emptyNeighborPositions.get(random.nextInt(emptyNeighborPositions.size()));
            worldMap.moveEntity(currentPosition, destination);
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
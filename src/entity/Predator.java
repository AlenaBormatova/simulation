package entity;

import path.PathFinder;
import world.WorldMap;
import world.WorldMapNeighborhoods;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public final class Predator extends Creature {

    private static final int METABOLISM_PER_TURN = 2;
    private static final int HEAL_ON_KILL = 18;

    private static final double REPRODUCTION_HP_RATIO = 0.72;
    private static final int REPRODUCTION_HP_COST = 12;
    private static final double REPRODUCTION_CHANCE = 0.30;

    private final int attack;

    public Predator(int hp, int speed, int attack) {
        this(hp, hp, speed, attack);
    }

    public Predator(int hp, int maxHp, int speed, int attack) {
        super(hp, maxHp, speed);
        this.attack = attack;
    }

    @Override
    public void makeMove(WorldMap worldMap) {
        Coordinates currentPosition = getCurrentPosition(worldMap);

        setHp(getHp() - METABOLISM_PER_TURN);
        if (!isAlive()) {
            worldMap.removeEntity(currentPosition);
            return;
        }

        Optional<WorldMapNeighborhoods.Positioned<Herbivore>> adjacentHerbivore =
                WorldMapNeighborhoods.findAdjacent(
                        worldMap,
                        currentPosition,
                        Herbivore.class,
                        Herbivore::isAlive
                );

        if (adjacentHerbivore.isPresent()) {
            WorldMapNeighborhoods.Positioned<Herbivore> target = adjacentHerbivore.orElseThrow();
            Herbivore prey = target.entity();

            prey.setHp(prey.getHp() - attack);

            if (!prey.isAlive()) {
                worldMap.removeEntity(target.position());
                setHp(getHp() + HEAL_ON_KILL);

                if (isReadyToReproduce()
                        && hasEmptyNeighbor(worldMap)
                        && ThreadLocalRandom.current().nextDouble() < getReproductionChance()) {
                    reproduce(worldMap);
                }
            }

            return;
        }

        List<Coordinates> path = PathFinder.find(
                worldMap,
                currentPosition,
                position -> WorldMapNeighborhoods.isAdjacentTo(
                        worldMap,
                        position,
                        Herbivore.class,
                        Herbivore::isAlive
                )
        );

        if (path.size() <= 1) {
            List<Coordinates> emptyNeighborPositions =
                    WorldMapNeighborhoods.emptyNeighbors8(worldMap, currentPosition);

            if (!emptyNeighborPositions.isEmpty()) {
                Coordinates destination = emptyNeighborPositions.get(
                        ThreadLocalRandom.current().nextInt(emptyNeighborPositions.size())
                );
                move(worldMap, currentPosition, destination);
            }
            return;
        }

        int steps = Math.min(speed, path.size() - 1);
        Coordinates destination = path.get(steps);
        move(worldMap, currentPosition, destination);
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
        return new Predator(maxHp, maxHp, speed, attack);
    }
}
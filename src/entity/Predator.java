package entity;

import path.PathFinder;
import world.WorldMap;
import world.WorldMapNeighborhoods;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public final class Predator extends Creature {

    private static final int METABOLISM_PER_TURN = 2;
    private static final int HEAL_ON_KILL = 18;

    private static final double REPRODUCTION_HP_RATIO = 0.72;
    private static final int REPRODUCTION_HP_COST = 12;
    private static final double REPRODUCTION_CHANCE = 0.30;

    private final int attack;
    private final int maxHp;

    public Predator(int hp, int speed, int attack) {
        super(hp, speed);
        this.attack = attack;
        this.maxHp = Math.max(1, hp);
        setHp(hp);
    }

    @Override
    public void setHp(int hp) {
        this.hp = Math.max(0, Math.min(maxHp, hp));
    }

    @Override
    public void makeMove(WorldMap map, Coordinates currentPosition, Random random) {
        setHp(getHp() - METABOLISM_PER_TURN);
        if (!isAlive()) {
            map.remove(currentPosition);
            return;
        }

        Optional<WorldMapNeighborhoods.Positioned<Herbivore>> adjacentHerbivore =
                WorldMapNeighborhoods.findAdjacent(
                        map,
                        currentPosition,
                        Herbivore.class,
                        Herbivore::isAlive
                );

        if (adjacentHerbivore.isPresent()) {
            WorldMapNeighborhoods.Positioned<Herbivore> target = adjacentHerbivore.orElseThrow();
            Herbivore prey = target.entity();

            prey.setHp(prey.getHp() - attack);

            if (!prey.isAlive()) {
                map.remove(target.position());
                setHp(getHp() + HEAL_ON_KILL);
                tryReproduce(map, currentPosition, random);
            }
            return;
        }

        List<Coordinates> path = PathFinder.findPathToNearest(
                map,
                currentPosition,
                position -> WorldMapNeighborhoods.isAdjacentTo(
                        map,
                        position,
                        Herbivore.class,
                        Herbivore::isAlive
                )
        );

        if (path.size() <= 1) {
            List<Coordinates> emptyNeighborPositions =
                    WorldMapNeighborhoods.emptyNeighbors8(map, currentPosition);

            if (!emptyNeighborPositions.isEmpty()) {
                Coordinates destination =
                        emptyNeighborPositions.get(random.nextInt(emptyNeighborPositions.size()));
                map.moveEntity(currentPosition, destination);
            }
            return;
        }

        int steps = Math.min(speed, path.size() - 1);
        Coordinates destination = path.get(steps);
        map.moveEntity(currentPosition, destination);
    }

    private void tryReproduce(WorldMap map, Coordinates currentPosition, Random random) {
        int currentHp = getHp();
        int reproductionThreshold = (int) Math.ceil(maxHp * REPRODUCTION_HP_RATIO);

        if (currentHp < reproductionThreshold
                || currentHp <= REPRODUCTION_HP_COST
                || random.nextDouble() >= REPRODUCTION_CHANCE) {
            return;
        }

        List<Coordinates> emptyNeighborPositions =
                WorldMapNeighborhoods.emptyNeighbors8(map, currentPosition);

        if (emptyNeighborPositions.isEmpty()) {
            return;
        }

        Coordinates childSpawnPosition = emptyNeighborPositions.get(random.nextInt(emptyNeighborPositions.size()));

        Predator child = new Predator(maxHp, speed, attack);
        if (map.placeEntity(childSpawnPosition, child)) {
            setHp(currentHp - REPRODUCTION_HP_COST);
        }
    }
}
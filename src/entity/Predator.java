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
    public void makeMove(WorldMap map, Coordinates position, Random random) {
        setHp(getHp() - METABOLISM_PER_TURN);
        if (!isAlive()) {
            map.remove(position);
            return;
        }

        Optional<WorldMapNeighborhoods.Located<Herbivore>> adjacent =
                WorldMapNeighborhoods.findAdjacent(
                        map,
                        position,
                        Herbivore.class,
                        Herbivore::isAlive
                );

        if (adjacent.isPresent()) {
            Herbivore target = adjacent.orElseThrow().entity();
            Coordinates targetPosition = adjacent.orElseThrow().position();

            target.setHp(target.getHp() - attack);

            if (!target.isAlive()) {
                map.remove(targetPosition);
                setHp(getHp() + HEAL_ON_KILL);
                tryReproduce(map, position, random);
            }
            return;
        }

        List<Coordinates> path = PathFinder.findPathToNearest(
                map,
                position,
                candidate -> WorldMapNeighborhoods.isAdjacentTo(map, candidate, Herbivore.class, Herbivore::isAlive)
        );

        if (path.size() <= 1) {
            List<Coordinates> freeNeighbors = WorldMapNeighborhoods.freeNeighbors8(map, position);
            if (!freeNeighbors.isEmpty()) {
                Coordinates nextPosition = freeNeighbors.get(random.nextInt(freeNeighbors.size()));
                map.move(position, nextPosition);
            }
            return;
        }

        int steps = Math.min(speed, path.size() - 1);
        Coordinates newPosition = path.get(steps);
        map.move(position, newPosition);
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

        Predator child = new Predator(maxHp, speed, attack);
        if (map.place(childPosition, child)) {
            setHp(currentHp - REPRODUCTION_HP_COST);
        }
    }
}
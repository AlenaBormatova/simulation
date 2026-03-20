package entity;

import path.PathFinder;
import world.WorldMap;

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

    public Predator(Coordinates position, int hp, int speed, int attack) {
        super(position, hp, speed);
        this.attack = attack;
        this.maxHp = Math.max(1, hp);
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

        Optional<Herbivore> adjacent = map.findAdjacentHerbivore8(getPosition());
        if (adjacent.isPresent()) {
            Herbivore adjacentHerbivore = adjacent.orElseThrow();
            adjacentHerbivore.setHp(adjacentHerbivore.getHp() - attack);

            if (!adjacentHerbivore.isAlive()) {
                map.remove(adjacentHerbivore.getPosition());
                setHp(getHp() + HEAL_ON_KILL);
                tryReproduce(map, random);
            }
            return;
        }

        List<Coordinates> path = PathFinder.findPathToNearest(
                map,
                getPosition(),
                map::isAdjacentToAliveHerbivore8
        );

        if (path.size() <= 1) {
            List<Coordinates> freeNeighbors = map.freeNeighbors8(getPosition());
            if (!freeNeighbors.isEmpty()) {
                Coordinates nextPosition = freeNeighbors.get(random.nextInt(freeNeighbors.size()));
                map.moveEntity(this, nextPosition);
            }
            return;
        }

        int steps = Math.min(speed, path.size() - 1);
        Coordinates newPosition = path.get(steps);
        map.moveEntity(this, newPosition);
    }

    private void tryReproduce(WorldMap map, Random random) {
        int currentHp = getHp();
        int reproductionThreshold = (int) Math.ceil(maxHp * REPRODUCTION_HP_RATIO);

        if (currentHp < reproductionThreshold
                || currentHp <= REPRODUCTION_HP_COST
                || random.nextDouble() >= REPRODUCTION_CHANCE) {
            return;
        }

        List<Coordinates> freeNeighborPositions = map.freeNeighbors8(getPosition());
        if (freeNeighborPositions.isEmpty()) {
            return;
        }

        Coordinates childPosition = freeNeighborPositions.get(random.nextInt(freeNeighborPositions.size()));

        Predator child = new Predator(childPosition, maxHp, speed, attack);
        if (map.place(child)) {
            setHp(currentHp - REPRODUCTION_HP_COST);
        }
    }

    @Override
    public String getGlyph() {
        return "\uD83D\uDC3A";
    }
}
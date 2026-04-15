package entity;

import world.WorldMap;
import world.WorldMapNeighborhoods;

public class Predator extends Creature {

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
    protected int getMetabolismPerTurn() {
        return METABOLISM_PER_TURN;
    }

    @Override
    protected boolean hasAdjacentFood(WorldMap worldMap) {
        Coordinates currentPosition = getCurrentPosition(worldMap);

        return WorldMapNeighborhoods.findAdjacent(
                worldMap,
                currentPosition,
                Herbivore.class,
                Herbivore::isAlive
        ).isPresent();
    }

    @Override
    protected void eatAdjacentFood(WorldMap worldMap) {
        Coordinates currentPosition = getCurrentPosition(worldMap);

        WorldMapNeighborhoods.Positioned<Herbivore> target =
                WorldMapNeighborhoods.findAdjacent(
                        worldMap,
                        currentPosition,
                        Herbivore.class,
                        Herbivore::isAlive
                ).orElseThrow(() -> new IllegalStateException("Adjacent herbivore not found"));

        Herbivore prey = target.entity();
        prey.setHp(prey.getHp() - attack);

        if (!prey.isAlive()) {
            worldMap.removeEntity(target.position());
            setHp(getHp() + HEAL_ON_KILL);
        }
    }

    @Override
    protected boolean isFoodAdjacent(WorldMap worldMap, Coordinates position) {
        return WorldMapNeighborhoods.isAdjacentTo(
                worldMap,
                position,
                Herbivore.class,
                Herbivore::isAlive
        );
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
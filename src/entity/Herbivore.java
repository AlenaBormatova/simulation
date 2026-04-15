package entity;

import world.WorldMap;
import world.WorldMapNeighborhoods;

public class Herbivore extends Creature {

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
    protected int getMetabolismPerTurn() {
        return METABOLISM_PER_TURN;
    }

    @Override
    protected boolean hasAdjacentFood(WorldMap worldMap) {
        Coordinates currentPosition = getCurrentPosition(worldMap);
        return WorldMapNeighborhoods.findAdjacent(worldMap, currentPosition, Grass.class).isPresent();
    }

    @Override
    protected void eatAdjacentFood(WorldMap worldMap) {
        Coordinates currentPosition = getCurrentPosition(worldMap);

        WorldMapNeighborhoods.Positioned<Grass> grassTarget =
                WorldMapNeighborhoods.findAdjacent(worldMap, currentPosition, Grass.class)
                        .orElseThrow(() -> new IllegalStateException("Adjacent grass not found"));

        worldMap.removeEntity(grassTarget.position());
        setHp(getHp() + HEAL_FROM_GRASS);
    }

    @Override
    protected boolean isFoodAdjacent(WorldMap worldMap, Coordinates position) {
        return WorldMapNeighborhoods.isAdjacentTo(worldMap, position, Grass.class);
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
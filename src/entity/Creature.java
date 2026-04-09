package entity;

import world.WorldMap;
import world.WorldMapNeighborhoods;

import java.util.List;
import java.util.Random;

public abstract class Creature extends Entity {

    protected int hp;
    protected final int maxHp;
    protected final int speed;

    public Creature(int hp, int maxHp, int speed) {
        this.maxHp = Math.max(1, maxHp);
        this.speed = speed;
        setHp(hp);
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = Math.max(0, Math.min(maxHp, hp));
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public abstract void makeMove(WorldMap worldMap, Coordinates position, Random random);

    protected final boolean isReadyToReproduce() {
        int reproductionThreshold = (int) Math.ceil(maxHp * getReproductionHpRatio());
        return getHp() >= reproductionThreshold && getHp() > getReproductionHpCost();
    }

    protected final boolean hasEmptyNeighbor(WorldMap worldMap, Coordinates currentPosition) {
        return !WorldMapNeighborhoods.emptyNeighbors8(worldMap, currentPosition).isEmpty();
    }

    protected final void reproduce(WorldMap worldMap, Coordinates currentPosition, Random random) {
        if (!isReadyToReproduce()) {
            throw new IllegalStateException("Creature is not ready to reproduce");
        }

        List<Coordinates> emptyNeighborPositions =
                WorldMapNeighborhoods.emptyNeighbors8(worldMap, currentPosition);

        if (emptyNeighborPositions.isEmpty()) {
            throw new IllegalStateException("No empty neighbor cell for reproduction");
        }

        Coordinates childSpawnPosition = emptyNeighborPositions.get(random.nextInt(emptyNeighborPositions.size()));

        Creature child = createChild();

        if (!worldMap.put(childSpawnPosition, child)) {
            throw new IllegalStateException("Cannot place child at " + childSpawnPosition);
        }

        setHp(getHp() - getReproductionHpCost());
    }

    protected abstract double getReproductionHpRatio();

    protected abstract int getReproductionHpCost();

    protected abstract double getReproductionChance();

    protected abstract Creature createChild();
}
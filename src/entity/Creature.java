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

    public abstract void makeMove(WorldMap map, Coordinates position, Random random);

    protected final void tryReproduce(WorldMap map, Coordinates currentPosition, Random random) {
        int currentHp = getHp();
        int reproductionThreshold = (int) Math.ceil(maxHp * getReproductionHpRatio());

        if (currentHp < reproductionThreshold
                || currentHp <= getReproductionHpCost()
                || random.nextDouble() >= getReproductionChance()) {
            return;
        }

        List<Coordinates> emptyNeighborPositions =
                WorldMapNeighborhoods.emptyNeighbors8(map, currentPosition);

        if (emptyNeighborPositions.isEmpty()) {
            return;
        }

        Coordinates childSpawnPosition = emptyNeighborPositions.get(random.nextInt(emptyNeighborPositions.size()));

        Creature child = createChild();
        if (map.placeEntity(childSpawnPosition, child)) {
            setHp(currentHp - getReproductionHpCost());
        }
    }

    protected abstract double getReproductionHpRatio();

    protected abstract int getReproductionHpCost();

    protected abstract double getReproductionChance();

    protected abstract Creature createChild();
}
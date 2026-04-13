package entity;

import world.WorldMap;
import world.WorldMapNeighborhoods;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Creature extends Entity {

    protected final int maxHp;
    protected final int speed;
    protected int hp;

    public Creature(int hp, int maxHp, int speed) {
        validateCharacteristics(hp, maxHp, speed);
        this.hp = hp;
        this.maxHp = maxHp;
        this.speed = speed;
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

    public abstract void makeMove(WorldMap worldMap);

    protected final boolean isReadyToReproduce() {
        int reproductionThreshold = (int) Math.ceil(maxHp * getReproductionHpRatio());
        return hp >= reproductionThreshold && hp > getReproductionHpCost();
    }

    protected final Coordinates getCurrentPosition(WorldMap worldMap) {
        return worldMap.findPositionOf(this).orElseThrow(() -> {
            String message = "%s is not placed on the map".formatted(getClass().getSimpleName());
            return new IllegalStateException(message);
        });
    }

    protected final boolean hasEmptyNeighbor(WorldMap worldMap) {
        Coordinates currentPosition = getCurrentPosition(worldMap);
        return !WorldMapNeighborhoods.emptyNeighbors8(worldMap, currentPosition).isEmpty();
    }

    protected final void reproduce(WorldMap worldMap) {
        if (!isReadyToReproduce()) {
            throw new IllegalStateException("Creature is not ready to reproduce");
        }

        Coordinates currentPosition = getCurrentPosition(worldMap);
        List<Coordinates> emptyNeighborPositions =
                WorldMapNeighborhoods.emptyNeighbors8(worldMap, currentPosition);

        if (emptyNeighborPositions.isEmpty()) {
            throw new IllegalStateException("No empty neighbor cell for reproduction");
        }

        Coordinates childSpawnPosition = emptyNeighborPositions.get(
                ThreadLocalRandom.current().nextInt(emptyNeighborPositions.size())
        );

        Creature child = createChild();

        worldMap.placeEntity(childSpawnPosition, child);
        setHp(hp - getReproductionHpCost());
    }

    protected final void move(WorldMap worldMap, Coordinates from, Coordinates to) {
        worldMap.validate(from);
        worldMap.validate(to);

        if (from.equals(to)) {
            String message = "Cannot move %s: source and destination are the same %s"
                    .formatted(getClass().getSimpleName(), from);
            throw new IllegalArgumentException(message);
        }

        Entity entityAtSource = worldMap.get(from).orElseThrow(() -> {
            String message = "Cannot move %s from %s: source cell is empty"
                    .formatted(getClass().getSimpleName(), from);
            return new IllegalStateException(message);
        });

        if (entityAtSource != this) {
            String message = "Cannot move %s from %s: source cell contains %s"
                    .formatted(
                            getClass().getSimpleName(),
                            from,
                            entityAtSource.getClass().getSimpleName()
                    );
            throw new IllegalStateException(message);
        }

        if (!worldMap.isEmpty(to)) {
            String message = "Cannot move %s from %s to %s: destination cell is occupied"
                    .formatted(getClass().getSimpleName(), from, to);
            throw new IllegalStateException(message);
        }

        worldMap.removeEntity(from);
        worldMap.placeEntity(to, this);
    }

    protected abstract double getReproductionHpRatio();

    protected abstract int getReproductionHpCost();

    protected abstract double getReproductionChance();

    protected abstract Creature createChild();

    private void validateCharacteristics(int hp, int maxHp, int speed) {
        if (maxHp <= 0) {
            throw new IllegalArgumentException("maxHp must be greater than 0: " + maxHp);
        }

        if (hp <= 0 || hp > maxHp) {
            String message = "hp must be in range [1, %d]: %d".formatted(maxHp, hp);
            throw new IllegalArgumentException(message);
        }

        if (speed <= 0) {
            throw new IllegalArgumentException("speed must be greater than 0: " + speed);
        }
    }
}
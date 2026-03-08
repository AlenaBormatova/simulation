package entity;

import path.PathFinder;
import world.WorldMap;

import java.util.List;
import java.util.Random;

public final class Herbivore extends Creature {

    private static final int METABOLISM_PER_TURN = 1;
    private static final int HEAL_FROM_GRASS = 8;

    private static final double REPRODUCTION_HP_RATIO = 0.92;
    private static final int REPRODUCTION_HP_COST = 16;
    private static final double REPRODUCTION_CHANCE = 0.08;

    private final int maxHp;

    public Herbivore(Coordinates position, int hp, int speed) {
        this(position, hp, hp, speed);
    }

    public Herbivore(Coordinates position, int hp, int maxHp, int speed) {
        super(position, hp, speed);
        this.maxHp = Math.max(1, maxHp);
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

        // 1) Если трава рядом — съесть (ход потрачен)
        Grass adjacent = map.findAdjacentGrass8(getPosition());
        if (adjacent != null) {
            map.remove(adjacent.getPosition());
            setHp(getHp() + HEAL_FROM_GRASS);
            tryReproduce(map, random);
            return;
        }

        // 2) Один BFS: ищем ближайшую ПУСТУЮ клетку, соседнюю с травой
        List<Coordinates> path = PathFinder.findPathToNearest(
                map,
                getPosition(),
                map::isAdjacentToGrass8 // цель: пустая клетка рядом с травой
        );

        if (path != null && path.size() > 1) {
            int steps = Math.min(speed, path.size() - 1);
            Coordinates newPos = path.get(steps);
            map.moveEntity(this, newPos);
            return;
        }

        // 3) Если цели нет (травы нет или не достижима) — случайный шаг
        List<Coordinates> free = map.freeNeighbors8(getPosition());
        if (!free.isEmpty()) {
            Coordinates next = free.get(random.nextInt(free.size()));
            map.moveEntity(this, next);
        }
    }

    private void tryReproduce(WorldMap map, Random random) {
        if (hp < Math.ceil(maxHp * REPRODUCTION_HP_RATIO)
                || hp <= REPRODUCTION_HP_COST
                || random.nextDouble() >= REPRODUCTION_CHANCE) {
            return;
        }

        List<Coordinates> freeNeighborPositions = map.freeNeighbors8(getPosition());
        if (freeNeighborPositions.isEmpty()) {
            return;
        }

        Coordinates childPosition = freeNeighborPositions.get(random.nextInt(freeNeighborPositions.size()));
        Herbivore child = new Herbivore(childPosition, maxHp, maxHp, speed);
        if (map.place(child)) {
            setHp(getHp() - REPRODUCTION_HP_COST);
        }
    }

    @Override public String getGlyph() {
        return "\uD83D\uDC07";
    }
}

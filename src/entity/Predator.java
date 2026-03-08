package entity;

import path.PathFinder;
import world.WorldMap;

import java.util.List;
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
    }

    public int getAttack() {
        return attack;
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

        // 1) Если жертва рядом — атакуем (ход потрачен)
        Herbivore adjacent = map.findAdjacentHerbivore8(getPosition());
        if (adjacent != null) {
            adjacent.setHp(adjacent.getHp() - attack);

            if (!adjacent.isAlive()) {
                map.remove(adjacent.getPosition());
                setHp(getHp() + HEAL_ON_KILL);
                tryReproduce(map, random);
            }
            return;
        }

        // 2) Один BFS: ищем ближайшую ПУСТУЮ клетку, соседнюю с живым травоядным
        List<Coordinates> path = PathFinder.findPathToNearest(
                map,
                getPosition(),
                map::isAdjacentToAliveHerbivore8
        );

        // Если путь недоступен (или мы уже "на месте"), не стоим на месте:
        // делаем свободный случайный шаг, иначе волк просто умирает от метаболизма
        if (path == null || path.size() <= 1) {
            List<Coordinates> free = map.freeNeighbors8(getPosition());
            if (!free.isEmpty()) {
                Coordinates next = free.get(random.nextInt(free.size()));
                map.moveEntity(this, next);
            }
            return;
        }

        // 3) Двигаемся
        int steps = Math.min(speed, path.size() - 1);
        Coordinates newPos = path.get(steps);
        map.moveEntity(this, newPos);
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
        Predator child = new Predator(childPosition, maxHp, speed, attack);
        if (map.place(child)) {
            setHp(getHp() - REPRODUCTION_HP_COST);
        }
    }

    @Override
    public String getGlyph() {
        return "\uD83D\uDC3A";
    }
}
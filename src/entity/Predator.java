package entity;

import path.PathFinder;
import world.WorldMap;

import java.util.List;
import java.util.Random;

public final class Predator extends Creature {

    private static final int METABOLISM_PER_TURN = 2;
    private static final int HEAL_ON_KILL = 18;

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
            }
            return;
        }

        // 2) Один BFS: ищем ближайшую ПУСТУЮ клетку, соседнюю с живым травоядным
        List<Coordinates> path = PathFinder.findPathToNearest(
                map,
                getPosition(),
                map::isAdjacentToAliveHerbivore8
        );

        if (path == null || path.size() <= 1) return;

        // 3) Двигаемся
        int steps = Math.min(speed, path.size() - 1);
        Coordinates newPos = path.get(steps);
        map.moveEntity(this, newPos);
    }


    @Override public String getGlyph() {
        return "\uD83D\uDC3A";
    }
}
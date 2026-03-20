package entity;

import world.WorldMap;

import java.util.Random;

public abstract class Creature extends Entity {

    protected int hp;
    protected final int speed;

    public Creature(Coordinates position, int hp, int speed) {
        super(position);
        this.hp = hp;
        this.speed = speed;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = Math.max(0, hp);
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public abstract void makeMove(WorldMap map, Random random);
}
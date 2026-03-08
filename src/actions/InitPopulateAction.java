package actions;

import entity.*;
import world.WorldMap;

import java.util.Random;
import java.util.function.Function;

public final class InitPopulateAction implements Action {

    @Override
    public void execute(WorldMap map, Random random, int turn) {
        int area = map.getWidth() * map.getHeight();

        int rocks = Math.max(1, area / 20);
        int trees = Math.max(1, area / 33);
        int grass = Math.max(1, area / 12);
        int herbivores = Math.max(1, area / 25);
        int predators = Math.max(1, area / 75);

        Function<Entity, Boolean> placeRandom = (entity) -> {
            for (int tries = 0; tries < area * 50; tries++) {
                Coordinates p = new Coordinates(random.nextInt(map.getWidth()), random.nextInt(map.getHeight()));
                entity.setPosition(p);
                if (map.place(entity)) return true;
            }
            return false;
        };

        for (int i = 0; i < rocks; i++) placeRandom.apply(new Rock(new Coordinates(0, 0)));
        for (int i = 0; i < trees; i++) placeRandom.apply(new Tree(new Coordinates(0, 0)));
        for (int i = 0; i < grass; i++) placeRandom.apply(new Grass(new Coordinates(0, 0)));

        final int herbHp = 50;
        final int herbSatiety = 60;
        final int herbSpeed = 1;

        final int predHp = 70;
        final int predSpeed = 1;
        final int predAttack = 8;

        for (int i = 0; i < herbivores; i++) {
            placeRandom.apply(new Herbivore(new Coordinates(0, 0), herbHp, herbSatiety, herbSpeed));
        }
        for (int i = 0; i < predators; i++) {
            placeRandom.apply(new Predator(new Coordinates(0, 0), predHp, predSpeed, predAttack));
        }
    }
}

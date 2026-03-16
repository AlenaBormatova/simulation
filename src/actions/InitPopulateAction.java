package actions;

import entity.Coordinates;
import entity.Entity;
import entity.Grass;
import entity.Herbivore;
import entity.Predator;
import entity.Rock;
import entity.Tree;
import world.WorldMap;

import java.util.Random;

public final class InitPopulateAction implements Action {

    private static final int ROCK_DENSITY_DIVISOR = 20;
    private static final int TREE_DENSITY_DIVISOR = 33;
    private static final int GRASS_DENSITY_DIVISOR = 12;
    private static final int HERBIVORE_DENSITY_DIVISOR = 25;
    private static final int PREDATOR_DENSITY_DIVISOR = 75;

    private static final int MAX_PLACEMENT_ATTEMPTS_MULTIPLIER = 50;

    private static final int HERBIVORE_HP = 50;
    private static final int HERBIVORE_SATIETY = 60;
    private static final int HERBIVORE_SPEED = 1;

    private static final int PREDATOR_HP = 70;
    private static final int PREDATOR_SPEED = 1;
    private static final int PREDATOR_ATTACK = 8;

    @Override
    public void execute(WorldMap map, Random random, int turn) {
        int area = map.getArea();

        int rocks = density(area, ROCK_DENSITY_DIVISOR);
        int trees = density(area, TREE_DENSITY_DIVISOR);
        int grass = density(area, GRASS_DENSITY_DIVISOR);
        int herbivores = density(area, HERBIVORE_DENSITY_DIVISOR);
        int predators = density(area, PREDATOR_DENSITY_DIVISOR);

        spawnMany(map, random, rocks, Rock::new);
        spawnMany(map, random, trees, Tree::new);
        spawnMany(map, random, grass, Grass::new);

        spawnMany(map, random, herbivores, this::createHerbivore);
        spawnMany(map, random, predators, this::createPredator);
    }

    private void spawnMany(WorldMap map, Random random, int count, SpawnFactory factory) {
        for (int i = 0; i < count; i++) {
            tryPlace(map, random, factory);
        }
    }

    private boolean tryPlace(WorldMap map, Random random, SpawnFactory factory) {
        int area = map.getArea();
        int maxAttempts = area * MAX_PLACEMENT_ATTEMPTS_MULTIPLIER;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            Coordinates position = new Coordinates(
                    random.nextInt(map.getWidth()),
                    random.nextInt(map.getHeight())
            );

            if (map.isEmpty(position)) {
                map.place(factory.create(position));
                return true;
            }
        }

        return false;
    }

    private Entity createHerbivore(Coordinates position) {
        return new Herbivore(position, HERBIVORE_HP, HERBIVORE_SATIETY, HERBIVORE_SPEED);
    }

    private Entity createPredator(Coordinates position) {
        return new Predator(position, PREDATOR_HP, PREDATOR_SPEED, PREDATOR_ATTACK);
    }

    private int density(int area, int divisor) {
        return Math.max(1, area / divisor);
    }

    @FunctionalInterface
    private interface SpawnFactory {
        Entity create(Coordinates position);
    }
}
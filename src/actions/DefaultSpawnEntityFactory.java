package actions;

import entity.Herbivore;
import entity.Predator;

public final class DefaultSpawnEntityFactory extends SpawnEntityFactory {

    private static final int INITIAL_HERBIVORE_HP = 50;
    private static final int INITIAL_HERBIVORE_SATIETY = 60;
    private static final int INITIAL_HERBIVORE_SPEED = 1;

    private static final int INITIAL_PREDATOR_HP = 70;
    private static final int INITIAL_PREDATOR_SPEED = 1;
    private static final int INITIAL_PREDATOR_ATTACK = 8;

    @Override
    public Herbivore createHerbivore() {
        return new Herbivore(
                INITIAL_HERBIVORE_HP,
                INITIAL_HERBIVORE_SATIETY,
                INITIAL_HERBIVORE_SPEED
        );
    }

    @Override
    public Predator createPredator() {
        return new Predator(
                INITIAL_PREDATOR_HP,
                INITIAL_PREDATOR_SPEED,
                INITIAL_PREDATOR_ATTACK
        );
    }
}
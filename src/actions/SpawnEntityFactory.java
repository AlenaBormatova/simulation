package actions;

import entity.Coordinates;
import entity.Grass;
import entity.Herbivore;
import entity.Predator;
import entity.Rock;
import entity.Tree;

public final class SpawnEntityFactory {

    private static final int INITIAL_HERBIVORE_HP = 50;
    private static final int INITIAL_HERBIVORE_SATIETY = 60;
    private static final int INITIAL_HERBIVORE_SPEED = 1;

    private static final int INITIAL_PREDATOR_HP = 70;
    private static final int INITIAL_PREDATOR_SPEED = 1;
    private static final int INITIAL_PREDATOR_ATTACK = 8;

    private final SpawnBalanceConfig balanceConfig;

    public SpawnEntityFactory() {
        this(SpawnBalanceConfig.DEFAULT);
    }

    public SpawnEntityFactory(SpawnBalanceConfig balanceConfig) {
        this.balanceConfig = balanceConfig;
    }

    public Rock createRock(Coordinates position) {
        return new Rock(position);
    }

    public Tree createTree(Coordinates position) {
        return new Tree(position);
    }

    public Grass createGrass(Coordinates position) {
        return new Grass(position);
    }

    public Herbivore createInitialHerbivore(Coordinates position) {
        return new Herbivore(
                position,
                INITIAL_HERBIVORE_HP,
                INITIAL_HERBIVORE_SATIETY,
                INITIAL_HERBIVORE_SPEED
        );
    }

    public Predator createInitialPredator(Coordinates position) {
        return new Predator(
                position,
                INITIAL_PREDATOR_HP,
                INITIAL_PREDATOR_SPEED,
                INITIAL_PREDATOR_ATTACK
        );
    }

    public Herbivore createMinimumSpawnHerbivore(Coordinates position) {
        SpawnBalanceConfig.HerbivoreSettings settings = balanceConfig.herbivores();
        return new Herbivore(position, settings.spawnHp(), settings.spawnSpeed());
    }

    public Predator createMinimumSpawnPredator(Coordinates position) {
        SpawnBalanceConfig.PredatorSettings settings = balanceConfig.predators();
        return new Predator(
                position,
                settings.spawnHp(),
                settings.spawnSpeed(),
                settings.spawnAttack()
        );
    }
}
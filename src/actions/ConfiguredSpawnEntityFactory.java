package actions;

import entity.Herbivore;
import entity.Predator;

public final class ConfiguredSpawnEntityFactory extends SpawnEntityFactory {

    private final SpawnBalanceConfig balanceConfig;

    public ConfiguredSpawnEntityFactory(SpawnBalanceConfig balanceConfig) {
        this.balanceConfig = balanceConfig;
    }

    @Override
    public Herbivore createHerbivore() {
        SpawnBalanceConfig.HerbivoreSettings settings = balanceConfig.herbivores();
        return new Herbivore(
                settings.spawnHp(),
                settings.spawnSpeed()
        );
    }

    @Override
    public Predator createPredator() {
        SpawnBalanceConfig.PredatorSettings settings = balanceConfig.predators();
        return new Predator(
                settings.spawnHp(),
                settings.spawnSpeed(),
                settings.spawnAttack()
        );
    }
}
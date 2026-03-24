package actions;

public record SpawnBalanceConfig(GrassSettings grass,
                                 HerbivoreSettings herbivores,
                                 PredatorSettings predators) {

    public static final SpawnBalanceConfig DEFAULT = new SpawnBalanceConfig(
            new GrassSettings(1, 20, 15, 10, 1),
            new HerbivoreSettings(3, 35, 25, 18, 2),
            new PredatorSettings(4, 100, 70, 6,
                    4, 40, 2, 8)
    );

    public record GrassSettings(int minimumFloor,
                                int spawnCapPerTurn,
                                int minimumByAreaDivisor,
                                int targetByAreaDivisor,
                                int targetPerHerbivoreMultiplier) {
    }

    public record HerbivoreSettings(int spawnCapPerTurn,
                                    int minimumByAreaDivisor,
                                    int targetByAreaDivisor,
                                    int spawnHp,
                                    int spawnSpeed) {
    }

    public record PredatorSettings(int spawnCapPerTurn,
                                   int minimumByAreaDivisor,
                                   int targetByAreaDivisor,
                                   int minimumByHerbivoresDivisor,
                                   int targetByHerbivoresDivisor,
                                   int spawnHp,
                                   int spawnSpeed,
                                   int spawnAttack) {
    }
}
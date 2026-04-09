package actions;

import entity.Herbivore;
import entity.Predator;
import world.WorldMap;
import world.WorldMapStatistics;

import java.util.Random;

public final class EnsureMinimumSpawnsAction implements Action {

    private static final int MAX_SPAWN_ATTEMPTS_PER_ENTITY = 3000;

    private final SpawnBalanceConfig config;
    private final SpawnEntityFactory entityFactory;

    public EnsureMinimumSpawnsAction() {
        this(SpawnBalanceConfig.DEFAULT);
    }

    public EnsureMinimumSpawnsAction(SpawnBalanceConfig config) {
        this(config, new SpawnEntityFactory(config));
    }

    EnsureMinimumSpawnsAction(SpawnBalanceConfig config,
                              SpawnEntityFactory entityFactory) {
        this.config = config;
        this.entityFactory = entityFactory;
    }

    @Override
    public void execute(WorldMap worldMap, Random random) {
        ensurePopulation(worldMap, random, buildGrassSpawnPlan(worldMap));
        ensurePopulation(worldMap, random, buildHerbivoreSpawnPlan(worldMap));
        ensurePopulation(worldMap, random, buildPredatorSpawnPlan(worldMap));
    }

    private void ensurePopulation(WorldMap worldMap, Random random, SpawnPlan spawnPlan) {
        if (spawnPlan.currentCount() >= spawnPlan.minimumCount()) {
            return;
        }

        int missingCountToReachTarget = spawnPlan.targetCount() - spawnPlan.currentCount();
        int spawnLimitThisTurn = Math.min(missingCountToReachTarget, spawnPlan.spawnCapPerTurn());

        EntitySpawner.spawnUpTo(
                worldMap,
                random,
                spawnLimitThisTurn,
                MAX_SPAWN_ATTEMPTS_PER_ENTITY,
                spawnPlan.factory()
        );
    }

    private SpawnPlan buildGrassSpawnPlan(WorldMap worldMap) {
        SpawnBalanceConfig.GrassSettings settings = config.grass();

        int currentGrassCount = WorldMapStatistics.count(worldMap, entity.Grass.class);
        int herbivoreCount = WorldMapStatistics.count(worldMap, Herbivore.class, Herbivore::isAlive);
        int mapArea = worldMap.getArea();

        int minimumGrassByArea = Math.max(settings.minimumFloor(), mapArea / settings.minimumByAreaDivisor());
        int minimumGrassCount = Math.max(minimumGrassByArea, herbivoreCount);

        int targetGrassByArea = Math.max(settings.minimumFloor(), mapArea / settings.targetByAreaDivisor());
        int targetGrassByHerbivores = herbivoreCount * settings.targetPerHerbivoreMultiplier();
        int targetGrassCount = Math.max(targetGrassByArea, targetGrassByHerbivores);
        int desiredGrassCount = Math.max(targetGrassCount, minimumGrassCount);

        return new SpawnPlan(
                currentGrassCount,
                minimumGrassCount,
                desiredGrassCount,
                settings.spawnCapPerTurn(),
                entityFactory::createGrass
        );
    }

    private SpawnPlan buildHerbivoreSpawnPlan(WorldMap worldMap) {
        SpawnBalanceConfig.HerbivoreSettings settings = config.herbivores();

        int currentHerbivoreCount = WorldMapStatistics.count(worldMap, Herbivore.class, Herbivore::isAlive);
        int mapArea = worldMap.getArea();

        int minimumHerbivoreCount = atLeastOne(mapArea / settings.minimumByAreaDivisor());
        int targetHerbivoreCount = Math.max(minimumHerbivoreCount, mapArea / settings.targetByAreaDivisor());

        return new SpawnPlan(
                currentHerbivoreCount,
                minimumHerbivoreCount,
                targetHerbivoreCount,
                settings.spawnCapPerTurn(),
                entityFactory::createMinimumSpawnHerbivore
        );
    }

    private SpawnPlan buildPredatorSpawnPlan(WorldMap worldMap) {
        SpawnBalanceConfig.PredatorSettings settings = config.predators();

        int currentPredatorCount = WorldMapStatistics.count(worldMap, Predator.class, Predator::isAlive);
        int herbivoreCount = WorldMapStatistics.count(worldMap, Herbivore.class, Herbivore::isAlive);
        int mapArea = worldMap.getArea();

        int minimumPredatorCountByArea = atLeastOne(mapArea / settings.minimumByAreaDivisor());
        int minimumPredatorCountByFoodSupply = atLeastOne(herbivoreCount / settings.minimumByHerbivoresDivisor());
        int minimumPredatorCount = Math.max(minimumPredatorCountByArea, minimumPredatorCountByFoodSupply);

        int targetPredatorCountByArea = atLeastOne(mapArea / settings.targetByAreaDivisor());
        int targetPredatorCountByFoodSupply = atLeastOne(herbivoreCount / settings.targetByHerbivoresDivisor());
        int desiredPredatorCount = Math.max(
                minimumPredatorCount,
                Math.min(targetPredatorCountByArea, targetPredatorCountByFoodSupply)
        );

        return new SpawnPlan(
                currentPredatorCount,
                minimumPredatorCount,
                desiredPredatorCount,
                settings.spawnCapPerTurn(),
                entityFactory::createMinimumSpawnPredator
        );
    }

    private int atLeastOne(int value) {
        return Math.max(1, value);
    }

    private record SpawnPlan(int currentCount,
                             int minimumCount,
                             int targetCount,
                             int spawnCapPerTurn,
                             EntitySpawner.EntityFactory factory) {
    }
}
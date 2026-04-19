import app.SimulationController;
import app.SimulationFactory;
import sim.Simulation;
import world.WorldMap;

import java.util.Scanner;

public final class DefaultMapMain {

    private static final int DEFAULT_WIDTH = 10;
    private static final int DEFAULT_HEIGHT = 10;
    private static final long DEFAULT_DELAY_MS = 1000;

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            WorldMap worldMap = new WorldMap(DEFAULT_WIDTH, DEFAULT_HEIGHT);

            SimulationFactory simulationFactory = new SimulationFactory();
            Simulation simulation = simulationFactory.create(worldMap);

            SimulationController controller = new SimulationController(
                    scanner,
                    simulation,
                    DEFAULT_DELAY_MS
            );
            controller.run();
        }
    }
}
import actions.InitPopulateAction;
import actions.MaintainPopulationAction;
import actions.MoveCreaturesAction;
import render.ConsoleRenderer;
import render.EmojiGlyphSet;
import sim.Simulation;
import world.WorldMap;

import java.util.Scanner;

public final class Main {

    public static final int NEXT_TURN = 1;
    public static final int INFINITE_SIMULATION = 2;
    public static final int EXIT = 3;
    public static final int STOP_CONTINUOUS = 4;

    private static final long DEFAULT_DELAY_MS = 1000;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int width = readPositiveInt(scanner, "Введите ширину мира: ");
        int height = readPositiveInt(scanner, "Введите высоту мира: ");

        WorldMap worldMap = new WorldMap(width, height);
        ConsoleRenderer renderer = new ConsoleRenderer("⬛", new EmojiGlyphSet());
        Simulation simulation = new Simulation.Builder(worldMap, renderer)
                .addInitAction(new InitPopulateAction())
                .addTurnAction(new MaintainPopulationAction())
                .addTurnAction(new MoveCreaturesAction())
                .build();

        boolean continuous = false;
        Thread simulationThread = null;

        printMenu(false);

        while (true) {
            System.out.print("> ");
            String command = scanner.nextLine().trim();

            if (String.valueOf(EXIT).equals(command)) {
                simulation.stopSimulation();
                System.out.println("Выход.");
                return;
            }

            if (!continuous) {
                if (String.valueOf(NEXT_TURN).equals(command)) {
                    simulation.nextTurn();
                } else if (String.valueOf(INFINITE_SIMULATION).equals(command)) {
                    continuous = true;

                    simulationThread = new Thread(() -> simulation.startSimulation(DEFAULT_DELAY_MS));
                    simulationThread.setDaemon(true);
                    simulationThread.start();

                    printMenu(true);
                } else {
                    System.out.println("Команда недоступна. Используйте "
                            + NEXT_TURN + ", "
                            + INFINITE_SIMULATION + " или "
                            + EXIT + "."
                    );
                }
            } else {
                if (String.valueOf(STOP_CONTINUOUS).equals(command)) {
                    simulation.stopSimulation();

                    simulationThread.interrupt();
                    try {
                        simulationThread.join();
                        simulationThread = null;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    continuous = false;
                    printMenu(false);
                } else {
                    System.out.println(
                            "Команда недоступна. Используйте "
                                    + STOP_CONTINUOUS + " или "
                                    + EXIT + "."
                    );
                }
            }
        }
    }

    private static void printMenu(boolean continuous) {
        if (!continuous) {
            System.out.println("Пошаговый режим:");
            System.out.println(NEXT_TURN + " - следующий ход");
            System.out.println(INFINITE_SIMULATION + " - бесконечная симуляция");
            System.out.println(EXIT + " - выход");
        } else {
            System.out.println("Непрерывный режим:");
            System.out.println(STOP_CONTINUOUS + " - остановить и вернуться в пошаговый режим");
            System.out.println(EXIT + " - выход");
        }
    }

    private static int readPositiveInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                int value = Integer.parseInt(input);
                if (value > 0) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
            }

            System.out.println("Введите положительное целое число.");
        }
    }
}
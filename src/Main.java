import actions.*;
import render.ConsoleRenderer;
import sim.Simulation;
import world.WorldMap;

import java.util.Random;
import java.util.Scanner;

public class Main {

    private static final long DEFAULT_DELAY_MS = 1000;
    private static final long SEED = 42;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int width = readPositiveInt(scanner, "Введите ширину мира: ");
        int height = readPositiveInt(scanner, "Введите высоту мира: ");

        WorldMap map = new WorldMap(width, height);

        ConsoleRenderer renderer = new ConsoleRenderer("□ ");

        Simulation simulation = new Simulation(map, renderer, new Random(SEED));

        simulation.addInitAction(new InitPopulateAction());

        simulation.addTurnAction(new EnsureMinimumSpawnsAction());
        simulation.addTurnAction(new MoveCreaturesAction(true));

        simulation.init();

        boolean continuous = false;
        Thread continuousThread = null;

        printMenu(continuous);

        while (true) {
            System.out.print("> ");
            String cmd = scanner.nextLine().trim();

            if ("0".equals(cmd)) {
                simulation.stopSimulation();
                System.out.println("Выход.");
                return;
            }

            if (!continuous) {
                if ("1".equals(cmd)) {
                    simulation.nextTurn();
                } else if ("2".equals(cmd)) {
                    continuous = true;

                    continuousThread = new Thread(() -> simulation.startSimulation(DEFAULT_DELAY_MS));
                    continuousThread.setDaemon(true);
                    continuousThread.start();

                    printMenu(true);
                } else {
                    System.out.println("Команда недоступна. Используйте 1, 2 или 0.");
                }
            } else {
                if ("3".equals(cmd)) {
                    simulation.stopSimulation();

                    if (continuousThread != null) {
                        try {
                            continuousThread.join(300);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }

                    continuous = false;
                    printMenu(false);
                } else {
                    System.out.println("Команда недоступна. Используйте 3 или 0.");
                }
            }
        }
    }

    private static void printMenu(boolean continuous) {
        if (!continuous) {
            System.out.println("""
                    Пошаговый режим:
                      1 — следующий ход
                      2 — перейти в непрерывный режим
                      0 — выход
                    """);
        } else {
            System.out.println("""
                    Непрерывный режим:
                      3 — остановить и вернуться в пошаговый режим
                      0 — выход
                    """);
        }
    }

    private static int readPositiveInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value > 0) return value;
            } catch (NumberFormatException ignored) {}
            System.out.println("Введите положительное целое число.");
        }
    }
}

package app;

import sim.Simulation;

import java.util.Scanner;

public final class SimulationController {

    private static final String NEXT_TURN = "1";
    private static final String START_CONTINUOUS = "2";
    private static final String EXIT = "3";
    private static final String STOP_CONTINUOUS = "4";

    private final Scanner scanner;
    private final Simulation simulation;
    private final long delayMs;

    private boolean continuousMode;
    private Thread simulationThread;

    public SimulationController(Scanner scanner, Simulation simulation, long delayMs) {
        this.scanner = scanner;
        this.simulation = simulation;
        this.delayMs = delayMs;
    }

    public void run() {
        printMenu();

        while (true) {
            String command = readCommand();

            if (EXIT.equals(command)) {
                stopContinuousModeIfNeeded();
                System.out.println("Выход.");
                return;
            }

            if (continuousMode) {
                handleContinuousModeCommand(command);
            } else {
                handleStepModeCommand(command);
            }
        }
    }

    private String readCommand() {
        System.out.print("> ");
        return scanner.nextLine().trim();
    }

    private void handleStepModeCommand(String command) {
        switch (command) {
            case NEXT_TURN -> simulation.nextTurn();
            case START_CONTINUOUS -> startContinuousMode();
            default -> printStepModeCommandError();
        }
    }

    private void handleContinuousModeCommand(String command) {
        if (STOP_CONTINUOUS.equals(command)) {
            stopContinuousMode();
        } else {
            printContinuousModeCommandError();
        }
    }

    private void startContinuousMode() {
        continuousMode = true;
        simulationThread = new Thread(() -> simulation.startSimulation(delayMs));
        simulationThread.setDaemon(true);
        simulationThread.start();
        printMenu();
    }

    private void stopContinuousMode() {
        stopContinuousModeIfNeeded();
        printMenu();
    }

    private void stopContinuousModeIfNeeded() {
        if (!continuousMode) {
            return;
        }

        simulation.stopSimulation();

        if (simulationThread != null) {
            simulationThread.interrupt();
            try {
                simulationThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            simulationThread = null;
        }

        continuousMode = false;
    }

    private void printMenu() {
        if (continuousMode) {
            printContinuousModeMenu();
        } else {
            printStepModeMenu();
        }
    }

    private void printStepModeMenu() {
        System.out.println("Пошаговый режим:");
        System.out.println(NEXT_TURN + " - следующий ход");
        System.out.println(START_CONTINUOUS + " - бесконечная симуляция");
        System.out.println(EXIT + " - выход");
    }

    private void printContinuousModeMenu() {
        System.out.println("Непрерывный режим:");
        System.out.println(STOP_CONTINUOUS + " - остановить и вернуться в пошаговый режим");
        System.out.println(EXIT + " - выход");
    }

    private void printStepModeCommandError() {
        System.out.println(
                "Команда недоступна. Используйте "
                        + NEXT_TURN + ", "
                        + START_CONTINUOUS + " или "
                        + EXIT + "."
        );
    }

    private void printContinuousModeCommandError() {
        System.out.println(
                "Команда недоступна. Используйте "
                        + STOP_CONTINUOUS + " или "
                        + EXIT + "."
        );
    }
}
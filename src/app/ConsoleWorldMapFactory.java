package app;

import world.WorldMap;

import java.util.Scanner;

public final class ConsoleWorldMapFactory {

    private final Scanner scanner;

    public ConsoleWorldMapFactory(Scanner scanner) {
        this.scanner = scanner;
    }

    public WorldMap create() {
        int width = readPositiveInt("Введите ширину мира: ");
        int height = readPositiveInt("Введите высоту мира: ");
        return new WorldMap(width, height);
    }

    private int readPositiveInt(String prompt) {
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
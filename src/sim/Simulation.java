package sim;

import actions.Action;
import render.Renderer;
import world.WorldMap;

import java.util.ArrayList;
import java.util.List;

public final class Simulation {

    private final WorldMap worldMap;
    private final Renderer renderer;
    private final List<Action> initActions;
    private final List<Action> turnActions;

    private int turn = 0;
    private volatile boolean running = false;

    private Simulation(Builder builder) {
        this.worldMap = builder.worldMap;
        this.renderer = builder.renderer;
        this.initActions = List.copyOf(builder.initActions);
        this.turnActions = List.copyOf(builder.turnActions);

        init();
    }

    public void nextTurn() {
        turn++;

        for (Action action : turnActions) {
            action.execute(worldMap);
        }

        printTurnHeader();
        renderer.render(worldMap);
    }

    public void startSimulation(long delayMs) {
        running = true;

        while (running) {
            nextTurn();

            if (delayMs <= 0) {
                continue;
            }

            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void stopSimulation() {
        running = false;
    }

    private void init() {
        for (Action action : initActions) {
            action.execute(worldMap);
        }

        printTurnHeader();
        renderer.render(worldMap);
    }

    public static final class Builder {
        private final WorldMap worldMap;
        private final Renderer renderer;
        private final List<Action> initActions = new ArrayList<>();
        private final List<Action> turnActions = new ArrayList<>();

        public Builder(WorldMap worldMap, Renderer renderer) {
            this.worldMap = worldMap;
            this.renderer = renderer;
        }

        public Builder addInitAction(Action action) {
            initActions.add(action);
            return this;
        }

        public Builder addTurnAction(Action action) {
            turnActions.add(action);
            return this;
        }

        public Simulation build() {
            return new Simulation(this);
        }
    }

    private void printTurnHeader() {
        System.out.println("Turn: " + turn);
    }
}
package sim;

import actions.Action;
import render.Renderer;
import world.WorldMap;

import java.util.ArrayList;
import java.util.List;

public final class Simulation {

    private final WorldMap worldMap;
    private final Renderer renderer;

    private final List<Action> initActions = new ArrayList<>();
    private final List<Action> turnActions = new ArrayList<>();

    private int turn = 0;
    private volatile boolean running = false;

    public Simulation(WorldMap worldMap, Renderer renderer) {
        this.worldMap = worldMap;
        this.renderer = renderer;
    }

    public void addInitAction(Action a) {
        initActions.add(a);
    }

    public void addTurnAction(Action a) {
        turnActions.add(a);
    }

    public void init() {
        for (Action action : initActions) {
            action.execute(worldMap);
        }
        renderer.render(worldMap, turn);
    }

    public void nextTurn() {
        turn++;

        for (Action action : turnActions) {
            action.execute(worldMap);
        }
        renderer.render(worldMap, turn);
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
}
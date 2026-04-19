package app;

import actions.InitPopulateAction;
import actions.MaintainPopulationAction;
import actions.MoveCreaturesAction;
import render.ConsoleRenderer;
import render.EmojiGlyphSet;
import sim.Simulation;
import world.WorldMap;

public final class SimulationFactory {

    private static final String EMPTY_CELL_GLYPH = "⬛";

    public Simulation create(WorldMap worldMap) {
        ConsoleRenderer renderer = new ConsoleRenderer(
                EMPTY_CELL_GLYPH,
                new EmojiGlyphSet()
        );

        return new Simulation.Builder(worldMap, renderer)
                .addInitAction(new InitPopulateAction())
                .addTurnAction(new MaintainPopulationAction())
                .addTurnAction(new MoveCreaturesAction())
                .build();
    }
}
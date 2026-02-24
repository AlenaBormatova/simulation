package render;

import entity.Entity;
import world.WorldMap;

public final class ConsoleRenderer implements Renderer {

    private final String emptyGlyph;

    public ConsoleRenderer(String  emptyGlyph) {
        this.emptyGlyph = emptyGlyph;
    }

    @Override
    public void render(WorldMap map, int turn) {
        StringBuilder output = new StringBuilder();
        output.append("Turn: ").append(turn).append("\n");

        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                Entity entityAtCell = map.get(x, y);
                output.append(entityAtCell == null ? emptyGlyph : entityAtCell.getGlyph()).append(' ');
            }
            output.append('\n');
        }

        System.out.print(output);
    }
}

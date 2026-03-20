package render;

import entity.Entity;
import world.WorldMap;

public final class ConsoleRenderer implements Renderer {

    private final String emptyGlyph;
    private final GlyphSet glyphSet;

    public ConsoleRenderer(String emptyGlyph, GlyphSet glyphSet) {
        this.emptyGlyph = emptyGlyph;
        this.glyphSet = glyphSet;
    }

    @Override
    public void render(WorldMap map, int turn) {
        StringBuilder output = new StringBuilder();

        output.append("Turn: ").append(turn).append("\n");

        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                Entity entity = map.get(x, y);
                output.append(entity == null ? emptyGlyph : glyphSet.getGlyph(entity)).append(' ');
            }
            output.append('\n');
        }

        System.out.print(output);
    }
}
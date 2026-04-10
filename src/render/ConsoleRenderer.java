package render;

import entity.Entity;
import world.WorldMap;

import java.util.Optional;

public final class ConsoleRenderer implements Renderer {

    private final String emptyGlyph;
    private final GlyphSet glyphSet;

    public ConsoleRenderer(String emptyGlyph, GlyphSet glyphSet) {
        this.emptyGlyph = emptyGlyph;
        this.glyphSet = glyphSet;
    }

    @Override
    public void render(WorldMap worldMap, int turn) {
        StringBuilder output = new StringBuilder();

        output.append("Turn: ").append(turn).append("\n");

        for (int y = 0; y < worldMap.getHeight(); y++) {
            for (int x = 0; x < worldMap.getWidth(); x++) {
                Optional<Entity> entity = worldMap.get(x, y);
                output.append(entity.map(glyphSet::getGlyph).orElse(emptyGlyph)).append(' ');
            }
            output.append('\n');
        }

        System.out.print(output);
    }
}
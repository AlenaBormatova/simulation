package render;

import entity.Entity;
import entity.Grass;
import entity.Herbivore;
import entity.Predator;
import entity.Rock;
import entity.Tree;

import java.util.Map;

public class EmojiGlyphSet implements GlyphSet {

    private static final Map<Class<? extends Entity>, String> GLYPHS = Map.of(
            Grass.class, "\uD83C\uDF3F",
            Herbivore.class, "\uD83D\uDC07",
            Predator.class, "\uD83D\uDC3A",
            Tree.class, "\uD83C\uDF33",
            Rock.class, "\uD83E\uDDF1"
    );

    @Override
    public String getGlyph(Entity entity) {
        return GLYPHS.getOrDefault(entity.getClass(), "?");
    }
}
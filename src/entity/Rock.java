package entity;

public class Rock extends Entity {
    public Rock(Coordinates position) {
        super(position);
    }

    @Override
    public String getGlyph() {
        return "\uD83E\uDDF1";
    }
}

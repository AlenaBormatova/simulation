package entity;

public final class Grass extends Entity {
    public Grass(Coordinates position) {
        super(position);
    }

    @Override
    public String getGlyph() {
        return "\uD83C\uDF3F";
    }
}
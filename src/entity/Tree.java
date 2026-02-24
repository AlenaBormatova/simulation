package entity;

public class Tree extends Entity {
    public Tree(Coordinates position) {
        super(position);
    }

    @Override
    public String getGlyph() {
        return "\uD83C\uDF33";
    }
}

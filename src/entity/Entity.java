package entity;

abstract public class Entity {
    private Coordinates position;

    protected Entity(Coordinates position) {
        this.position = position;
    }

    public Coordinates getPosition() {
        return position;
    }

    public void setPosition(Coordinates position) {
        this.position = position;
    }

    // Символ для рендера в консоли
    public abstract String getGlyph();
}
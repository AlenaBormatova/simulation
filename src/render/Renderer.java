package render;

import world.WorldMap;

public interface Renderer {
    void render(WorldMap map, int turn);
}
package actions;

import world.WorldMap;

import java.util.Random;

public interface Action {
    void execute(WorldMap worldMap, Random random);
}
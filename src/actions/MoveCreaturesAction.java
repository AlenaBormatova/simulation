package actions;

import entity.Creature;
import world.WorldMap;

import java.util.List;
import java.util.Random;

public final class MoveCreaturesAction implements Action {

    @Override
    public void execute(WorldMap map, Random random, int turn) {
        List<Creature> creatures = map.getAliveCreaturesSnapshot();
        for (Creature creature : creatures) {
            if (creature.isAlive()) {
                creature.makeMove(map, random);
            }
        }
    }
}
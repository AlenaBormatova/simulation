package actions;

import entity.Creature;
import world.WorldMap;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class MoveCreaturesAction implements Action {

    private final boolean shuffleOrder;

    public MoveCreaturesAction(boolean shuffleOrder) {
        this.shuffleOrder = shuffleOrder;
    }

    @Override
    public void execute(WorldMap map, Random random, int turn) {
        List<Creature> creatures = map.getAliveCreaturesSnapshot();
        if (shuffleOrder) {
            Collections.shuffle(creatures, random);
        }

        for (Creature creature : creatures) {
            if (creature.isAlive()) {
                creature.makeMove(map, random);
            }
        }
    }
}

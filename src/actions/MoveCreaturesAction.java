package actions;

import entity.Creature;
import world.WorldMap;
import world.WorldMapStatistics;

import java.util.List;
import java.util.Random;

public final class MoveCreaturesAction implements Action {

    @Override
    public void execute(WorldMap map, Random random) {
        List<Creature> creatures = WorldMapStatistics.getAliveCreaturesSnapshot(map);
        for (Creature creature : creatures) {
            if (creature.isAlive()) {
                creature.makeMove(map, random);
            }
        }
    }
}
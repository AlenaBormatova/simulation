package actions;

import entity.Creature;
import entity.Entity;
import world.WorldMap;

import java.util.List;
import java.util.Random;

public final class MoveCreaturesAction implements Action {

    @Override
    public void execute(WorldMap map, Random random) {
        List<WorldMap.Occupant> snapshot = map.getOccupantsSnapshot();

        for (WorldMap.Occupant occupant : snapshot) {
            Entity entity = occupant.entity();

            if (!(entity instanceof Creature creature)) {
                continue;
            }

            if (!creature.isAlive()) {
                continue;
            }

            if (map.get(occupant.position()) != creature) {
                continue;
            }

            creature.makeMove(map, occupant.position(), random);
        }
    }
}
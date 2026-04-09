package actions;

import entity.Creature;
import entity.Entity;
import world.WorldMap;

import java.util.List;
import java.util.Random;

public final class MoveCreaturesAction implements Action {

    @Override
    public void execute(WorldMap worldMap, Random random) {
        List<WorldMap.PositionedEntity> positionedEntities =
                worldMap.getPositionedEntities();

        for (WorldMap.PositionedEntity positionedEntity : positionedEntities) {
            Entity entity = positionedEntity.entity();

            if (!(entity instanceof Creature creature)) {
                continue;
            }

            if (!creature.isAlive()) {
                continue;
            }

            if (worldMap.get(positionedEntity.position()) != creature) {
                continue;
            }

            creature.makeMove(worldMap, positionedEntity.position(), random);
        }
    }
}
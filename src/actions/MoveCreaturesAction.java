package actions;

import entity.Creature;
import entity.Entity;
import world.WorldMap;

import java.util.List;
import java.util.Random;

public final class MoveCreaturesAction implements Action {

    @Override
    public void execute(WorldMap map, Random random) {
        List<WorldMap.PositionedEntity> positionedEntities =
                map.getPositionedEntitiesSnapshot();

        for (WorldMap.PositionedEntity positionedEntity : positionedEntities) {
            Entity entity = positionedEntity.entity();

            if (!(entity instanceof Creature creature)) {
                continue;
            }

            if (!creature.isAlive()) {
                continue;
            }

            if (map.get(positionedEntity.position()) != creature) {
                continue;
            }

            creature.makeMove(map, positionedEntity.position(), random);
        }
    }
}
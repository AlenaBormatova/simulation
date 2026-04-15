package actions;

import entity.Coordinates;
import entity.Creature;
import entity.Entity;
import world.WorldMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class MoveCreaturesAction implements Action {

    @Override
    public void execute(WorldMap worldMap) {
        List<WorldMap.PositionedEntity> positionedCreatures = getPositionedCreatures(worldMap);

        for (WorldMap.PositionedEntity positionedCreature : positionedCreatures) {
            Creature creature = (Creature) positionedCreature.entity();

            if (!creature.isAlive()) {
                continue;
            }

            if (!isStillAtPosition(worldMap, positionedCreature.position(), creature)) {
                continue;
            }

            creature.makeMove(worldMap);
        }
    }

    private List<WorldMap.PositionedEntity> getPositionedCreatures(WorldMap worldMap) {
        List<WorldMap.PositionedEntity> positionedCreatures = new ArrayList<>();

        for (WorldMap.PositionedEntity positionedEntity : worldMap.getPositionedEntities()) {
            if (positionedEntity.entity() instanceof Creature) {
                positionedCreatures.add(positionedEntity);
            }
        }

        return positionedCreatures;
    }

    private boolean isStillAtPosition(WorldMap worldMap, Coordinates position, Creature creature) {
        Optional<Entity> actualEntity = worldMap.get(position);
        return actualEntity.isPresent() && actualEntity.orElseThrow() == creature;
    }
}
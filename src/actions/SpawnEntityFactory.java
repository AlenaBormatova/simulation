package actions;

import entity.Grass;
import entity.Herbivore;
import entity.Predator;
import entity.Rock;
import entity.Tree;

public abstract class SpawnEntityFactory {

    public Rock createRock() {
        return new Rock();
    }

    public Tree createTree() {
        return new Tree();
    }

    public Grass createGrass() {
        return new Grass();
    }

    public abstract Herbivore createHerbivore();

    public abstract Predator createPredator();
}
package entities.map;

import entities.movement.MoveResult;
import entities.movement.Position;
import entities.population.enviroment.Food;
import entities.population.living.PreCreature;

import java.util.List;

public interface WorldContext {

    MoveResult isMovementAllowed(final Position position);

    boolean addPreCreature(final PreCreature preCreature);

    List<Food> getFoods();

}

package entities.map;

import entities.map.utils.WorldMap;
import entities.movement.MoveResult;
import entities.movement.Position;
import entities.population.enviroment.Food;
import entities.population.living.PreCreature;

import java.util.List;

public interface WorldContext {

    boolean isInBounds(final Position position);
    MoveResult isMovementAllowed(final Position position);

    boolean addPreCreature(final PreCreature preCreature);

    void removeFood(final Food food);

    List<Food> getFoods();
    WorldMap getWorldMap();

}

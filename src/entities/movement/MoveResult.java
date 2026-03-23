package entities.movement;

import entities.population.enviroment.Food;

import java.io.Serializable;

public record MoveResult(boolean allowed, Food food) implements Serializable {
}

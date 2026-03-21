package entities.population.logic.evaluators;

import entities.population.Creature;
import entities.population.logic.activity.ActionScore;

public interface ActionEvaluator {

    ActionScore evaluate(final Creature creature);

}

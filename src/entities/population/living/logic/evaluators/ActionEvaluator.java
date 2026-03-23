package entities.population.living.logic.evaluators;

import entities.population.living.Creature;
import entities.population.living.logic.activity.ActionScore;

public interface ActionEvaluator {

    ActionScore evaluate(final Creature creature);

}

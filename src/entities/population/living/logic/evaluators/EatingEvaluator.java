package entities.population.living.logic.evaluators;

import entities.population.living.Creature;
import entities.population.living.logic.activity.Action;
import entities.population.living.logic.activity.ActionScore;

public class EatingEvaluator implements ActionEvaluator{

    @Override
    public ActionScore evaluate(final Creature creature) {
        return new ActionScore(Action.EAT, 0);
    }
}

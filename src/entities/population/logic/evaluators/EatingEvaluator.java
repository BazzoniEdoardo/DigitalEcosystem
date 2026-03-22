package entities.population.logic.evaluators;

import entities.population.Creature;
import entities.population.logic.activity.Action;
import entities.population.logic.activity.ActionScore;

public class EatingEvaluator implements ActionEvaluator{

    @Override
    public ActionScore evaluate(final Creature creature) {
        return new ActionScore(Action.EAT, 0);
    }
}

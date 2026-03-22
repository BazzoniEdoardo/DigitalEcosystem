package entities.population.logic.evaluators;

import entities.population.Creature;
import entities.population.logic.activity.Action;
import entities.population.logic.activity.ActionScore;

public class MovementEvaluator implements ActionEvaluator{


    @Override
    public ActionScore evaluate(final Creature creature) {

        final double tstValue = 0.5;

        return new ActionScore(Action.MOVE, tstValue);
    }
}

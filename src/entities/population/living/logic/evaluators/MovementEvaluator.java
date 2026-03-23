package entities.population.living.logic.evaluators;

import entities.population.living.Creature;
import entities.population.living.logic.activity.Action;
import entities.population.living.logic.activity.ActionScore;

public class MovementEvaluator implements ActionEvaluator{


    @Override
    public ActionScore evaluate(final Creature creature) {

        final float energy = creature.getEnergy();
        final float maxEnergy = creature.getDna().getMetabolismGene().getGeneAttribute("maxEnergy");
        final float movementDesire = creature.getDna().getMovementGene().getGeneAttribute("movementDesire");

        final float energyRatio = (maxEnergy > 0) ? energy / maxEnergy : 0;

        final float score = (float) Math.pow(energyRatio, 0.5) *  movementDesire;

        return new ActionScore(Action.MOVE, score);
    }
}

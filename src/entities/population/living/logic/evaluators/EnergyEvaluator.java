package entities.population.living.logic.evaluators;

import entities.population.living.Creature;
import entities.population.living.logic.activity.Action;
import entities.population.living.logic.activity.ActionScore;

public class EnergyEvaluator implements ActionEvaluator {

    @Override
    public ActionScore evaluate(final Creature creature) {

        final float energy = creature.getEnergy();
        final float maxEnergy = creature.getDna().getMetabolismGene().getGeneAttribute("maxEnergy");
        final float energyConservation = creature.getDna().getMetabolismGene().getGeneAttribute("energyConservation");

        final float energyRatio = (maxEnergy > 0) ? energy / maxEnergy : 0;

        final float threshold = 0.05f; // 5%
        float score = 0f;

        if (energyRatio <= threshold) {
            score = 1.0f * energyConservation;
        } else if (energyRatio <= 2 * threshold) {
            score = (2 * threshold - energyRatio) / threshold * energyConservation;
        }

        return new ActionScore(Action.IDLE, score);
    }
}

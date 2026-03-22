package entities.population.logic.evaluators;

import configuration.RandomConfig;
import entities.population.Creature;
import entities.population.genetics.genes.ReproductionGene;
import entities.population.logic.activity.Action;
import entities.population.logic.activity.ActionScore;

public class ReproductionEvaluator implements ActionEvaluator {


    @Override
    public ActionScore evaluate(final Creature creature) {
        final ReproductionGene reproductionGene = creature.getDna().getReproductionGene();

        float score;

        final float threshold = reproductionGene.getGeneAttribute("reproductionThreshold");
        final float cooldown = reproductionGene.getGeneAttribute("reproductionCooldown");
        final float desire = reproductionGene.getGeneAttribute("reproductionDesire");
        final float energyFactor = Math.min(creature.getEnergy() / threshold, 1.0f);
        final float cooldownFactor = 1.0f - Math.min(Math.clamp(cooldown, 0, reproductionGene.getGeneAttribute("reproductionRate")) / reproductionGene.getGeneAttribute("reproductionRate"), 1.0f);

        //Primo calcolo dello score
        score = energyFactor * cooldownFactor * desire;

        //Aggiungiamo una leggera randomicita' (da togliere quando si fa una versione completa di questo)
        score *= 0.8f + 0.2f * RandomConfig.random.nextFloat();

        return new ActionScore(Action.REPRODUCE, score);
    }
}

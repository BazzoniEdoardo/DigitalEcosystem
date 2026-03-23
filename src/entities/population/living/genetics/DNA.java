package entities.population.living.genetics;

import configuration.RandomConfig;
import core.App;
import entities.population.living.genetics.genes.*;
import settings.categories.genes.*;

public class DNA {

    protected BehaviourGene behaviourGene;
    protected MetabolismGene metabolismGene;
    protected MovementGene movementGene;
    protected PerceptionGene perceptionGene;
    protected ReproductionGene reproductionGene;

    public DNA() {
        final BehaviourGeneSettings behaviourGeneSettings = App.getSimManager().getSettings().getGeneSettings().behaviourGeneSettings();
        final MetabolismGeneSettings metabolismGeneSettings = App.getSimManager().getSettings().getGeneSettings().metabolismGeneSettings();
        final MovementGeneSettings movementGeneSettings = App.getSimManager().getSettings().getGeneSettings().movementGeneSettings();
        final PerceptionGeneSettings perceptionGeneSettings = App.getSimManager().getSettings().getGeneSettings().perceptionGeneSettings();
        final ReproductionGeneSettings reproductionGeneSettings = App.getSimManager().getSettings().getGeneSettings().reproductionGeneSettings();

        this.behaviourGene = new BehaviourGene(
                behaviourGeneSettings.aggressiveness(),
                behaviourGeneSettings.fear(),
                behaviourGeneSettings.curiosity(),
                behaviourGeneSettings.randomness(),
                behaviourGeneSettings.persistence());

        this.metabolismGene = new MetabolismGene(
                metabolismGeneSettings.baseHungerConsumption(),
                metabolismGeneSettings.digestionMultiplier(),
                metabolismGeneSettings.maxEnergy(),
                metabolismGeneSettings.maxHunger(),
                metabolismGeneSettings.energyConservation());

        this.movementGene = new MovementGene(
                movementGeneSettings.baseSpeed(),
                movementGeneSettings.baseAgility(),
                movementGeneSettings.energyEfficiency(),
                movementGeneSettings.movementDesire());

        this.perceptionGene = new PerceptionGene(
                perceptionGeneSettings.visionRange(),
                perceptionGeneSettings.visionAngle(),
                perceptionGeneSettings.foodPriority());

        this.reproductionGene = new ReproductionGene(
                reproductionGeneSettings.reproductionThreshold(),
                reproductionGeneSettings.reproductionRate(),
                reproductionGeneSettings.childrenMultiplier(),
                reproductionGeneSettings.mutationRate(),
                reproductionGeneSettings.mutationDeviation(),
                reproductionGeneSettings.reproductionDesire());
    }

    protected DNA(final DNA dna, final boolean mutated) {
        if (mutated) {
            final float deviation = dna.getReproductionGene().getGeneAttribute("mutationDeviation");

            this.behaviourGene = dna.getBehaviourGene().mutate(deviation);
            this.metabolismGene = dna.getMetabolismGene().mutate(deviation);
            this.movementGene = dna.movementGene.mutate(deviation);
            this.perceptionGene = dna.perceptionGene.mutate(deviation);
            this.reproductionGene = dna.reproductionGene.mutate(deviation);
        }else {
            this.behaviourGene = dna.getBehaviourGene().clone();
            this.metabolismGene = dna.getMetabolismGene().clone();
            this.movementGene = dna.movementGene.clone();
            this.perceptionGene = dna.perceptionGene.clone();
            this.reproductionGene = dna.reproductionGene.clone();
        }
    }

    public DNA reproduce() {
        final float mutationRate = RandomConfig.random.nextFloat();
        final boolean mutated = mutationRate <= this.reproductionGene.getGeneAttribute("mutationRate");

        return new DNA(this, mutated);
    }

    public void update() {
        reproductionGene.update();
    }

    public BehaviourGene getBehaviourGene() {
        return behaviourGene;
    }

    public MetabolismGene getMetabolismGene() {
        return metabolismGene;
    }

    public MovementGene getMovementGene() {
        return movementGene;
    }

    public PerceptionGene getPerceptionGene() {
        return perceptionGene;
    }

    public ReproductionGene getReproductionGene() {
        return reproductionGene;
    }

    @Override
    public String toString() {
        return "DNA{" +
                "behaviourGene=" + behaviourGene +
                ", metabolismGene=" + metabolismGene +
                ", movementGene=" + movementGene +
                ", perceptionGene=" + perceptionGene +
                ", reproductionGene=" + reproductionGene +
                '}';
    }
}

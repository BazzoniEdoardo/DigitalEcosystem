package entities.population.genetics;

import configuration.RandomConfig;
import configuration.Settings;
import core.App;
import entities.population.genetics.genes.*;

public class DNA {

    protected BehaviourGene behaviourGene;
    protected MetabolismGene metabolismGene;
    protected MovementGene movementGene;
    protected PerceptionGene perceptionGene;
    protected ReproductionGene reproductionGene;

    public DNA() {
        final Settings baseSettings = App.getSimManager().getSettings();

        this.behaviourGene = new BehaviourGene(baseSettings.getAggressiveness(), baseSettings.getFear(), baseSettings.getCuriosity(), baseSettings.getRandomness(), baseSettings.getPersistence());
        this.metabolismGene = new MetabolismGene(baseSettings.getBaseHungerConsumption(), baseSettings.getDigestionMultiplier(), baseSettings.getMaxEnergy(), baseSettings.getMaxHunger());
        this.movementGene = new MovementGene(baseSettings.getBaseSpeed(), baseSettings.getBaseAgility(), baseSettings.getEnergyEfficency());
        this.perceptionGene = new PerceptionGene(baseSettings.getVisionRange(), baseSettings.getVisionAngle(), baseSettings.getFoodPriority());
        this.reproductionGene = new ReproductionGene(baseSettings.getReproductionThreshold(), baseSettings.getReproductionRate(), baseSettings.getChildrenMultiplier(), baseSettings.getMutationRate(), baseSettings.getMutationDeviation());
    }

    protected DNA(final DNA dna, final boolean mutated) {
        if (mutated) {
            final float deviation = dna.getReproductionGene().getGeneAttribute("mutationDeviation");

            this.behaviourGene = dna.getBehaviourGene().mutate(deviation);
            this.metabolismGene = dna.getMetabolismGene().mutate(deviation);
            this.movementGene = dna.movementGene.mutate(deviation);
            this.perceptionGene = dna.perceptionGene.mutate(deviation);
            this.reproductionGene = dna.reproductionGene.mutate(deviation);

            System.out.println("A DNA has mutated!");
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

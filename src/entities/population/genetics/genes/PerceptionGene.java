package entities.population.genetics.genes;

public class PerceptionGene extends AbstractGene<PerceptionGene> {

    public PerceptionGene() {
        setGeneAttribute("visionRange", 0f);    //Visione per il pathfinding, numero di caselle di vista
        setGeneAttribute("visionAngle", 0f);    //Angolo di visione
    }

    public PerceptionGene(final float visionRange, final float visionAngle, final float foodPriority) {
        setGeneAttribute("visionRange", visionRange);   //Visione per il pathfinding, numero di caselle di vista
        setGeneAttribute("visionAngle", visionAngle);   //Angolo di visione
    }

    public PerceptionGene(final PerceptionGene perceptionGene) {
        super(perceptionGene);
    }

    @Override
    protected PerceptionGene self() {
        return this;
    }

    @Override
    public PerceptionGene clone() {
        return new PerceptionGene(this);
    }

    @Override
    public void update() {

    }

}

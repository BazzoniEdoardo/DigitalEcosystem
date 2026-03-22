package entities.population.genetics.genes;

//FATTA
public class ReproductionGene extends AbstractGene<ReproductionGene> {

    //TODO: Aggiungere il costo della riproduzione
    public ReproductionGene() {
        setGeneAttribute("reproductionThreshold", 0f);      //Quantita di energia minima per riprodursi         //FATTO
        setGeneAttribute("reproductionRate", 0f);           //Ogni quanto puo' riprodursi                       //FATTO
        setGeneAttribute("childrenMultiplier", 0f);         //Probabilita' di fare piu' figli                   //FATTO
        setGeneAttribute("mutationRate", 0f);               //Probabilita' di mutazione del dna                 //FATTO -> da implementare per le PreCreature // FATTO
        setGeneAttribute("mutationDeviation", 0f);          //Max Range per la deviazione di mutazione del DNA  //FATTO
        setGeneAttribute("reproductionDesire", 0f);         //Desiderio di riprodursi

        setGeneAttribute("reproductionCooldown", 0f);       //Cooldown per la riproduzione
    }

    public ReproductionGene(final float reproductionThreshold, final float reproductionRate, final float childrenMultiplier, final float mutationRate, final float mutationDeviation, final float reproductionDesire) {
        setGeneAttribute("reproductionThreshold", reproductionThreshold);   //Quantita di energia minima per riprodursi
        setGeneAttribute("reproductionRate", reproductionRate);             //Ogni quanto puo' riprodursi
        setGeneAttribute("childrenMultiplier", childrenMultiplier);         //Probabilita' di fare piu' figli
        setGeneAttribute("mutationRate", mutationRate);                     //Probabilita' di mutazione del dna
        setGeneAttribute("mutationDeviation", mutationDeviation);           //Max Range per la deviazione di mutazione del DNA
        setGeneAttribute("reproductionDesire", reproductionDesire);         //Desiderio di riprodursi

        setGeneAttribute("reproductionCooldown", 0f);               //Cooldown per la riproduzione
    }

    public ReproductionGene(final ReproductionGene reproductionGene) {
        super(reproductionGene);
    }

    protected void updateReproductionCooldown() {
        final float reproductionCooldown = getGeneAttribute("reproductionCooldown");
        if (reproductionCooldown <= 0) return;

        setGeneAttribute("reproductionCooldown", reproductionCooldown-1);
    }

    @Override
    public void update() {
        updateReproductionCooldown();
    }

    public void restartReproductionCooldown() {
        setGeneAttribute("reproductionCooldown", getGeneAttribute("reproductionRate"));
    }

    @Override
    protected ReproductionGene self() {
        return this;
    }

    @Override
    public ReproductionGene clone() {
        return new ReproductionGene(this);
    }

    @Override
    public String toString() {
        return "ReproductionGene{" +
                "geneAttributes=" + geneAttributes +
                '}';
    }
}

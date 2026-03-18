package entities.population.genetics.genes;

public class ReproductionGene extends AbstractGene<ReproductionGene> {

    public ReproductionGene() {
        setGeneAttribute("reproductionThreshold", 0f);      //Quantita di energia minima per riprodursi
        setGeneAttribute("reproductionRate", 0f);           //Ogni quanto puo' riprodursi
        setGeneAttribute("childrenMultiplier", 0f);         //Probabilita' di fare piu' figli
        setGeneAttribute("mutationRate", 0f);               //Probabilita' di mutazione del dna
        setGeneAttribute("mutationDeviation", 0f);          //Max Range per la deviazione di mutazione del DNA
    }

    public ReproductionGene(final float reproductionThreshold, final float reproductionRate, final float childrenMultiplier, final float mutationRate, final float mutationDeviation) {
        setGeneAttribute("reproductionThreshold", reproductionThreshold);   //Quantita di energia minima per riprodursi
        setGeneAttribute("reproductionRate", reproductionRate);             //Ogni quanto puo' riprodursi
        setGeneAttribute("childrenMultiplier", childrenMultiplier);         //Probabilita' di fare piu' figli
        setGeneAttribute("mutationRate", mutationRate);                     //Probabilita' di mutazione del dna
        setGeneAttribute("mutationDeviation", mutationDeviation);           //Max Range per la deviazione di mutazione del DNA
    }

    public ReproductionGene(final ReproductionGene reproductionGene) {
        super(reproductionGene);
    }

    @Override
    protected ReproductionGene self() {
        return this;
    }

    @Override
    public ReproductionGene clone() {
        return new ReproductionGene(this);
    }
}

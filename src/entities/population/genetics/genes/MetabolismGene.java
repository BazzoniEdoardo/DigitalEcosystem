package entities.population.genetics.genes;

public class MetabolismGene extends AbstractGene<MetabolismGene>{

    public MetabolismGene() {
        setGeneAttribute("baseHungerConsumption", 0f);  //Quanto consuma di base di hunger nel tempo
        setGeneAttribute("digestionMultiplier", 0f);    //Moltiplicatore di quanto nutrimento ottiene dal cibo
        setGeneAttribute("maxEnergy", 0f);
        setGeneAttribute("maxHunger", 0f);
    }

    public MetabolismGene(final float baseHungerConsumption, final float digestionMultiplier, final float maxEnergy, final float maxHungry) {
        setGeneAttribute("baseHungerConsumption", baseHungerConsumption);   //Quanto consuma di base di hunger nel tempo
        setGeneAttribute("digestionMultiplier", digestionMultiplier);       //Moltiplicatore di quanto nutrimento ottiene dal cibo
        setGeneAttribute("maxEnergy", maxEnergy);
        setGeneAttribute("maxHunger", maxHungry);
    }

    public MetabolismGene(final MetabolismGene metabolismGene) {
        super(metabolismGene);
    }

    @Override
    protected MetabolismGene self() {
        return this;
    }

    @Override
    public MetabolismGene clone() {
        return new MetabolismGene(this);
    }
}

package entities.population.genetics.genes;

public class BehaviourGene extends AbstractGene<BehaviourGene> {
    //TODO: implementare un sistema di trasmissione di comportamenti specifici appresi da altre creature per sopravvivere (molto avanzato)
    /*
    Per esempio se una creatura con certi parametri trova che un certo comportamento la fa sopravvivere viene tramandato in automatico ai figli. Bisognera' pero' implementare
    la creazione di questi comportamenti, che a loro volta non sono standard e neanche algoritmi. Devono essere creati dalle creature stesse.
     */

    public BehaviourGene() {
        super();

        setGeneAttribute("aggressiveness", 0f);     //Aggressivita'
        setGeneAttribute("fear", 0f);               //Paura
        setGeneAttribute("curiosity", 0f);          //Curiosita'
        setGeneAttribute("randomness", 0f);         //Randomicita'
        setGeneAttribute("persistence", 0f);        //Persistenza
    }

    public BehaviourGene(final float aggressiveness, final float fear, final float curiosity, final float randomness, final float persistence) {
        super();

        setGeneAttribute("aggressiveness", aggressiveness);     //Aggressivita'
        setGeneAttribute("fear", fear);                         //Paura
        setGeneAttribute("curiosity", curiosity);               //Curiosita'
        setGeneAttribute("randomness", randomness);             //Randomicita'
        setGeneAttribute("persistence", persistence);           //Persistenza
    }

    public BehaviourGene(final BehaviourGene behaviourGene) {
        super(behaviourGene);
    }

    @Override
    protected BehaviourGene self() {
        return this;
    }

    @Override
    public BehaviourGene clone() {
        return new BehaviourGene(this);
    }

}

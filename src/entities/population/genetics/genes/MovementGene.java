package entities.population.genetics.genes;

public class MovementGene extends AbstractGene<MovementGene>{

    public MovementGene() {
        setGeneAttribute("speed", 0f);                //Di quante caselle si puo' muovere al secondo
        setGeneAttribute("agility", 0f);              //Rispetto al movimento precedente, puo' muoversi solo nelle n caselle a partire da quella direzione. Se va a destra e agility=3, lui puo' muoversi solo a destra, o in diagonale altodestra o bassodestra
        setGeneAttribute("energyEfficiency", 0f);     //Riduzione nel costo di movimento base           //FATTO
    }

    public MovementGene(final float speed, final float agility, final float energyEfficiency) {
        setGeneAttribute("speed", speed);                           //Di quante caselle si puo' muovere al secondo
        setGeneAttribute("agility", agility);                       //Rispetto al movimento precedente, puo' muoversi solo nelle agility caselle a partire da quella direzione. Se va a destra e agility=3, lui puo' muoversi solo a destra, o in diagonale altodestra o bassodestra
        setGeneAttribute("energyEfficiency", energyEfficiency);     //Riduzione nel costo di movimento base
    }

    public MovementGene(final MovementGene movementGene) {
        super(movementGene);
    }

    @Override
    protected MovementGene self() {
        return this;
    }

    @Override
    public MovementGene clone() {
        return new MovementGene(this);
    }

    @Override
    public void update() {

    }
}

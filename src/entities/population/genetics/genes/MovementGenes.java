package entities.population.genetics.genes;

public class MovementGenes {

    protected int speed; // di quante caselle si puo' muovere al secondo
    protected int agility; //Rispetto al movimento precedente, puo' muoversi solo nelle agility caselle a partire da quella direzione. Se va a destra e agility=3, lui puo' muoversi solo a destra, o in diagonale altodestra o bassodestra
    protected float energyEfficiency; //Riduzione nel costo di movimento base
}

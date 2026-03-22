package builders;

import entities.map.World;
import entities.population.Creature;
import entities.movement.Position;
import entities.population.genetics.DNA;

public class CreatureBuilder {
    private Position position;
    private float energy;
    private float hunger;
    private DNA dna;
    private World world;

    public CreatureBuilder() {}

    public CreatureBuilder setPosition(final Position position) {
        this.position = position;
        return this;
    }

    public CreatureBuilder setEnergy(final float energy) {
        this.energy = energy;
        return this;
    }

    public CreatureBuilder setHunger(final float hunger) {
        this.hunger = hunger;
        return this;
    }

    public CreatureBuilder setDna(final DNA dna) {
        this.dna = dna;
        return this;
    }

    public CreatureBuilder setWorld(final World world) {
        this.world = world;
        return this;
    }

    public Position getPosition() {
        return position;
    }

    public float getEnergy() {
        return energy;
    }

    public float getHunger() {
        return hunger;
    }

    public DNA getDna() { return dna; }

    public World getWorld() {
        return world;
    }

    public Creature build() throws IllegalArgumentException {
        if (this.position == null) throw new IllegalArgumentException("Position cannot be null");
        if (this.hunger < 0) throw new IllegalArgumentException("Hunger cannot be lower than 0");
        if (this.energy < 0) throw new IllegalArgumentException("Energy cannot be lower than 0");
        if (this.dna == null) throw new IllegalArgumentException("DNA cannot be null");
        if (this.world == null) throw new IllegalArgumentException("World cannot be null");

        return new Creature(this.position, this.energy, this.hunger, this.dna, this.world);
    }
}

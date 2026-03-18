package entities.population;

import configuration.RandomConfig;
import core.App;
import entities.SimulationEntity;
import entities.map.World;
import entities.movement.Position;
import entities.population.genetics.DNA;
import managers.EntityManager;
import render.entities.AbstractRenderedEntity;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Creature extends AbstractRenderedEntity implements SimulationEntity, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    //Le statistiche saranno delegate ad una classe DNA
    protected final DNA dna;

    protected final int id;
    protected Position position;
    protected float energy;
    protected float hunger;
    protected boolean alive;
    protected boolean moving;

    public Creature(final Position position, final float energy, final float hunger) {
        this.id = EntityManager.nextId();
        this.dna = new DNA();
        setPosition(position);
        setEnergy(energy);
        setHunger(hunger);
        setAlive(true);
    }

    public Creature(final Position position, final float energy, final float hunger, final DNA dna) {
        this.id = EntityManager.nextId();
        this.dna = dna.reproduce();
        setPosition(position);
        setEnergy(energy);
        setHunger(hunger);
        setAlive(true);
    }

    public Creature() {
        this(null, App.getSimManager().getSettings().getBaseEnergy(), App.getSimManager().getSettings().getBaseHunger());
    }

    public Creature(final Creature creature) {
        this(creature.getPosition(), creature.getEnergy(), creature.getHunger(), creature.getDna());
    }

    @Override
    public int getId() {
        return id;
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

    public boolean isAlive() { return alive; }

    public boolean isMoving() { return moving; }

    public DNA getDna() {
        return dna;
    }

    private void setPosition(final Position position) {
        this.position = (position != null) ? position : new Position(0, 0);
    }

    private void setEnergy(final float energy) {
        this.energy = (this.energy >= 0) ? energy : App.getSimManager().getSettings().getBaseEnergy();
    }

    private void setHunger(final float hunger) {
        this.hunger = (this.hunger >= 0) ? hunger : App.getSimManager().getSettings().getBaseHunger();
    }

    private void setAlive(final boolean alive) { this.alive = alive; }

    private void setMoving(final boolean moving) { this.moving = moving; }

    public void eat(final Food food) {
        this.energy += food.getNutrition();
        //StatsManager.printFoodAlert(this, food);
    }

    public SimulationEntity clone() {
        return new Creature(this);
    }

    //MAIN METHODS

    public Position getNextPosition() {
        return new Position(RandomConfig.random.nextInt(position.x()-1, position.x()+2), RandomConfig.random.nextInt(position.y()-1, position.y()+2));
    }

    public boolean move(final Position position) {
        if (Objects.equals(position, this.position)) return false;

        setPosition(position);
        setMoving(true);
        return true;
    }

    private boolean canReproduce() {
        return this.energy >= App.getSimManager().getSettings().getReproductionThreshold();
    }

    @Override
    public void update() {
        if (!isAlive()) return;

        //Movimento
        energy -= (moving) ? App.getSimManager().getSettings().getEnergyLossPerMove() : App.getSimManager().getSettings().getEnergyLossPerTick();
        setMoving(false);

        //Riproduzione
        if (canReproduce()) {
            World.preCreatures.add(new PreCreature((int) App.getSimManager().getSettings().getPregnancyTicks(), this));
            setEnergy(this.energy-App.getSimManager().getSettings().getReproductionCost());
        }

        //Morte
        if (energy <= 0) {
            setAlive(false);
        }

    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Creature creature)) return false;
        return getId() == creature.getId() && getEnergy() == creature.getEnergy() && getHunger() == creature.getHunger() && Objects.equals(getPosition(), creature.getPosition());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getPosition(), getEnergy(), getHunger());
    }

    @Override
    public String toString() {
        return "Creature{" +
                "id=" + id +
                ", position=" + position +
                ", energy=" + energy +
                ", hunger=" + hunger +
                '}';
    }

    @Override
    public SimulationEntity getEntity() {
        return this;
    }

    @Override
    public Map<String, String> getInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("ID",        String.valueOf(id));
        info.put("Position",  position.x() + ", " + position.y());
        info.put("Energy",    String.format("%.2f", energy));
        info.put("Hunger",    String.format("%.2f", hunger));
        info.put("Alive",     String.valueOf(alive));
        info.put("Moving",    String.valueOf(moving));
        return info;
    }

    @Override
    public String getEntityTypeName() {
        return "Creature";
    }
}

package entities.population;

import configuration.RandomConfig;
import core.App;
import entities.SimulationEntity;
import entities.map.World;
import entities.movement.MoveResult;
import entities.movement.Position;
import entities.population.genetics.DNA;
import entities.population.logic.Brain;
import entities.population.logic.activity.Action;
import managers.EntityManager;
import render.entities.AbstractRenderedEntity;
import settings.categories.CreatureSettings;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

//TODO: SISTEMARE IL MOVIMENTO CHE ATTUALMENTE HA UN DESIGN ORRIBILE (AGGIUNGERE RIFERIMENTO A WORLD DENTRO CREATURE MOLTO SEMPLICEMENTE)
public class Creature extends AbstractRenderedEntity implements SimulationEntity, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    //Le statistiche saranno delegate ad una classe DNA
    protected final DNA dna;
    protected final Brain brain;

    protected final int id;
    protected Position position;
    protected float energy;
    protected float hunger;
    protected boolean alive;
    protected boolean moving;

    protected final World world;

    public Creature(final Position position, final float energy, final float hunger, final World world) {
        this.id = EntityManager.nextId();
        this.dna = new DNA();
        this.brain = new Brain(this);
        this.world = world;
        setPosition(position);
        setEnergy(energy);
        setHunger(hunger);
        setAlive(true);
    }

    public Creature(final Position position, final float energy, final float hunger, final DNA dna, final World world) {
        this.id = EntityManager.nextId();
        this.dna = dna.reproduce();
        this.brain = new Brain(this);
        this.world = world;
        setPosition(position);
        setEnergy(energy);
        setHunger(hunger);
        setAlive(true);
    }

    public Creature(final Creature creature) {
        this(creature.getPosition(), creature.getEnergy(), creature.getHunger(), creature.getDna(), creature.getWorld());
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

    public World getWorld() { return world; }

    public Brain getBrain() { return brain; }

    private void setPosition(final Position position) {
        this.position = (position != null) ? position : new Position(0, 0);
    }

    private void setEnergy(final float energy) {
        final CreatureSettings settings = App.getSimManager().getSettings().getCreatureSettings();

        this.energy = (energy >= 0) ? energy : settings.baseEnergy();
    }

    private void setHunger(final float hunger) {
        final CreatureSettings settings = App.getSimManager().getSettings().getCreatureSettings();

        this.hunger = (hunger >= 0) ? hunger : settings.baseHunger();
    }

    private void setAlive(final boolean alive) { this.alive = alive; }

    private void setMoving(final boolean moving) { this.moving = moving; }

    public void eat(final Food food) {
        this.energy = Math.clamp(energy + food.getNutrition() * dna.getMetabolismGene().getGeneAttribute("digestionMultiplier"), 0, dna.getMetabolismGene().getGeneAttribute("maxEnergy"));
        this.energy = Math.clamp(hunger + food.getNutrition() * dna.getMetabolismGene().getGeneAttribute("digestionMultiplier"), 0, dna.getMetabolismGene().getGeneAttribute("maxHunger"));
        //StatsManager.printFoodAlert(this, food);
    }

    public Creature clone() {
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

    public boolean moveRandom() {
        Position newPos = getNextPosition();
        MoveResult result = world.isMovementAllowed(newPos);

        if (!result.allowed()) return false;

        boolean r = this.move(newPos);

        if (!r) return false;

        if (result.food() != null) {
            this.eat(result.food());
            this.world.getFoods().remove(result.food());
        }

        return true;
    }

    public void idle() {
        setMoving(false);
    }

    private boolean canReproduce() {
        return this.energy >= dna.getReproductionGene().getGeneAttribute("reproductionThreshold");
    }

    @Override
    public void update() {
        if (!isAlive()) return;

        final CreatureSettings settings = App.getSimManager().getSettings().getCreatureSettings();

        dna.update();

        //Aggiornamenti necessari
        hunger = Math.clamp(hunger-dna.getMetabolismGene().getGeneAttribute("baseHungerConsumption"), 0, dna.getMetabolismGene().getGeneAttribute("maxHunger"));

        updateAction();

        //Movimento, utilizzo del DNA per gestire, qui dovrei separare la logica e creare delle funzioni generali, tipo updateMovement ecc..., ma per ora va bene cosi
        energy -= ((moving) ? settings.energyLossPerMovement() : settings.energyLossPerTick()) / dna.getMovementGene().getGeneAttribute("energyEfficiency");

        //Morte
        if (energy <= 0) {
            setAlive(false);
        }

    }

    protected void updateAction() {
        final Action action = brain.decide();

        switch (action) {
            case REPRODUCE -> this.reproduce();
            case IDLE -> setMoving(false);
            case MOVE -> moveRandom();
        }
    }

    protected void reproduce() {
        final CreatureSettings settings = App.getSimManager().getSettings().getCreatureSettings();

        setMoving(false);

        //Restart del cooldown per la riproduzione
        dna.getReproductionGene().restartReproductionCooldown();

        //Scelta di quanti figli ottiene
        final int childrenNumber = (int) (RandomConfig.random.nextFloat(1, 2) * dna.getReproductionGene().getGeneAttribute("childrenMultiplier"));

        for (int i = 0; i < childrenNumber; i++) {
            world.addPreCreature(new PreCreature((int) settings.pregnancyTicks(), this));
            setEnergy(this.energy-settings.reproductionCost());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Creature creature)) return false;
        return this.id == creature.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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
        final Map<String, String> info = new LinkedHashMap<>();
        info.put("ID",       String.valueOf(id));
        info.put("Position", String.format("%.0f, %.0f", (double) position.x(), (double) position.y()));
        info.put("Energy",   String.format("%.2f", energy));
        info.put("Hunger",   String.format("%.2f", hunger));
        info.put("Alive",    String.valueOf(alive));
        info.put("Moving",   String.valueOf(moving));
        return info;
    }

    @Override
    public String getEntityTypeName() {
        return "Creature";
    }
}

package entities.population;

import core.App;
import entities.SimulationEntity;

import java.io.Serializable;

public class PreCreature implements SimulationEntity, Serializable {
    private static final long serialVersionUID = 1L;

    protected int daysTillBorn;
    protected final Creature parent;

    public PreCreature(final int daysTillBorn, final Creature parent) {
        this.daysTillBorn = daysTillBorn;
        this.parent = parent;
    }

    public PreCreature() {
        this((int) App.getSimManager().getSettings().getPregnancyTicks(), null);
    }

    public PreCreature(final PreCreature preCreature) {
        this(preCreature.getDaysTillBorn(), preCreature.getParent());
    }

    public int getDaysTillBorn() {
        return daysTillBorn;
    }

    public Creature getParent() {
        return this.parent;
    }

    public void setDaysTillBorn(final int daysTillBorn) {
        this.daysTillBorn = daysTillBorn;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public void update() {
        setDaysTillBorn(daysTillBorn-1);
    }

    @Override
    public SimulationEntity clone() {
        return new PreCreature(this);
    }
}

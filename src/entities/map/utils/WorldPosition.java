package entities.map.utils;

import entities.population.SimulationEntity;
import entities.movement.Position;

import java.io.Serializable;

public class WorldPosition implements Serializable {

    protected final Position position;
    protected SimulationEntity entity;

    public WorldPosition(final Position position) {
        this.position = position;
        this.entity = null;
    }

    public void setEntity(final SimulationEntity entity) {
        this.entity = entity;
    }

    public SimulationEntity getEntity() {
        return entity;
    }

    public void clear() {
        setEntity(null);
    }
}

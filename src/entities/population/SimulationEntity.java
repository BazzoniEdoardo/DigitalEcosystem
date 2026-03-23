package entities.population;

import entities.movement.Position;

public interface SimulationEntity {

    int getId();

    Position getPosition();

    SimulationEntity clone();
}

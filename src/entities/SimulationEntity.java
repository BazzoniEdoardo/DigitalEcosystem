package entities;

public interface SimulationEntity {

    int getId();

    void update();

    SimulationEntity clone();
}

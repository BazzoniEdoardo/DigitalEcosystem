package settings;

import settings.categories.*;

import java.io.Serial;
import java.io.Serializable;

public class Settings implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    //Settings Generali
    private volatile SimulationSettings simulationSettings;
    private volatile WorldSettings worldSettings;
    private volatile CreatureSettings creatureSettings;
    private volatile FoodSettings foodSettings;
    private volatile GeneSettings geneSettings;

    public Settings() {

        simulationSettings = new SimulationSettings();
        worldSettings = new WorldSettings();
        creatureSettings = new CreatureSettings();
        foodSettings = new FoodSettings();
        geneSettings = new GeneSettings();

    }

    //Getters
    public synchronized SimulationSettings getSimulationSettings() {
        return simulationSettings;
    }

    public synchronized WorldSettings getWorldSettings() {
        return worldSettings;
    }

    public synchronized CreatureSettings getCreatureSettings() {
        return creatureSettings;
    }

    public synchronized FoodSettings getFoodSettings() {
        return foodSettings;
    }

    public synchronized GeneSettings getGeneSettings() {
        return geneSettings;
    }

    //Setters

    public synchronized void setGeneSettings(GeneSettings geneSettings) {
        this.geneSettings = geneSettings;
    }

    public synchronized void setFoodSettings(FoodSettings foodSettings) {
        this.foodSettings = foodSettings;
    }

    public synchronized void setCreatureSettings(CreatureSettings creatureSettings) {
        this.creatureSettings = creatureSettings;
    }

    public synchronized void setWorldSettings(WorldSettings worldSettings) {
        this.worldSettings = worldSettings;
    }

    public synchronized void setSimulationSettings(SimulationSettings simulationSettings) {
        this.simulationSettings = simulationSettings;
    }
}

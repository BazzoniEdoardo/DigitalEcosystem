package settings;

import settings.categories.*;

import java.io.Serial;
import java.io.Serializable;

public class Settings implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    //Settings Generali
    private SimulationSettings simulationSettings;
    private WorldSettings worldSettings;
    private CreatureSettings creatureSettings;
    private FoodSettings foodSettings;
    private GeneSettings geneSettings;

    public Settings() {

        simulationSettings = new SimulationSettings();
        worldSettings = new WorldSettings();
        creatureSettings = new CreatureSettings();
        foodSettings = new FoodSettings();
        geneSettings = new GeneSettings();

    }

    //Getters
    public SimulationSettings getSimulationSettings() {
        return simulationSettings;
    }

    public WorldSettings getWorldSettings() {
        return worldSettings;
    }

    public CreatureSettings getCreatureSettings() {
        return creatureSettings;
    }

    public FoodSettings getFoodSettings() {
        return foodSettings;
    }

    public GeneSettings getGeneSettings() {
        return geneSettings;
    }

    //Setters

    public void setGeneSettings(GeneSettings geneSettings) {
        this.geneSettings = geneSettings;
    }

    public void setFoodSettings(FoodSettings foodSettings) {
        this.foodSettings = foodSettings;
    }

    public void setCreatureSettings(CreatureSettings creatureSettings) {
        this.creatureSettings = creatureSettings;
    }

    public void setWorldSettings(WorldSettings worldSettings) {
        this.worldSettings = worldSettings;
    }

    public void setSimulationSettings(SimulationSettings simulationSettings) {
        this.simulationSettings = simulationSettings;
    }
}

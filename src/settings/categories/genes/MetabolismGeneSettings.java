package settings.categories.genes;

import java.io.Serializable;

public record MetabolismGeneSettings(float baseHungerConsumption, float digestionMultiplier, float maxEnergy, float maxHunger) implements Serializable {

    public MetabolismGeneSettings() {
        this(0.05f, 1.0f, 100.0f, 100.0f);
    }

    public MetabolismGeneSettings withBaseHungerConsumption(float baseHungerConsumption) {
        return new MetabolismGeneSettings(baseHungerConsumption, this.digestionMultiplier, this.maxEnergy, this.maxHunger);
    }

    public MetabolismGeneSettings withDigestionMultiplier(float digestionMultiplier) {
        return new MetabolismGeneSettings(this.baseHungerConsumption, digestionMultiplier, this.maxEnergy, this.maxHunger);
    }

    public MetabolismGeneSettings withMaxEnergy(float maxEnergy) {
        return new MetabolismGeneSettings(this.baseHungerConsumption, this.digestionMultiplier, maxEnergy, this.maxHunger);
    }

    public MetabolismGeneSettings withMaxHunger(float maxHunger) {
        return new MetabolismGeneSettings(this.baseHungerConsumption, this.digestionMultiplier, this.maxEnergy, maxHunger);
    }

}

package settings.categories;

import java.io.Serializable;

public record CreatureSettings(float baseEnergy, float baseHunger, float energyLossPerTick, float energyLossPerMovement, float reproductionCost, float pregnancyTicks) implements Serializable {

    public CreatureSettings() {
        this(75, 50, 0.05f, 0.2f, 30, 30);
    }

    public CreatureSettings withBaseEnergy(float baseEnergy) {
        return new CreatureSettings(baseEnergy, this.baseHunger, this.energyLossPerTick, this.energyLossPerMovement, this.reproductionCost, this.pregnancyTicks);
    }

    public CreatureSettings withBaseHunger(float baseHunger) {
        return new CreatureSettings(this.baseEnergy, baseHunger, this.energyLossPerTick, this.energyLossPerMovement, this.reproductionCost, this.pregnancyTicks);
    }

    public CreatureSettings withEnergyLossPerTick(float energyLossPerTick) {
        return new CreatureSettings(this.baseEnergy, this.baseHunger, energyLossPerTick, this.energyLossPerMovement, this.reproductionCost, this.pregnancyTicks);
    }

    public CreatureSettings withEnergyLossPerMovement(float energyLossPerMovement) {
        return new CreatureSettings(this.baseEnergy, this.baseHunger, this.energyLossPerTick, energyLossPerMovement, this.reproductionCost, this.pregnancyTicks);
    }

    public CreatureSettings withReproductionCost(float reproductionCost) {
        return new CreatureSettings(this.baseEnergy, this.baseHunger, this.energyLossPerTick, this.energyLossPerMovement, reproductionCost, this.pregnancyTicks);
    }

    public CreatureSettings withPregnancyTicks(float pregnancyTicks) {
        return new CreatureSettings(this.baseEnergy, this.baseHunger, this.energyLossPerTick, this.energyLossPerMovement, this.reproductionCost, pregnancyTicks);
    }

}

package settings.categories.genes;

import java.io.Serializable;

public record MovementGeneSettings(float baseSpeed, float baseAgility, float energyEfficiency) implements Serializable {

    public MovementGeneSettings() {
        this(1.0f, 3.0f, 1.0f);
    }

    public MovementGeneSettings withBaseSpeed(float baseSpeed) {
        return new MovementGeneSettings(baseSpeed, this.baseAgility, this.energyEfficiency);
    }

    public MovementGeneSettings withBaseAgility(float baseAgility) {
        return new MovementGeneSettings(this.baseSpeed, baseAgility, this.energyEfficiency);
    }

    public MovementGeneSettings withEnergyEfficiency(float energyEfficiency) {
        return new MovementGeneSettings(this.baseSpeed, this.baseAgility, energyEfficiency);
    }

}

package settings.categories.genes;

import java.io.Serializable;

public record MovementGeneSettings(float baseSpeed, float baseAgility, float energyEfficiency, float movementDesire) implements Serializable {

    public MovementGeneSettings() {
        this(1.0f, 3.0f, 1.0f, 1.0f);
    }

    public MovementGeneSettings withBaseSpeed(float baseSpeed) {
        return new MovementGeneSettings(baseSpeed, this.baseAgility, this.energyEfficiency, this.movementDesire);
    }

    public MovementGeneSettings withBaseAgility(float baseAgility) {
        return new MovementGeneSettings(this.baseSpeed, baseAgility, this.energyEfficiency, this.movementDesire);
    }

    public MovementGeneSettings withEnergyEfficiency(float energyEfficiency) {
        return new MovementGeneSettings(this.baseSpeed, this.baseAgility, energyEfficiency, this.movementDesire);
    }

    public MovementGeneSettings withMovementDesire(float movementDesire) {
        return new MovementGeneSettings(this.baseSpeed, this.baseAgility, this.energyEfficiency, movementDesire);
    }

}

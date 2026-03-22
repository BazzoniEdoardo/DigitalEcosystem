package settings.categories;

import java.io.Serializable;

public record SimulationSettings(float tickDuration, float speedMultiplier) implements Serializable {

    public SimulationSettings() {
        this(100, 1.0f);
    }

    public SimulationSettings withTickDuration(float tickDuration) {
        return new SimulationSettings(tickDuration, this.speedMultiplier);
    }

    public SimulationSettings withSpeedMultiplier(float speedMultiplier) {
        return new SimulationSettings(this.tickDuration, speedMultiplier);
    }
}

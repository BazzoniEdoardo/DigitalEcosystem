package settings.categories.genes;

import java.io.Serializable;

public record PerceptionGeneSettings(float visionRange, float visionAngle, float foodPriority) implements Serializable {

    public PerceptionGeneSettings() {
        this(3.0f, 90.0f, 1.0f);
    }

    public PerceptionGeneSettings withVisionRange(float visionRange) {
        return new PerceptionGeneSettings(visionRange, this.visionAngle, this.foodPriority);
    }

    public PerceptionGeneSettings withVisionAngle(float visionAngle) {
        return new PerceptionGeneSettings(this.visionRange, visionAngle, this.foodPriority);
    }

    public PerceptionGeneSettings withFoodPriority(float foodPriority) {
        return new PerceptionGeneSettings(this.visionRange, this.visionAngle, foodPriority);
    }

}

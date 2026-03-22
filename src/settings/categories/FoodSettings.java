package settings.categories;

import java.io.Serializable;

public record FoodSettings(float baseNutrition, float decaymentPerTick) implements Serializable {

    public FoodSettings() {
        this(25, 0.1f);
    }

    public FoodSettings withBaseNutrition(float baseNutrition) {
        return new FoodSettings(baseNutrition, this.decaymentPerTick);
    }

    public FoodSettings withDecaymentPerTick(float decaymentPerTick) {
        return new FoodSettings(this.baseNutrition, decaymentPerTick);
    }

}

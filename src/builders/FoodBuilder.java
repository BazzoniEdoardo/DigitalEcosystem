package builders;

import entities.population.Food;
import entities.movement.Position;

public class FoodBuilder {
    private Position position;
    private float nutrition;

    public FoodBuilder() {}

    public FoodBuilder setPosition(final Position position) {
        this.position = position;
        return this;
    }

    public FoodBuilder setNutrition(final float nutrition) {
        this.nutrition = nutrition;
        return this;
    }

    public Position getPosition() {
        return position;
    }

    public float getNutrition() {
        return nutrition;
    }

    public Food build() throws IllegalArgumentException {
        if (this.position == null) throw new IllegalArgumentException("Position cannot be null");
        if (this.nutrition <= 0) throw new IllegalArgumentException("Nutrition cannot be equal or lower than 0");

        return new Food(this.position, this.nutrition);
    }
}

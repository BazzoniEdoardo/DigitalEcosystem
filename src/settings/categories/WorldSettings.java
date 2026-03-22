package settings.categories;

import java.io.Serializable;

//Aggiungere validazioni sull'input
public record WorldSettings(int width, int height, int basePopulation, int baseFood, float foodPerTick) implements Serializable {

    public WorldSettings() {
        this(50, 50, 20, 50, 0.3f);
    }

    public WorldSettings withWidth(int width) {
        return new WorldSettings(width, this.height, this.basePopulation, this.baseFood, this.foodPerTick);
    }

    public WorldSettings withHeight(int height) {
        return new WorldSettings(this.width, height, this.basePopulation, this.baseFood, this.foodPerTick);
    }

    public WorldSettings withBasePopulation(int basePopulation) {
        return new WorldSettings(this.width, this.height, basePopulation, this.baseFood, this.foodPerTick);
    }

    public WorldSettings withBaseFood(int baseFood) {
        return new WorldSettings(this.width, this.height, this.basePopulation, baseFood, this.foodPerTick);
    }

    public WorldSettings withFoodPerTick(float foodPerTick) {
        return new WorldSettings(this.width, this.height, this.basePopulation, this.baseFood, foodPerTick);
    }
}

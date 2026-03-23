package entities.map.managers;

import builders.FoodBuilder;
import configuration.RandomConfig;
import core.App;
import entities.map.utils.WorldMap;
import entities.movement.Position;
import entities.population.enviroment.Food;
import managers.StatsManager;
import settings.categories.FoodSettings;
import settings.categories.WorldSettings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FoodManager implements Serializable {

    private final List<Food> foods;
    private float foodPerTick;

    public FoodManager() {
        this.foods = Collections.synchronizedList(new ArrayList<>());
        this.foodPerTick = 0;
    }

    public List<Food> getFoods() {
        return foods;
    }

    public boolean addFood(final Food food, final WorldMap worldMap) {
        if (food == null || worldMap == null) return false;
        if (!worldMap.isPositionFree(food.getPosition())) return false;

        foods.add(food);
        worldMap.setCellEntity(food.getPosition(), food);

        return true;
    }

    public void removeExpiredFood() {
        synchronized (foods) {
            foods.removeIf(Food::isExpired);
        }
    }

    public void update(final WorldMap worldMap) {
        final WorldSettings wSettings = App.getSimManager().getSettings().getWorldSettings();
        final FoodSettings fSettings  = App.getSimManager().getSettings().getFoodSettings();

        List<Food> snapshot = new ArrayList<>(foods);
        snapshot.forEach(food -> {
            boolean wasExpired = food.isExpired();
            food.update();
            if (!wasExpired && food.isExpired())
                StatsManager.notifyFoodExpired();
        });

        foodPerTick += wSettings.foodPerTick();
        final int foodToSpawn = (int) Math.floor(foodPerTick);
        foodPerTick -= foodToSpawn;

        for (int i = 0; i < foodToSpawn; i++) {
            spawnRandomFood(worldMap, fSettings, wSettings);
        }
    }

    private void spawnRandomFood(final WorldMap worldMap, final FoodSettings foodSettings, final WorldSettings worldSettings) {
        FoodBuilder builder = new FoodBuilder()
                .setNutrition(RandomConfig.random.nextFloat(
                        foodSettings.baseNutrition() - 3,
                        foodSettings.baseNutrition() + 4))
                .setPosition(randomPosition(worldSettings));

        while (!addFood(builder.build(), worldMap)) {
            builder.setPosition(randomPosition(worldSettings));
        }

        StatsManager.notifyFoodSpawned();
    }

    private Position randomPosition(final WorldSettings worldSettings) {
        return new Position(
                RandomConfig.random.nextInt(0, worldSettings.width()),
                RandomConfig.random.nextInt(0, worldSettings.height())
        );
    }
}

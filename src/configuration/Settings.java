package configuration;

import java.io.Serial;
import java.io.Serializable;

public class Settings implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    //SimulationConfig
    private final float tickDuration = 100; //
    private volatile float speedMultiplier = 1.0f;

    //WorldConfig
    private int width = 50;
    private int height = 50;
    private volatile int basePopulation = 20;
    private volatile int baseFood = 50;
    private volatile float foodPerTick = 0.3f;

    //CreatureConfig
    private volatile float baseEnergy = 75;
    private volatile float baseHunger = 50;
    private volatile float energyLossPerTick = 0.05f;
    private volatile float energyLossPerMove = 0.2f;
    private volatile float reproductionThreshold = 80;
    private volatile float reproductionCost = 30;
    private volatile float pregnancyTicks = 30;

    //FoodConfig
    private volatile float baseNutrition = 25f;
    private volatile float decaymentPerTick = 0.1f;

    public float getFoodPerTick() {
        return foodPerTick;
    }

    public void setFoodPerTick(float foodPerTick) {
        this.foodPerTick = foodPerTick;
    }

    public float getTickDuration() {
        return tickDuration;
    }

    public float getSpeedMultiplier() {
        return speedMultiplier;
    }

    public void setSpeedMultiplier(float speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getBasePopulation() {
        return basePopulation;
    }

    public void setBasePopulation(int basePopulation) {
        this.basePopulation = basePopulation;
    }

    public int getBaseFood() {
        return baseFood;
    }

    public void setBaseFood(int baseFood) {
        this.baseFood = baseFood;
    }

    public float getBaseEnergy() {
        return baseEnergy;
    }

    public void setBaseEnergy(float baseEnergy) {
        this.baseEnergy = baseEnergy;
    }

    public float getBaseHunger() {
        return baseHunger;
    }

    public void setBaseHunger(float baseHunger) {
        this.baseHunger = baseHunger;
    }

    public float getEnergyLossPerTick() {
        return energyLossPerTick;
    }

    public void setEnergyLossPerTick(float energyLossPerTick) {
        this.energyLossPerTick = energyLossPerTick;
    }

    public float getEnergyLossPerMove() {
        return energyLossPerMove;
    }

    public void setEnergyLossPerMove(float energyLossPerMove) {
        this.energyLossPerMove = energyLossPerMove;
    }

    public float getReproductionThreshold() {
        return reproductionThreshold;
    }

    public void setReproductionThreshold(float reproductionThreshold) {
        this.reproductionThreshold = reproductionThreshold;
    }

    public float getReproductionCost() {
        return reproductionCost;
    }

    public void setReproductionCost(float reproductionCost) {
        this.reproductionCost = reproductionCost;
    }

    public float getPregnancyTicks() {
        return pregnancyTicks;
    }

    public void setPregnancyTicks(float pregnancyTicks) {
        this.pregnancyTicks = pregnancyTicks;
    }

    public float getBaseNutrition() {
        return baseNutrition;
    }

    public void setBaseNutrition(float baseNutrition) {
        this.baseNutrition = baseNutrition;
    }

    public float getDecaymentPerTick() {
        return decaymentPerTick;
    }

    public void setDecaymentPerTick(float decaymentPerTick) {
        this.decaymentPerTick = decaymentPerTick;
    }
}

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
    private volatile float reproductionCost = 30;
    private volatile float pregnancyTicks = 30;

    //FoodConfig
    private volatile float baseNutrition = 25f;
    private volatile float decaymentPerTick = 0.1f;

    //GenesSettings
    //MovementGene
    private volatile float baseSpeed = 1.0f;
    private volatile float baseAgility = 3.0f;
    private volatile float energyEfficency = 1.0f;
    //PerceptionGene
    private volatile float visionRange = 3.0f;
    private volatile float visionAngle = 90.0f;
    private volatile float foodPriority = 1.0f;
    //MetabolismGene
    private volatile float baseHungerConsumption = 0.05f;
    private volatile float digestionMultiplier = 1.0f;
    private volatile float maxEnergy = 100.0f;
    private volatile float maxHunger = 100.0f;
    //ReproductionGene
    private volatile float reproductionThreshold = 80.0f;
    private volatile float reproductionRate = 40.0f; // Ogni quanti tick
    private volatile float childrenMultiplier = 1.0f;
    private volatile float mutationRate = 0.2f;
    private volatile float mutationDeviation = 0.2f;
    //BehaviourGene
    private volatile float aggressiveness = 1.0f;
    private volatile float fear = 1.0f;
    private volatile float curiosity = 1.0f;
    private volatile float randomness = 1.0f;
    private volatile float persistence = 1.0f;

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

    public float getFoodPriority() {
        return foodPriority;
    }

    public void setFoodPriority(float foodPriority) {
        this.foodPriority = foodPriority;
    }

    public float getBaseSpeed() {
        return baseSpeed;
    }

    public void setBaseSpeed(float baseSpeed) {
        this.baseSpeed = baseSpeed;
    }

    public float getBaseAgility() {
        return baseAgility;
    }

    public void setBaseAgility(float baseAgility) {
        this.baseAgility = baseAgility;
    }

    public float getEnergyEfficency() {
        return energyEfficency;
    }

    public void setEnergyEfficency(float energyEfficency) {
        this.energyEfficency = energyEfficency;
    }

    public float getVisionRange() {
        return visionRange;
    }

    public void setVisionRange(float visionRange) {
        this.visionRange = visionRange;
    }

    public float getVisionAngle() {
        return visionAngle;
    }

    public void setVisionAngle(float visionAngle) {
        this.visionAngle = visionAngle;
    }

    public float getBaseHungerConsumption() {
        return baseHungerConsumption;
    }

    public void setBaseHungerConsumption(float baseHungerConsumption) {
        this.baseHungerConsumption = baseHungerConsumption;
    }

    public float getDigestionMultiplier() {
        return digestionMultiplier;
    }

    public void setDigestionMultiplier(float digestionMultiplier) {
        this.digestionMultiplier = digestionMultiplier;
    }

    public float getMaxEnergy() {
        return maxEnergy;
    }

    public void setMaxEnergy(float maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    public float getMaxHunger() {
        return maxHunger;
    }

    public void setMaxHunger(float maxHunger) {
        this.maxHunger = maxHunger;
    }

    public float getReproductionRate() {
        return reproductionRate;
    }

    public void setReproductionRate(float reproductionRate) {
        this.reproductionRate = reproductionRate;
    }

    public float getChildrenMultiplier() {
        return childrenMultiplier;
    }

    public void setChildrenMultiplier(float childrenMultiplier) {
        this.childrenMultiplier = childrenMultiplier;
    }

    public float getMutationRate() {
        return mutationRate;
    }

    public void setMutationRate(float mutationRate) {
        this.mutationRate = mutationRate;
    }

    public float getMutationDeviation() {
        return mutationDeviation;
    }

    public void setMutationDeviation(float mutationDeviation) {
        this.mutationDeviation = mutationDeviation;
    }

    public float getAggressiveness() {
        return aggressiveness;
    }

    public void setAggressiveness(float aggressiveness) {
        this.aggressiveness = aggressiveness;
    }

    public float getFear() {
        return fear;
    }

    public void setFear(float fear) {
        this.fear = fear;
    }

    public float getCuriosity() {
        return curiosity;
    }

    public void setCuriosity(float curiosity) {
        this.curiosity = curiosity;
    }

    public float getRandomness() {
        return randomness;
    }

    public void setRandomness(float randomness) {
        this.randomness = randomness;
    }

    public float getPersistence() {
        return persistence;
    }

    public void setPersistence(float persistence) {
        this.persistence = persistence;
    }
}

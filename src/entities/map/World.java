package entities.map;

import builders.CreatureBuilder;
import builders.FoodBuilder;
import configuration.RandomConfig;
import core.App;
import entities.Updatable;
import entities.map.managers.FoodManager;
import entities.map.managers.PopulationManager;
import entities.map.utils.WorldInitializer;
import entities.map.utils.WorldMap;
import entities.movement.MoveResult;
import entities.movement.Position;
import entities.population.living.Creature;
import entities.population.enviroment.Food;
import entities.population.living.PreCreature;
import entities.population.living.genetics.DNA;
import managers.StatsManager;
import settings.categories.CreatureSettings;
import settings.categories.FoodSettings;
import settings.categories.WorldSettings;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public class World implements Updatable, WorldContext, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    protected final WorldMap worldMap;
    protected final PopulationManager populationManager;
    protected final FoodManager foodManager;

    private int tickIndex = 0;

    public World(final int width, final int height) {
        this.worldMap = new WorldMap(width, height);
        this.populationManager = new PopulationManager();
        this.foodManager = new FoodManager();
    }

    public World() {
        this(App.getSimManager().getSettings().getWorldSettings().width(), App.getSimManager().getSettings().getWorldSettings().height());
    }

    public void populate() {
        WorldInitializer.populate(this);
    }

    public boolean hasSimulationEnded() {
        return !populationManager.hasCreatures();
    }

    @Override
    public void update() {
        worldMap.updateEntitiesPositions(populationManager.getCreatures(), foodManager.getFoods());

        populationManager.updateCreatures();
        foodManager.update(worldMap);
        populationManager.updatePreCreatures(this, worldMap);

        populationManager.removeDeadCreatures();
        foodManager.removeExpiredFood();
    }

    @Override
    public MoveResult isMovementAllowed(final Position position) {
        return worldMap.isMovementAllowed(position, foodManager.getFoods());
    }

    @Override
    public boolean addPreCreature(final PreCreature preCreature) {
        return populationManager.addPreCreature(preCreature);
    }

    //GETTERS

    @Override
    public List<Food> getFoods() { return foodManager.getFoods(); }

    public List<Creature> getCreatures() { return populationManager.getCreatures(); }

    public PopulationManager getPopulationManager() {
        return populationManager;
    }

    public FoodManager getFoodManager() {
        return foodManager;
    }

    public WorldMap getWorldMap() {
        return worldMap;
    }

    public int getWidth() {
        return worldMap.getWidth();
    }

    public int getHeight() {
        return worldMap.getHeight();
    }
}

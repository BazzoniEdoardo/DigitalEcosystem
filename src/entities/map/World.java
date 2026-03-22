package entities.map;

import builders.CreatureBuilder;
import builders.FoodBuilder;
import configuration.RandomConfig;
import core.App;
import entities.SimulationEntity;
import entities.movement.MoveResult;
import entities.movement.Position;
import entities.population.Creature;
import entities.population.Food;
import entities.population.PreCreature;
import entities.population.genetics.DNA;
import managers.StatsManager;
import settings.categories.CreatureSettings;
import settings.categories.FoodSettings;
import settings.categories.WorldSettings;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public class World implements SimulationEntity, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    protected int width;
    protected int height;

    protected final List<Creature> creatures;
    protected final List<Food> foods;

    private int tickIndex = 0;
    private long lastTimeUpdateCreaturesNs   = 0L;
    private long lastTimeUpdateFoodNs        = 0L;
    private long lastTimeUpdateMapNs         = 0L;
    private long lastTimeUpdatePreCreaturesNs = 0L;

    protected final char[][] map;

    protected float foodPerTick;

    private final Queue<PreCreature> preCreatures = new LinkedList<>();

    public World(final int width, final int height) {
        setWidth(width);
        setHeight(height);

        this.creatures = Collections.synchronizedList(new ArrayList<>());
        this.foods = Collections.synchronizedList(new ArrayList<>());

        this.map = new char[this.width][this.height];

        this.foodPerTick = 0;

    }

    public World() {
        final WorldSettings settings = App.getSimManager().getSettings().getWorldSettings();

        this(settings.width(), settings.height());
    }

    public World(final World world) {
        this(world.getWidth(), world.getHeight());

        this.creatures.addAll(world.getCreatures());
        this.foods.addAll(world.getFoods());

        for (int i = 0; i < world.getWidth(); i++) {
            for (int j = 0; j < world.getHeight(); j++) {
                this.map[i][j] = world.getMap()[i][j];
            }
        }

        preCreatures.addAll(world.getPreCreatures());
    }

    private void setWidth(final int width) {
        final WorldSettings settings = App.getSimManager().getSettings().getWorldSettings();
        this.width = (width > 0) ? width : settings.width();
    }

    private void setHeight(final int height) {
        final WorldSettings settings = App.getSimManager().getSettings().getWorldSettings();
        this.height = (height > 0) ? height : settings.height();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public synchronized List<Creature> getCreatures() {
        return creatures;
    }

    public synchronized List<Food> getFoods() {
        return foods;
    }

    public char[][] getMap() {
        return map;
    }

    public Queue<PreCreature> getPreCreatures() {
        return preCreatures;
    }

    //MAIN METHODS

    public void populate() {
        updateMap();
        populateCreatures();
        populateFood();
    }

    private void populateCreatures() {
        final WorldSettings settings = App.getSimManager().getSettings().getWorldSettings();
        final CreatureSettings cSettings = App.getSimManager().getSettings().getCreatureSettings();
        for (int i = 0; i < settings.basePopulation(); i++) {

            CreatureBuilder builder = new CreatureBuilder()
                    .setEnergy(RandomConfig.random.nextFloat(cSettings.baseEnergy() - 10, cSettings.baseEnergy() + 11))
                    .setHunger(RandomConfig.random.nextFloat(cSettings.baseHunger() - 10, cSettings.baseHunger() + 11))
                    .setPosition(new Position(RandomConfig.random.nextInt(0, width), RandomConfig.random.nextInt(0, height)))
                    .setDna(new DNA())
                    .setWorld(this);

            while (!addCreature(builder.build())) {
                builder.setPosition(new Position(RandomConfig.random.nextInt(0, width), RandomConfig.random.nextInt(0, height)));
            }
        }
    }

    private void populateFood() {
        final WorldSettings settings = App.getSimManager().getSettings().getWorldSettings();
        final FoodSettings fSettings = App.getSimManager().getSettings().getFoodSettings();

        for (int i = 0; i < settings.baseFood(); i++) {

            FoodBuilder builder = new FoodBuilder()
                    .setNutrition(RandomConfig.random.nextFloat(fSettings.baseNutrition() - 3, fSettings.baseNutrition() + 4))
                    .setPosition(new Position(RandomConfig.random.nextInt(0, width), RandomConfig.random.nextInt(0, height)));

            while (!addFood(builder.build())) {
                builder.setPosition(new Position(RandomConfig.random.nextInt(0, width), RandomConfig.random.nextInt(0, height)));
            }
        }
    }

    private boolean addCreature(final Creature creature) {
        if (!isPositionFree(creature.getPosition())) return false;

        this.creatures.add(creature);
        return true;
    }

    public boolean addPreCreature(final PreCreature preCreature) {
        if (preCreature == null) return false;

        this.preCreatures.add(preCreature);
        return true;
    }

    private boolean addFood(final Food food) {
        if (!isPositionFree(food.getPosition())) return false;

        this.foods.add(food);
        return true;
    }

    private boolean isPositionFree(final Position position) {
        if (position.x() < 0 || position.x() >= width || position.y() < 0 || position.y() >= height) return false;

        return this.map[position.x()][position.y()] == '.';
    }

    public MoveResult isMovementAllowed(final Position position) {
        if (position.x() < 0 || position.x() >= width || position.y() < 0 || position.y() >= height) return new MoveResult(false, null);

        if (this.map[position.x()][position.y()] == 'C') return new MoveResult(false, null);
        if (this.map[position.x()][position.y()] == '.') return new MoveResult(true, null);

        return this.foods.stream()
                .filter(f -> f.getPosition().equals(position))
                .findFirst()
                .map(f -> new MoveResult(true, f))
                .orElse(new MoveResult(true, null));

    }

    private void updateMap() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                this.map[i][j] = '.';
            }
        }

        creatures.removeIf(c -> !c.isAlive());
        foods.removeIf(Food::isExpired);

        for (Creature c : creatures) {
            map[c.getPosition().x()][c.getPosition().y()] = 'C';
        }

        for (Food f : foods) {
            map[f.getPosition().x()][f.getPosition().y()] = 'F';
        }
    }

    private void printMap() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                System.out.print(this.map[i][j] + "\t");
            }

            System.out.println();
        }
        System.out.println("\n");

        StatsManager.printAveragePopulationEnergy(creatures);
        StatsManager.printAverageFoodNutrition(foods);
    }

    private void updateCreatures() {
        final List<Creature> tmp = new ArrayList<>(this.creatures);

        tmp.forEach(Creature::update);
    }

    private void updateFood() {
        final WorldSettings settings = App.getSimManager().getSettings().getWorldSettings();
        final FoodSettings fSettings = App.getSimManager().getSettings().getFoodSettings();

        this.foodPerTick += settings.foodPerTick();


        final List<Food> tmp = new ArrayList<>(this.foods);
        tmp.forEach(food -> {
            boolean wasExpired = food.isExpired();
            food.update();
            if (!wasExpired && food.isExpired()) StatsManager.notifyFoodExpired(); // <-- AGGIUNGERE
        });

        //Generazione del cibo
        final int foodToSpawn = (int) Math.floor(this.foodPerTick);
        for (int i = 0; i < foodToSpawn; i++) {
            FoodBuilder builder = new FoodBuilder()
                    .setNutrition(RandomConfig.random.nextFloat(fSettings.baseNutrition() - 3, fSettings.baseNutrition() + 4))
                    .setPosition(new Position(RandomConfig.random.nextInt(0, width), RandomConfig.random.nextInt(0, height)));

            while (!addFood(builder.build())) {
                builder.setPosition(new Position(RandomConfig.random.nextInt(0, width), RandomConfig.random.nextInt(0, height)));
            }

            StatsManager.notifyFoodSpawned(); // <-- AGGIUNGERE (dopo il while)
        }

        this.foodPerTick -= foodToSpawn;

    }

    private void updatePreCreatures() {
        final CreatureSettings cSettings = App.getSimManager().getSettings().getCreatureSettings();

        if (preCreatures.isEmpty()) return;

        preCreatures.forEach(PreCreature::update);


        PreCreature next;
        while ((next = preCreatures.peek()) != null && next.getDaysTillBorn() <= 0) {
            final PreCreature child = preCreatures.poll();
            final Creature parent = child.getParent();
            final Position parentPos = parent.getPosition();
            Position firstFreePosition = null;

            search:
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    Position pos = new Position(parentPos.x()-1+i, parentPos.y()-1+j);
                    if (isPositionFree(pos)) {
                        firstFreePosition = pos;
                        break search;
                    }
                }
            }

            //NESSUNO SPAZIO DISPONIBILE, MUORE
            if (firstFreePosition == null) {
                StatsManager.notifyPreCreatureDied(); // <-- AGGIUNGERE
                continue;
            }

            //NASCITA DELLA CREATURA
            CreatureBuilder builder = new CreatureBuilder()
                    .setEnergy(RandomConfig.random.nextFloat(cSettings.baseEnergy() - 10, cSettings.baseEnergy() + 11))
                    .setHunger(RandomConfig.random.nextFloat(cSettings.baseHunger() - 10, cSettings.baseHunger() + 11))
                    .setPosition(firstFreePosition)
                    .setDna(parent.getDna())
                    .setWorld(this);

            addCreature(builder.build());
            StatsManager.notifyCreatureBorn(); // <-- AGGIUNGERE
        }

    }

    @Override
    public int getId() {
        return -1;
    }

    @Override
    public void update() {
        StatsManager.startTimer();
        updateCreatures();
        lastTimeUpdateCreaturesNs = StatsManager.stopTimer();

        StatsManager.startTimer();
        updateFood();
        lastTimeUpdateFoodNs = StatsManager.stopTimer();

        StatsManager.startTimer();
        updatePreCreatures();
        lastTimeUpdatePreCreaturesNs = StatsManager.stopTimer();

        StatsManager.startTimer();
        updateMap();
        lastTimeUpdateMapNs = StatsManager.stopTimer();

        //printMap();

        StatsManager.recordTick(
                tickIndex++,
                creatures,
                foods,
                preCreatures.size(),
                lastTimeUpdateCreaturesNs,
                lastTimeUpdateFoodNs,
                lastTimeUpdateMapNs,
                lastTimeUpdatePreCreaturesNs
        );
    }

    public boolean hasSimulationEnded() {
        return creatures.isEmpty();
    }

    @Override
    public World clone() {
        return new World(this);
    }
}

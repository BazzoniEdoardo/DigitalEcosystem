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
import managers.StatsManager;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public class World implements SimulationEntity, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    protected int width;
    protected int height;

    protected final ArrayList<Creature> creatures;
    protected final ArrayList<Food> foods;

    private int tickIndex = 0;
    private long lastTimeUpdateCreaturesNs   = 0L;
    private long lastTimeUpdateFoodNs        = 0L;
    private long lastTimeUpdateMapNs         = 0L;
    private long lastTimeUpdatePreCreaturesNs = 0L;

    protected final char[][] map;

    protected float foodPerTick;

    public static final Queue<PreCreature> preCreatures = new LinkedList<>();

    public World(final int width, final int height) {
        setWidth(width);
        setHeight(height);

        this.creatures = new ArrayList<>();
        this.foods = new ArrayList<>();

        this.map = new char[this.width][this.height];

        this.foodPerTick = 0;

    }

    public World() {
        this(App.getSimManager().getSettings().getWidth(), App.getSimManager().getSettings().getHeight());
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
        this.width = (width > 0) ? width : App.getSimManager().getSettings().getWidth();
    }

    private void setHeight(final int height) {
        this.height = (height > 0) ? height : App.getSimManager().getSettings().getHeight();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ArrayList<Creature> getCreatures() {
        return creatures;
    }

    public ArrayList<Food> getFoods() {
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
        for (int i = 0; i < App.getSimManager().getSettings().getBasePopulation(); i++) {

            CreatureBuilder builder = new CreatureBuilder()
                    .setEnergy(RandomConfig.random.nextFloat(App.getSimManager().getSettings().getBaseEnergy() - 10, App.getSimManager().getSettings().getBaseEnergy() + 11))
                    .setHunger(RandomConfig.random.nextFloat(App.getSimManager().getSettings().getBaseHunger() - 10, App.getSimManager().getSettings().getBaseHunger() + 11))
                    .setPosition(new Position(RandomConfig.random.nextInt(0, width), RandomConfig.random.nextInt(0, height)));

            while (!addCreature(builder.build())) {
                builder.setPosition(new Position(RandomConfig.random.nextInt(0, width), RandomConfig.random.nextInt(0, height)));
            }
        }
    }

    private void populateFood() {
        for (int i = 0; i < App.getSimManager().getSettings().getBaseFood(); i++) {

            FoodBuilder builder = new FoodBuilder()
                    .setNutrition(RandomConfig.random.nextFloat(App.getSimManager().getSettings().getBaseNutrition() - 3, App.getSimManager().getSettings().getBaseNutrition() + 4))
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

    private boolean addFood(final Food food) {
        if (!isPositionFree(food.getPosition())) return false;

        this.foods.add(food);
        return true;
    }

    private boolean removeCreature(final Creature creature) {
        return creatures.remove(creature);
    }

    private boolean removeFood(final Food food) {
        return foods.remove(food);
    }

    private boolean isPositionFree(final Position position) {
        if (position.x() < 0 || position.x() >= width || position.y() < 0 || position.y() >= height) return false;

        return this.map[position.x()][position.y()] == '.';
    }

    private MoveResult isMovementAllowed(final Position position) {
        if (position.x() < 0 || position.x() >= width || position.y() < 0 || position.y() >= height) return new MoveResult(false, null);

        if (this.map[position.x()][position.y()] == '.') return new MoveResult(true, null);
        if (this.map[position.x()][position.y()] == 'C') return new MoveResult(false, null);

        Food food = new Food();
        for (Food f : foods) {
            if (Objects.equals(f.getPosition(), position)) {
                food = f;
            }
        }

        return new MoveResult(true, food);
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
        this.creatures.forEach(creature -> {
            Position newPos = creature.getNextPosition();
            MoveResult result = isMovementAllowed(newPos);

            if (!result.allowed()) {
                boolean wasAlive = creature.isAlive();
                creature.update();
                if (wasAlive && !creature.isAlive()) StatsManager.notifyCreatureDied(); // <-- AGGIUNGERE
                return;
            }

            creature.move(newPos);

            if (result.food() != null) {
                creature.eat(result.food());
                StatsManager.notifyFoodEaten();  // <-- AGGIUNGERE
                foods.remove(result.food());
            }

            boolean wasAlive = creature.isAlive();        // <-- AGGIUNGERE
            creature.update();
            if (wasAlive && !creature.isAlive()) StatsManager.notifyCreatureDied(); // <-- AGGIUNGERE
        });
    }

    private void updateFood() {
        this.foodPerTick += App.getSimManager().getSettings().getFoodPerTick();

        this.foods.forEach(food -> {
            boolean wasExpired = food.isExpired();
            food.update();
            if (!wasExpired && food.isExpired()) StatsManager.notifyFoodExpired(); // <-- AGGIUNGERE
        });

        //Generazione del cibo
        final int foodToSpawn = (int) Math.floor(this.foodPerTick);
        for (int i = 0; i < foodToSpawn; i++) {
            FoodBuilder builder = new FoodBuilder()
                    .setNutrition(RandomConfig.random.nextFloat(App.getSimManager().getSettings().getBaseNutrition() - 3, App.getSimManager().getSettings().getBaseNutrition() + 4))
                    .setPosition(new Position(RandomConfig.random.nextInt(0, width), RandomConfig.random.nextInt(0, height)));

            while (!addFood(builder.build())) {
                builder.setPosition(new Position(RandomConfig.random.nextInt(0, width), RandomConfig.random.nextInt(0, height)));
            }

            StatsManager.notifyFoodSpawned(); // <-- AGGIUNGERE (dopo il while)
        }

        this.foodPerTick -= foodToSpawn;

    }

    private void updatePreCreatures() {
        if (preCreatures.isEmpty()) return;

        preCreatures.forEach(PreCreature::update);

        if (preCreatures.peek().getDaysTillBorn() <= 0) {
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
                return;
            }

            //NASCITA DELLA CREATURA
            CreatureBuilder builder = new CreatureBuilder()
                    .setEnergy(RandomConfig.random.nextFloat(App.getSimManager().getSettings().getBaseEnergy() - 10, App.getSimManager().getSettings().getBaseEnergy() + 11))
                    .setHunger(RandomConfig.random.nextFloat(App.getSimManager().getSettings().getBaseHunger() - 10, App.getSimManager().getSettings().getBaseHunger() + 11))
                    .setPosition(firstFreePosition);

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
    public SimulationEntity clone() {
        return new World(this);
    }
}

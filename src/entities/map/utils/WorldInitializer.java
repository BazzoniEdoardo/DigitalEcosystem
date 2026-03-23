package entities.map.utils;

import builders.CreatureBuilder;
import builders.FoodBuilder;
import configuration.RandomConfig;
import core.App;
import entities.map.World;
import entities.movement.Position;
import entities.population.living.genetics.DNA;
import settings.categories.CreatureSettings;
import settings.categories.FoodSettings;
import settings.categories.WorldSettings;

public final class WorldInitializer {

    private WorldInitializer() {}

    public static void populate(final World world) {
        populateCreatures(world);
        populateFood(world);
    }

    private static void populateCreatures(final World world) {
        final WorldSettings wSettings = App.getSimManager().getSettings().getWorldSettings();
        final CreatureSettings cSettings = App.getSimManager().getSettings().getCreatureSettings();

        for (int i = 0; i < wSettings.basePopulation(); i++) {
            CreatureBuilder builder = new CreatureBuilder()
                    .setEnergy(RandomConfig.random.nextFloat(
                            cSettings.baseEnergy() - 10,
                            cSettings.baseEnergy() + 11))
                    .setHunger(RandomConfig.random.nextFloat(
                            cSettings.baseHunger() - 10,
                            cSettings.baseHunger() + 11))
                    .setDna(new DNA())
                    .setWorld(world)
                    .setPosition(randomPosition(world));

            // Continua a provare finché non trova una posizione libera
            while (!world.getPopulationManager().addCreature(builder.build(), world.getWorldMap())) {
                builder.setPosition(randomPosition(world));
            }
        }
    }

    private static void populateFood(final World world) {
        final WorldSettings wSettings = App.getSimManager().getSettings().getWorldSettings();
        final FoodSettings fSettings = App.getSimManager().getSettings().getFoodSettings();

        for (int i = 0; i < wSettings.baseFood(); i++) {
            FoodBuilder builder = new FoodBuilder()
                    .setNutrition(RandomConfig.random.nextFloat(
                            fSettings.baseNutrition() - 3,
                            fSettings.baseNutrition() + 4))
                    .setPosition(randomPosition(world));

            while (!world.getFoodManager().addFood(builder.build(), world.getWorldMap())) {
                builder.setPosition(randomPosition(world));
            }
        }
    }

    private static Position randomPosition(final World world) {
        return new Position(
                RandomConfig.random.nextInt(0, world.getWidth()),
                RandomConfig.random.nextInt(0, world.getHeight())
        );
    }
}

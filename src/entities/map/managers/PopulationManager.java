package entities.map.managers;

import builders.CreatureBuilder;
import core.App;
import entities.map.WorldContext;
import entities.map.utils.WorldMap;
import entities.movement.Position;
import entities.population.living.Creature;
import entities.population.living.PreCreature;
import managers.StatsManager;
import settings.categories.CreatureSettings;

import java.io.Serializable;
import java.util.*;

public class PopulationManager implements Serializable {

    private final List<Creature> creatures;
    private final Queue<PreCreature> preCreatures;

    public PopulationManager() {
        this.creatures = Collections.synchronizedList(new ArrayList<>());
        this.preCreatures = new ArrayDeque<>();
    }

    //CRUD

    //READ

    public List<Creature> getCreatures() {
        return creatures;
    }

    public Queue<PreCreature> getPreCreatures() {
        return preCreatures;
    }

    //CREATE

    public boolean addCreature(final Creature creature, WorldMap worldMap) {
        if (creature == null || worldMap == null) return false;
        if (!worldMap.isPositionFree(creature.getPosition())) return false;

        creatures.add(creature);
        worldMap.setCellEntity(creature.getPosition(), creature);
        return true;
    }

    public boolean addPreCreature(final PreCreature preCreature) {
        if (preCreature == null) return false;

        preCreatures.add(preCreature);
        return true;
    }

    //UPDATE
    //Accesso concorrente quindi copia dell'array
    public void updateCreatures() {
        List<Creature> temp = new ArrayList<>(creatures);
        temp.forEach(Creature::update);
    }

    //Nessun accesso concorrente (per ora)
    public void updatePreCreatures(final WorldContext world, final WorldMap worldMap) {
//        Queue<PreCreature> temp = new ArrayDeque<>(preCreatures);

        if (!preCreatures.isEmpty()) {
            preCreatures.forEach(PreCreature::update);

            spawnReadyCreatures(world, worldMap);
        }
    }

    //DELETE

    public void removeDeadCreatures() {
        synchronized (creatures) {
            creatures.removeIf(c -> !c.isAlive());
        }
    }

    //NASCITA

    private void spawnReadyCreatures(final WorldContext world,final WorldMap worldMap) {
        final CreatureSettings cSettings =
                App.getSimManager().getSettings().getCreatureSettings();

        PreCreature next;
        while ((next = preCreatures.peek()) != null && next.getDaysTillBorn() <= 0) {
            PreCreature child = preCreatures.poll();
            Creature parent = child.getParent();
            Position free = findFreePositionAround(parent.getPosition(), worldMap);

            if (free == null) {
                StatsManager.notifyPreCreatureDied();
                continue;
            }

            final Creature born = new CreatureBuilder()
                    .setEnergy(parent.getEnergy())
                    .setPosition(free)
                    .setDna(parent.getDna())
                    .setWorld(world)
                    .build();

            addCreature(born, worldMap);
            StatsManager.notifyCreatureBorn();
        }
    }


    //Aggiungere config per il radius della nascita
    private Position findFreePositionAround(final Position center, final WorldMap worldMap) {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                Position p = new Position(center.x()-1+i, center.y()-1+j);
                if (worldMap.isPositionFree(p)) return p;
            }
        return null;
    }

    public boolean hasCreatures() {
        return !creatures.isEmpty();
    }

}

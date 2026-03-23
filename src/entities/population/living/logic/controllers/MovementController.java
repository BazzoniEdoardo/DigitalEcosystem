package entities.population.living.logic.controllers;

import entities.map.World;
import entities.map.WorldContext;
import entities.movement.MoveResult;
import entities.movement.Position;
import entities.population.living.Creature;
import entities.population.living.genetics.DNA;

import java.util.LinkedList;
import java.util.Queue;

public class MovementController {

    //Qui sara' implementato il pathfinding ecc...
    private final Queue<Position> nextMovement;

    public MovementController() {

        this.nextMovement = new LinkedList<>();

    };

    //Implementare la coda
    public void move(final Creature creature) {
        final WorldContext world = creature.getWorld();
        final Position target = isFoodNearby(creature, world);

        if (target != null) {
            final Position position = creature.getPosition();

            final int dx = Integer.signum(target.x() - position.x()); //Gli dico se muoversi su o giu o non modificare
            final int dy = Integer.signum(target.y() - position.y());

            final Position nextStep = new Position(position.x() + dx, position.y() + dy);
            final MoveResult result = world.isMovementAllowed(nextStep);

            if (result.allowed()) {
                creature.move(nextStep);

                if (result.food() != null) {
                    creature.eat(result.food());
                    world.getFoods().remove(result.food());
                }
            }

        }else {
            creature.moveRandom();
        }

    }

    //Implementare dopo la coda
    public Position isFoodNearby(final Creature creature, final WorldContext world) {
        final DNA dna = creature.getDna();
        final Position position = creature.getPosition();
        final float visionRange = dna.getPerceptionGene().getGeneAttribute("visionRange");
        final int range = (int) visionRange;

        Position closestFood = null;
        float bestDistance = Float.MAX_VALUE;

        //Aggiunta futura del pathfinding per trovare quello piu' vicino, e aggiunta di una lista di prossime mosse
        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                Position currentPosition = new Position(position.x() + dx, position.y() + dy);
                MoveResult result = world.isMovementAllowed(currentPosition);

                if (result.allowed() && result.food() != null) {
                    float distance = Math.abs(currentPosition.x() - position.x()) + Math.abs(currentPosition.y() - position.y());

                    if (distance < bestDistance) {
                        bestDistance = distance;
                        closestFood = currentPosition;
                    }

                }
            }
        }

        //nextMovement.add(closestFood);
        return closestFood;

    }
}

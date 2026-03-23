package entities.population.living.logic;

import entities.population.living.Creature;
import entities.population.living.logic.activity.Action;
import entities.population.living.logic.activity.ActionScore;
import entities.population.living.logic.controllers.MovementController;
import entities.population.living.logic.evaluators.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Brain {

    protected final Creature parent;
    protected List<ActionEvaluator> evaluators;

    protected MovementController movementController;

    public Brain(final Creature parent) {
        this.parent = parent;

        this.evaluators = Collections.synchronizedList(new ArrayList<>());

        this.evaluators.add(new EatingEvaluator());
        this.evaluators.add(new EnergyEvaluator());
        this.evaluators.add(new MovementEvaluator());
        this.evaluators.add(new ReproductionEvaluator());

        this.movementController = new MovementController();
    }

    public Action decide() {
        //Lista dei punteggi per il decision making
        List<ActionScore> scores = new ArrayList<>();

        evaluators.forEach(e -> scores.add(e.evaluate(parent)));

        final ActionScore action = chooseBestAction(scores);

        return action.getAction();
    }

    public void movementDecision() {
        movementController.move(parent);
    }

    //Si puo' modificare aggiungendo confronti con il DNA per la scelta finale, oppure gia' dentro l'evaluate si va a calcolare considerando il dna (cosa da fare secondo me).
    protected ActionScore chooseBestAction(final List<ActionScore> scores) {
        return scores.stream().max(Comparator.comparingDouble(ActionScore::getScore)).get();
    }

}

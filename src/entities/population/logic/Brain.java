package entities.population.logic;

import entities.population.Creature;
import entities.population.logic.activity.Action;
import entities.population.logic.activity.ActionScore;
import entities.population.logic.evaluators.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Brain {

    protected final Creature parent;
    protected List<ActionEvaluator> evaluators;

    public Brain(final Creature parent) {
        this.parent = parent;

        this.evaluators = Collections.synchronizedList(new ArrayList<>());

        this.evaluators.add(new EatingEvaluator());
        this.evaluators.add(new IdlingEvaluator());
        this.evaluators.add(new MovementEvaluator());
        this.evaluators.add(new ReproductionEvaluator());
    }

    public Action decide() {
        //Lista dei punteggi per il decision making
        List<ActionScore> scores = new ArrayList<>();

        evaluators.forEach(e -> scores.add(e.evaluate(parent)));

        final ActionScore action = chooseBestAction(scores);

        if (action.getAction() == Action.REPRODUCE) {
            System.out.println("ID: " + parent.getId() + " | Choosen: " + action.getAction() + " | Score: " + action.getScore());
        }

        return action.getAction();
    }

    //Si puo' modificare aggiungendo confronti con il DNA per la scelta finale, oppure gia' dentro l'evaluate si va a calcolare considerando il dna (cosa da fare secondo me).
    protected ActionScore chooseBestAction(final List<ActionScore> scores) {
        return scores.stream().max(Comparator.comparingDouble(ActionScore::getScore)).get();
    }

}

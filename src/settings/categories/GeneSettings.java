package settings.categories;

import settings.categories.genes.*;

import java.io.Serializable;

public record GeneSettings(BehaviourGeneSettings behaviourGeneSettings, MetabolismGeneSettings metabolismGeneSettings, MovementGeneSettings movementGeneSettings, PerceptionGeneSettings perceptionGeneSettings, ReproductionGeneSettings reproductionGeneSettings) implements Serializable {

    public GeneSettings() {
        this(new BehaviourGeneSettings(), new MetabolismGeneSettings(), new MovementGeneSettings(), new PerceptionGeneSettings(), new ReproductionGeneSettings());
    }

    public GeneSettings withBehaviourGeneSettings(BehaviourGeneSettings behaviourGeneSettings) {
        return new GeneSettings(
                behaviourGeneSettings,
                this.metabolismGeneSettings,
                this.movementGeneSettings,
                this.perceptionGeneSettings,
                this.reproductionGeneSettings
        );
    }

    public GeneSettings withMetabolismGeneSettings(MetabolismGeneSettings metabolismGeneSettings) {
        return new GeneSettings(
                this.behaviourGeneSettings,
                metabolismGeneSettings,
                this.movementGeneSettings,
                this.perceptionGeneSettings,
                this.reproductionGeneSettings
        );
    }

    public GeneSettings withMovementGeneSettings(MovementGeneSettings movementGeneSettings) {
        return new GeneSettings(
                this.behaviourGeneSettings,
                this.metabolismGeneSettings,
                movementGeneSettings,
                this.perceptionGeneSettings,
                this.reproductionGeneSettings
        );
    }

    public GeneSettings withPerceptionGeneSettings(PerceptionGeneSettings perceptionGeneSettings) {
        return new GeneSettings(
                this.behaviourGeneSettings,
                this.metabolismGeneSettings,
                this.movementGeneSettings,
                perceptionGeneSettings,
                this.reproductionGeneSettings
        );
    }

    public GeneSettings withReproductionGeneSettings(ReproductionGeneSettings reproductionGeneSettings) {
        return new GeneSettings(
                this.behaviourGeneSettings,
                this.metabolismGeneSettings,
                this.movementGeneSettings,
                this.perceptionGeneSettings,
                reproductionGeneSettings
        );
    }

}

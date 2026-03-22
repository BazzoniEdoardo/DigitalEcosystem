package settings.categories.genes;

import java.io.Serializable;

public record BehaviourGeneSettings(float aggressiveness, float fear, float curiosity, float randomness, float persistence) implements Serializable {

    public BehaviourGeneSettings() {
        this(1.0f, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    public BehaviourGeneSettings withAggressiveness(float aggressiveness) {
        return new BehaviourGeneSettings(aggressiveness, this.fear, this.curiosity, this.randomness, this.persistence);
    }

    public BehaviourGeneSettings withFear(float fear) {
        return new BehaviourGeneSettings(this.aggressiveness, fear, this.curiosity, this.randomness, this.persistence);
    }

    public BehaviourGeneSettings withCuriosity(float curiosity) {
        return new BehaviourGeneSettings(this.aggressiveness, this.fear, curiosity, this.randomness, this.persistence);
    }

    public BehaviourGeneSettings withRandomness(float randomness) {
        return new BehaviourGeneSettings(this.aggressiveness, this.fear, this.curiosity, randomness, this.persistence);
    }

    public BehaviourGeneSettings withPersistence(float persistence) {
        return new BehaviourGeneSettings(this.aggressiveness, this.fear, this.curiosity, this.randomness, persistence);
    }

}

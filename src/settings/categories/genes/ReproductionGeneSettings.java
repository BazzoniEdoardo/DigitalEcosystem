package settings.categories.genes;

import java.io.Serializable;

public record ReproductionGeneSettings(float reproductionThreshold, float reproductionRate, float childrenMultiplier, float mutationRate, float mutationDeviation, float reproductionDesire) implements Serializable {

    public ReproductionGeneSettings() {
        this(80.0f, 40.0f, 1.0f, 0.2f, 0.2f, 1f);
    }

    public ReproductionGeneSettings withReproductionThreshold(float reproductionThreshold) {
        return new ReproductionGeneSettings(reproductionThreshold, this.reproductionRate, this.childrenMultiplier, this.mutationRate, this.mutationDeviation, this.reproductionDesire);
    }

    public ReproductionGeneSettings withReproductionRate(float reproductionRate) {
        return new ReproductionGeneSettings(this.reproductionThreshold, reproductionRate, this.childrenMultiplier, this.mutationRate, this.mutationDeviation, this.reproductionDesire);
    }

    public ReproductionGeneSettings withChildrenMultiplier(float childrenMultiplier) {
        return new ReproductionGeneSettings(this.reproductionThreshold, this.reproductionRate, childrenMultiplier, this.mutationRate, this.mutationDeviation, this.reproductionDesire);
    }

    public ReproductionGeneSettings withMutationRate(float mutationRate) {
        return new ReproductionGeneSettings(this.reproductionThreshold, this.reproductionRate, this.childrenMultiplier, mutationRate, this.mutationDeviation, this.reproductionDesire);
    }

    public ReproductionGeneSettings withMutationDeviation(float mutationDeviation) {
        return new ReproductionGeneSettings(this.reproductionThreshold, this.reproductionRate, this.childrenMultiplier, this.mutationRate, mutationDeviation, this.reproductionDesire);
    }

    public ReproductionGeneSettings withReproductionDesire(float reproductionDesire) {
        return new ReproductionGeneSettings(this.reproductionThreshold, this.reproductionRate, this.childrenMultiplier, this.mutationRate, this.mutationDeviation, reproductionDesire);
    }

}

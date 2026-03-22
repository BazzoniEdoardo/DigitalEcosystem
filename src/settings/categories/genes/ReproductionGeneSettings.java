package settings.categories.genes;

import java.io.Serializable;

public record ReproductionGeneSettings(float reproductionThreshold, float reproductionRate, float childrenMultiplier, float mutationRate, float mutationDeviation) implements Serializable {

    public ReproductionGeneSettings() {
        this(80.0f, 40.0f, 1.0f, 0.2f, 0.2f);
    }

    public ReproductionGeneSettings withReproductionThreshold(float reproductionThreshold) {
        return new ReproductionGeneSettings(reproductionThreshold, this.reproductionRate, this.childrenMultiplier, this.mutationRate, this.mutationDeviation);
    }

    public ReproductionGeneSettings withReproductionRate(float reproductionRate) {
        return new ReproductionGeneSettings(this.reproductionThreshold, reproductionRate, this.childrenMultiplier, this.mutationRate, this.mutationDeviation);
    }

    public ReproductionGeneSettings withChildrenMultiplier(float childrenMultiplier) {
        return new ReproductionGeneSettings(this.reproductionThreshold, this.reproductionRate, childrenMultiplier, this.mutationRate, this.mutationDeviation);
    }

    public ReproductionGeneSettings withMutationRate(float mutationRate) {
        return new ReproductionGeneSettings(this.reproductionThreshold, this.reproductionRate, this.childrenMultiplier, mutationRate, this.mutationDeviation);
    }

    public ReproductionGeneSettings withMutationDeviation(float mutationDeviation) {
        return new ReproductionGeneSettings(this.reproductionThreshold, this.reproductionRate, this.childrenMultiplier, this.mutationRate, mutationDeviation);
    }

}

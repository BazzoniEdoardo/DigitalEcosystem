package entities.population.living.genetics.genes;

import configuration.RandomConfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractGene<T extends AbstractGene<T>> implements Gene {

    protected Map<String, Float> geneAttributes;

    public AbstractGene() {
        geneAttributes = new HashMap<>();
    }

    public AbstractGene(final AbstractGene<T> gene)  {
        geneAttributes = new HashMap<>(gene.getGenes());
    }

    public Map<String, Float> getGenes() {
        return Collections.unmodifiableMap(geneAttributes);
    }

    protected void setGeneAttribute(final String key, final Float attribute) {
        if (key == null || attribute == null) return;

        geneAttributes.put(key, attribute);
    }

    @Override
    public Float getGeneAttribute(final String key) {
        return geneAttributes.get(key);
    }

    protected abstract T self();
    public abstract T clone();

    public T mutate(final float deviation) {
        final T clone = clone();

        clone.geneAttributes.replaceAll((key, value) -> Math.abs(RandomConfig.random.nextFloat((float)value - deviation, (float)value + deviation)));

        return clone;
    }

    @Override
    public String toString() {
        return geneAttributes.toString();
    }



}

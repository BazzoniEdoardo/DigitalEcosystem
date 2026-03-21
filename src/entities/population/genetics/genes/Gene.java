package entities.population.genetics.genes;

import java.util.Map;

public interface Gene {

    Map<String, Float> getGenes();

    Float getGeneAttribute(final String key);
    //void setGeneAttribute(final String key, final Float attribute);

    Gene mutate(final float deviation);
    Gene clone();

    void update();
}

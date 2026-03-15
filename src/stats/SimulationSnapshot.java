package stats;

import entities.Food;
import entities.population.Creature;

import java.util.ArrayList;
import java.util.List;

/**
 * Cattura un'istantanea dello stato della simulazione ad ogni tick.
 * Include dati di simulazione, demografia estesa e metriche di performance.
 */
public record SimulationSnapshot(

        // --- IDENTIFICAZIONE TICK ---
        int tick,

        // --- STATO SIMULAZIONE ---
        int populationCount,
        int foodCount,
        int preCreatureCount,

        // --- EVENTI TICK: demografia ---
        int deathsThisTick,           // morti per energia esaurita
        int preCreatureDeathsThisTick,// PreCreature morte per mancanza di spazio
        int birthsThisTick,           // nascite avvenute con successo
        int foodEatenThisTick,
        int foodExpiredThisTick,
        int foodSpawnedThisTick,      // cibo generato dinamicamente questo tick

        // --- DATI GREZZI PER STATISTICHE ---
        List<Float> creatureEnergies,
        List<Float> foodNutritions,

        // --- PERFORMANCE: tempi in nanosecondi ---
        long timeUpdateCreaturesNs,
        long timeUpdateFoodNs,
        long timeUpdateMapNs,
        long timeUpdatePreCreaturesNs,

        // --- PERFORMANCE: memoria heap in bytes ---
        long heapUsedBytes,
        long heapFreeBytes,
        long heapTotalBytes,

        // --- PERFORMANCE: dimensioni strutture dati stimate ---
        long creaturesMemBytes,      // stima: creatures list
        long foodsMemBytes,          // stima: foods list
        long preCreaturesMemBytes    // stima: preCreatures queue

) {
    /**
     * Costanti per la stima della dimensione in memoria degli oggetti.
     * Valori approssimativi basati sul layout JVM:
     *   Creature: 16 header + id(4) + energy(4) + hunger(4) + alive(1) + moving(1)
     *             + position ref(8) + Position(16+4+4) = ~72 bytes
     *   Food:     16 header + nutrition(4) + expired(1) + position ref(8) + Position(24) = ~56 bytes
     *   PreCreature: 16 header + daysTillBorn(4) + parent ref(8) = ~32 bytes
     */
    public static final int CREATURE_SIZE_BYTES     = 72;
    public static final int FOOD_SIZE_BYTES         = 56;
    public static final int PRECREATURE_SIZE_BYTES  = 32;

    public static SimulationSnapshot capture(
            int tick,
            ArrayList<Creature> creatures,
            ArrayList<entities.Food> foods,
            int preCreatureCount,
            int deathsThisTick,
            int preCreatureDeathsThisTick,
            int birthsThisTick,
            int foodEatenThisTick,
            int foodExpiredThisTick,
            int foodSpawnedThisTick,
            long timeUpdateCreaturesNs,
            long timeUpdateFoodNs,
            long timeUpdateMapNs,
            long timeUpdatePreCreaturesNs
    ) {
        List<Float> energies   = creatures.stream().map(Creature::getEnergy).toList();
        List<Float> nutritions = foods.stream().map(Food::getNutrition).toList();

        Runtime rt = Runtime.getRuntime();
        long heapTotal = rt.totalMemory();
        long heapFree  = rt.freeMemory();
        long heapUsed  = heapTotal - heapFree;

        long creaturesMem    = (long) creatures.size()    * CREATURE_SIZE_BYTES;
        long foodsMem        = (long) foods.size()        * FOOD_SIZE_BYTES;
        long preCreaturesMem = (long) preCreatureCount    * PRECREATURE_SIZE_BYTES;

        return new SimulationSnapshot(
                tick,
                creatures.size(),
                foods.size(),
                preCreatureCount,
                deathsThisTick,
                preCreatureDeathsThisTick,
                birthsThisTick,
                foodEatenThisTick,
                foodExpiredThisTick,
                foodSpawnedThisTick,
                energies,
                nutritions,
                timeUpdateCreaturesNs,
                timeUpdateFoodNs,
                timeUpdateMapNs,
                timeUpdatePreCreaturesNs,
                heapUsed,
                heapFree,
                heapTotal,
                creaturesMem,
                foodsMem,
                preCreaturesMem
        );
    }
}
package managers;

import core.App;
import entities.population.Food;
import entities.population.Creature;
import stats.SimulationSnapshot;
import stats.SimulationStats;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Manager centralizzato per la raccolta e l'analisi delle statistiche di simulazione.
 *
 * UTILIZZO:
 *  - Chiamare startSimulation() in SimulationManager.init() PRIMA di creare il mondo
 *  - In World.update(), wrappare le 4 funzioni principali con startTimer()/stopTimer():
 *      StatsManager.startTimer();
 *      updateCreatures();
 *      long tCreatures = StatsManager.stopTimer();
 *      ... (ripetere per updateFood, updateMap, updatePreCreatures)
 *  - Chiamare recordTick() al termine di ogni tick
 *  - notifyCreatureDied(), notifyPreCreatureDied(), notifyCreatureBorn(),
 *    notifyFoodEaten(), notifyFoodExpired(), notifyFoodSpawned()
 *    dalle rispettive entità/metodi
 *  - Chiamare printFinalReport() in SimulationManager.end()
 */
public class StatsManager {

    // ── Snapshots ──
    private static final List<SimulationSnapshot> snapshots = new ArrayList<>();

    // ── Contatori tick corrente ──
    private static int  currentTickDeaths          = 0;
    private static int  currentTickPreDeaths       = 0;
    private static int  currentTickBirths          = 0;
    private static int  currentTickEaten           = 0;
    private static int  currentTickExpired         = 0;
    private static int  currentTickFoodSpawned     = 0;

    // ── Timer ──
    private static long timerStart = 0L;

    // ── Timestamp simulazione ──
    private static long simulationStartMs = 0L;

    // =========================================================================
    // LIFECYCLE
    // =========================================================================

    public static void startSimulation() {
        snapshots.clear();
        resetTickCounters();
        simulationStartMs = System.currentTimeMillis();
    }

    private static void resetTickCounters() {
        currentTickDeaths      = 0;
        currentTickPreDeaths   = 0;
        currentTickBirths      = 0;
        currentTickEaten       = 0;
        currentTickExpired     = 0;
        currentTickFoodSpawned = 0;
    }

    // =========================================================================
    // TIMER — da usare in World.update() intorno alle 4 funzioni principali
    // =========================================================================

    /** Avvia il timer. Chiamare immediatamente prima della funzione da misurare. */
    public static void startTimer() {
        timerStart = System.nanoTime();
    }

    /**
     * Ferma il timer e restituisce il tempo trascorso in nanosecondi.
     * Chiamare immediatamente dopo la funzione misurata.
     */
    public static long stopTimer() {
        return System.nanoTime() - timerStart;
    }

    // =========================================================================
    // NOTIFICHE EVENTI
    // =========================================================================

    public static void notifyCreatureDied()      { currentTickDeaths++;      }
    public static void notifyPreCreatureDied()   { currentTickPreDeaths++;   }
    public static void notifyCreatureBorn()      { currentTickBirths++;      }
    public static void notifyFoodEaten()         { currentTickEaten++;       }
    public static void notifyFoodExpired()       { currentTickExpired++;     }
    public static void notifyFoodSpawned()       { currentTickFoodSpawned++; }

    // =========================================================================
    // REGISTRAZIONE TICK
    // =========================================================================

    /**
     * Da chiamare ALLA FINE di ogni tick in World.update(), dopo updateMap().
     *
     * @param tick                 indice del tick corrente
     * @param creatures            lista corrente delle creature
     * @param foods                lista corrente dei cibi
     * @param preCreatureCount     dimensione della coda preCreatures
     * @param timeUpdateCreaturesNs  ns misurati da stopTimer() per updateCreatures
     * @param timeUpdateFoodNs       ns misurati da stopTimer() per updateFood
     * @param timeUpdateMapNs        ns misurati da stopTimer() per updateMap
     * @param timeUpdatePreCreaturesNs ns misurati da stopTimer() per updatePreCreatures
     */
    public static void recordTick(
            int tick,
            ArrayList<Creature> creatures,
            ArrayList<Food> foods,
            int preCreatureCount,
            long timeUpdateCreaturesNs,
            long timeUpdateFoodNs,
            long timeUpdateMapNs,
            long timeUpdatePreCreaturesNs
    ) {
        SimulationSnapshot snapshot = SimulationSnapshot.capture(
                tick,
                creatures,
                foods,
                preCreatureCount,
                currentTickDeaths,
                currentTickPreDeaths,
                currentTickBirths,
                currentTickEaten,
                currentTickExpired,
                currentTickFoodSpawned,
                timeUpdateCreaturesNs,
                timeUpdateFoodNs,
                timeUpdateMapNs,
                timeUpdatePreCreaturesNs
        );
        snapshots.add(snapshot);
        resetTickCounters();
    }

    // =========================================================================
    // STAMPA IN TEMPO REALE
    // =========================================================================

    public static void printAveragePopulationEnergy(final ArrayList<Creature> creatures) {
        if (creatures.isEmpty()) return;
        double av = 0; float min = Float.MAX_VALUE, max = Float.MIN_VALUE;
        for (Creature c : creatures) {
            av += c.getEnergy();
            if (c.getEnergy() > max) max = c.getEnergy();
            if (c.getEnergy() < min) min = c.getEnergy();
        }
        System.out.printf("Total Creatures: %d%n", creatures.size());
        System.out.printf("Creatures Average Energy: %.2f%n", av / creatures.size());
        System.out.printf("Creatures Max Energy: %.2f%n", max);
        System.out.printf("Creatures Min Energy: %.2f%n", min);
    }

    public static void printAverageFoodNutrition(final ArrayList<Food> foods) {
        if (foods.isEmpty()) return;
        double av = 0; float min = Float.MAX_VALUE, max = Float.MIN_VALUE;
        for (Food f : foods) {
            av += f.getNutrition();
            if (f.getNutrition() > max) max = f.getNutrition();
            if (f.getNutrition() < min) min = f.getNutrition();
        }
        System.out.printf("Total Foods: %d%n", foods.size());
        System.out.printf("Foods Average Nutrition: %.2f%n", av / foods.size());
        System.out.printf("Foods Max Nutrition: %.2f%n", max);
        System.out.printf("Foods Min Nutrition: %.2f%n", min);
    }

    public static void printFoodAlert(final Creature creature, final Food food) {
        System.out.println("FOOD HAS BEEN EATEN");
        System.out.println("-------------------");
        System.out.println(creature);
        System.out.println("-------------------");
        System.out.println(food);
        System.out.println("-------------------");
    }

    // =========================================================================
    // REPORT FINALE
    // =========================================================================

    public static void printFinalReport() {
        if (snapshots.isEmpty()) {
            System.out.println("[StatsManager] Nessun dato registrato.");
            return;
        }
        long totalDurationMs = System.currentTimeMillis() - simulationStartMs;
        SimulationStats stats = new SimulationStats(snapshots, totalDurationMs);
        printReportToConsole(stats);
        saveReportToJson(stats);
    }

    // =========================================================================
    // STAMPA CONSOLE
    // =========================================================================

    private static void printReportToConsole(SimulationStats s) {
        String sep = "=".repeat(58);
        String sub = "-".repeat(58);
        System.out.println("\n" + sep);
        System.out.println("           SIMULATION FINAL REPORT");
        System.out.println(sep);

        System.out.println("\n[ CONFIGURATION ]");
        System.out.println(sub);
        System.out.printf("  World Size             : %d x %d%n", App.getSimManager().getSettings().getWidth(), App.getSimManager().getSettings().getHeight());
        System.out.printf("  Base Population        : %d%n", App.getSimManager().getSettings().getBasePopulation());
        System.out.printf("  Base Food              : %d%n", App.getSimManager().getSettings().getBaseFood());
        System.out.printf("  Food Per Tick          : %.2f%n", App.getSimManager().getSettings().getFoodPerTick());
        System.out.printf("  Tick Duration (ms)     : %.2f%n", App.getSimManager().getSettings().getTickDuration());
        System.out.printf("  Creature Base Energy   : %.1f%n", App.getSimManager().getSettings().getBaseEnergy());
        System.out.printf("  Creature Base Hunger   : %.1f%n", App.getSimManager().getSettings().getBaseHunger());
        System.out.printf("  Energy Loss/Tick       : %.1f%n", App.getSimManager().getSettings().getEnergyLossPerTick());
        System.out.printf("  Energy Loss/Move       : %.1f%n", App.getSimManager().getSettings().getEnergyLossPerMove());
        System.out.printf("  Reproduction Threshold : %.1f%n", App.getSimManager().getSettings().getReproductionThreshold());
        System.out.printf("  Reproduction Cost      : %.1f%n", App.getSimManager().getSettings().getReproductionCost());
        System.out.printf("  Pregnancy Ticks        : %.2f%n",   App.getSimManager().getSettings().getPregnancyTicks());
        System.out.printf("  Food Base Nutrition    : %.1f%n", App.getSimManager().getSettings().getBaseNutrition());
        System.out.printf("  Food Decay/Tick        : %.1f%n", App.getSimManager().getSettings().getDecaymentPerTick());

        System.out.println("\n[ DURATION ]");
        System.out.println(sub);
        System.out.printf("  Total Ticks            : %d%n",       s.totalTicks);
        System.out.printf("  Total Duration         : %s%n",       formatDuration(s.totalDurationMs));
        System.out.printf("  Avg Tick Duration      : %.2f ms%n",  s.avgTickDurationMs);

        System.out.println("\n[ POPULATION & DEMOGRAPHY ]");
        System.out.println(sub);
        System.out.printf("  Peak Population        : %d  (tick %d)%n", s.peakPopulation, s.peakPopulationTick);
        System.out.printf("  Min Population         : %d  (tick %d)%n", s.minPopulation, s.minPopulationTick);
        System.out.printf("  Avg Population         : %.2f%n",           s.avgPopulation);
        System.out.printf("  Total Births           : %d%n",             s.totalBirths);
        System.out.printf("  Avg Births/Tick        : %.2f%n",           s.avgBirthsPerTick);
        System.out.printf("  Total Deaths (energy)  : %d%n",             s.totalDeaths);
        System.out.printf("  Total Deaths (space)   : %d%n",             s.totalPreCreatureDeaths);
        System.out.printf("  Total Deaths (all)     : %d%n",             s.totalDeathsAll);
        System.out.printf("  Avg Deaths/Tick        : %.2f%n",           s.avgDeathsPerTick);
        System.out.printf("  Mortality Rate         : %.1f%%%n",         s.mortalityRate);
        System.out.printf("  Birth/Death Ratio      : %.3f%n",           s.birthDeathRatio);
        System.out.printf("  Peak Growth Tick       : %d%n",             s.peakGrowthTick);
        System.out.printf("  Peak Decline Tick      : %d%n",             s.peakDeclineTick);
        System.out.printf("  Peak PreCreatures      : %d  (tick %d)%n",  s.peakPreCreatures, s.peakPreCreaturesTick);

        System.out.println("\n[ CREATURE ENERGY ]");
        System.out.println(sub);
        System.out.printf("  Average / Median       : %.2f / %.2f%n", s.avgCreatureEnergy, s.medianCreatureEnergy);
        System.out.printf("  Std Dev                : %.2f%n",          s.stdDevCreatureEnergy);
        System.out.printf("  Min / Max              : %.1f / %.1f%n",   s.minCreatureEnergy, s.maxCreatureEnergy);
        System.out.printf("  P25 / P75 / P90        : %.1f / %.1f / %.1f%n", s.p25CreatureEnergy, s.p75CreatureEnergy, s.p90CreatureEnergy);

        System.out.println("\n[ FOOD ]");
        System.out.println(sub);
        System.out.printf("  Peak Food on Map       : %d  (tick %d)%n", s.peakFood, s.peakFoodTick);
        System.out.printf("  Avg Food/Tick          : %.2f%n",           s.avgFoodPerTick);
        System.out.printf("  Total Spawned          : %d%n",             s.totalFoodSpawned);
        System.out.printf("  Avg Spawned/Tick       : %.2f%n",           s.avgFoodSpawnedPerTick);
        System.out.printf("  Total Eaten            : %d%n",             s.totalFoodEaten);
        System.out.printf("  Total Expired          : %d%n",             s.totalFoodExpired);
        System.out.printf("  Consumption Rate       : %.1f%%%n",         s.foodConsumptionRate);

        System.out.println("\n[ FOOD NUTRITION ]");
        System.out.println(sub);
        System.out.printf("  Average / Median       : %.2f / %.2f%n", s.avgFoodNutrition, s.medianFoodNutrition);
        System.out.printf("  Std Dev                : %.2f%n",          s.stdDevFoodNutrition);
        System.out.printf("  Min / Max              : %.1f / %.1f%n",   s.minFoodNutrition, s.maxFoodNutrition);
        System.out.printf("  P25 / P75              : %.1f / %.1f%n",   s.p25FoodNutrition, s.p75FoodNutrition);

        System.out.println("\n[ PERFORMANCE — TIMING ]");
        System.out.println(sub);
        System.out.printf("  %-22s  avg=%6.3f  med=%6.3f  p95=%6.3f  max=%6.3f ms%n",
                "updateCreatures()", s.avgUpdateCreaturesMs, s.medianUpdateCreaturesMs, s.p95UpdateCreaturesMs, s.maxUpdateCreaturesMs);
        System.out.printf("  %-22s  avg=%6.3f  med=%6.3f  p95=%6.3f  max=%6.3f ms%n",
                "updateFood()", s.avgUpdateFoodMs, s.medianUpdateFoodMs, s.p95UpdateFoodMs, s.maxUpdateFoodMs);
        System.out.printf("  %-22s  avg=%6.3f  med=%6.3f  p95=%6.3f  max=%6.3f ms%n",
                "updateMap()", s.avgUpdateMapMs, s.medianUpdateMapMs, s.p95UpdateMapMs, s.maxUpdateMapMs);
        System.out.printf("  %-22s  avg=%6.3f  med=%6.3f  p95=%6.3f  max=%6.3f ms%n",
                "updatePreCreatures()", s.avgUpdatePreCreaturesMs, s.medianUpdatePreCreaturesMs, s.p95UpdatePreCreaturesMs, s.maxUpdatePreCreaturesMs);
        System.out.printf("  %-22s  avg=%6.3f  p95=%6.3f  max=%6.3f ms%n",
                "TOTAL TICK", s.avgTotalTickMs, s.p95TotalTickMs, s.maxTotalTickMs);

        System.out.println("\n[ PERFORMANCE — MEMORY ]");
        System.out.println(sub);
        System.out.printf("  Heap Used  avg/peak    : %.1f MB / %.1f MB%n", s.avgHeapUsedMb, s.peakHeapUsedMb);
        System.out.printf("  Heap Used  min/stddev  : %.1f MB / %.1f MB%n", s.minHeapUsedMb, s.stdDevHeapUsedMb);
        System.out.printf("  Heap Used  final       : %.1f MB%n",           s.finalHeapUsedMb);
        System.out.printf("  Creatures mem  peak    : %.3f MB%n",            s.peakCreaturesMemMb);
        System.out.printf("  Foods mem      peak    : %.3f MB%n",            s.peakFoodsMemMb);
        System.out.printf("  PreCreatures mem peak  : %.3f MB%n",            s.peakPreCreaturesMemMb);

        System.out.println("\n" + sep + "\n");
    }

    // =========================================================================
    // EXPORT JSON
    // =========================================================================

    private static void saveReportToJson(SimulationStats s) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename  = "simulation_report_" + timestamp + ".json";
        try (FileWriter fw = new FileWriter(filename)) {
            fw.write(buildJson(s));
            System.out.println("[StatsManager] Report salvato in: " + filename);
        } catch (IOException e) {
            System.err.println("[StatsManager] Errore nel salvataggio del report: " + e.getMessage());
        }
    }

    private static String buildJson(SimulationStats s) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");

        // Config
        sb.append("  \"config\": {\n");
        sb.append(jf("world_width",                      App.getSimManager().getSettings().getWidth()));
        sb.append(jf("world_height",                     App.getSimManager().getSettings().getHeight()));
        sb.append(jf("base_population",                  App.getSimManager().getSettings().getBasePopulation()));
        sb.append(jf("base_food",                        App.getSimManager().getSettings().getBaseFood()));
        sb.append(jf("food_per_tick",                    App.getSimManager().getSettings().getFoodPerTick()));
        sb.append(jf("tick_duration_ms",                 App.getSimManager().getSettings().getTickDuration()));
        sb.append(jf("creature_base_energy",             App.getSimManager().getSettings().getBaseEnergy()));
        sb.append(jf("creature_base_hunger",             App.getSimManager().getSettings().getBaseHunger()));
        sb.append(jf("creature_energy_loss_per_tick",    App.getSimManager().getSettings().getEnergyLossPerTick()));
        sb.append(jf("creature_energy_loss_per_move",    App.getSimManager().getSettings().getEnergyLossPerMove()));
        sb.append(jf("creature_reproduction_threshold",  App.getSimManager().getSettings().getReproductionThreshold()));
        sb.append(jf("creature_reproduction_cost",       App.getSimManager().getSettings().getReproductionCost()));
        sb.append(jf("creature_pregnancy_ticks",         App.getSimManager().getSettings().getPregnancyTicks()));
        sb.append(jf("food_base_nutrition",              App.getSimManager().getSettings().getBaseNutrition()));
        sb.append(jfl("food_decay_per_tick",             App.getSimManager().getSettings().getDecaymentPerTick()));
        sb.append("  },\n");

        // Durata
        sb.append("  \"duration\": {\n");
        sb.append(jf("total_ticks",           s.totalTicks));
        sb.append(jf("total_duration_ms",     s.totalDurationMs));
        sb.append(jfl("avg_tick_duration_ms", s.avgTickDurationMs));
        sb.append("  },\n");

        // Popolazione e demografia
        sb.append("  \"population\": {\n");
        sb.append(jf("peak_population",            s.peakPopulation));
        sb.append(jf("peak_population_tick",        s.peakPopulationTick));
        sb.append(jf("min_population",              s.minPopulation));
        sb.append(jf("min_population_tick",         s.minPopulationTick));
        sb.append(jf("avg_population",              s.avgPopulation));
        sb.append(jf("total_births",                s.totalBirths));
        sb.append(jf("avg_births_per_tick",         s.avgBirthsPerTick));
        sb.append(jf("total_deaths_energy",         s.totalDeaths));
        sb.append(jf("total_deaths_space",          s.totalPreCreatureDeaths));
        sb.append(jf("total_deaths_all",            s.totalDeathsAll));
        sb.append(jf("avg_deaths_per_tick",         s.avgDeathsPerTick));
        sb.append(jf("mortality_rate_pct",          s.mortalityRate));
        sb.append(jf("birth_death_ratio",           s.birthDeathRatio));
        sb.append(jf("peak_growth_tick",            s.peakGrowthTick));
        sb.append(jf("peak_decline_tick",           s.peakDeclineTick));
        sb.append(jf("peak_precreatures",           s.peakPreCreatures));
        sb.append(jfl("peak_precreatures_tick",     s.peakPreCreaturesTick));
        sb.append("  },\n");

        // Energia
        sb.append("  \"creature_energy\": {\n");
        sb.append(jf("average",   s.avgCreatureEnergy));
        sb.append(jf("median",    s.medianCreatureEnergy));
        sb.append(jf("std_dev",   s.stdDevCreatureEnergy));
        sb.append(jf("min",       s.minCreatureEnergy));
        sb.append(jf("max",       s.maxCreatureEnergy));
        sb.append(jf("p25",       s.p25CreatureEnergy));
        sb.append(jf("p75",       s.p75CreatureEnergy));
        sb.append(jfl("p90",      s.p90CreatureEnergy));
        sb.append("  },\n");

        // Cibo
        sb.append("  \"food\": {\n");
        sb.append(jf("peak_food",              s.peakFood));
        sb.append(jf("peak_food_tick",         s.peakFoodTick));
        sb.append(jf("avg_food_per_tick",      s.avgFoodPerTick));
        sb.append(jf("total_spawned",          s.totalFoodSpawned));
        sb.append(jf("avg_spawned_per_tick",   s.avgFoodSpawnedPerTick));
        sb.append(jf("total_eaten",            s.totalFoodEaten));
        sb.append(jf("total_expired",          s.totalFoodExpired));
        sb.append(jfl("consumption_rate_pct",  s.foodConsumptionRate));
        sb.append("  },\n");

        // Nutrizione
        sb.append("  \"food_nutrition\": {\n");
        sb.append(jf("average",  s.avgFoodNutrition));
        sb.append(jf("median",   s.medianFoodNutrition));
        sb.append(jf("std_dev",  s.stdDevFoodNutrition));
        sb.append(jf("min",      s.minFoodNutrition));
        sb.append(jf("max",      s.maxFoodNutrition));
        sb.append(jf("p25",      s.p25FoodNutrition));
        sb.append(jfl("p75",     s.p75FoodNutrition));
        sb.append("  },\n");

        // Performance timing
        sb.append("  \"performance_timing\": {\n");
        sb.append(jfObj("update_creatures", s.avgUpdateCreaturesMs, s.medianUpdateCreaturesMs,
                s.minUpdateCreaturesMs, s.maxUpdateCreaturesMs, s.stdDevUpdateCreaturesMs, s.p95UpdateCreaturesMs));
        sb.append(jfObj("update_food", s.avgUpdateFoodMs, s.medianUpdateFoodMs,
                s.minUpdateFoodMs, s.maxUpdateFoodMs, s.stdDevUpdateFoodMs, s.p95UpdateFoodMs));
        sb.append(jfObj("update_map", s.avgUpdateMapMs, s.medianUpdateMapMs,
                s.minUpdateMapMs, s.maxUpdateMapMs, s.stdDevUpdateMapMs, s.p95UpdateMapMs));
        sb.append(jfObj("update_precreatures", s.avgUpdatePreCreaturesMs, s.medianUpdatePreCreaturesMs,
                s.minUpdatePreCreaturesMs, s.maxUpdatePreCreaturesMs, s.stdDevUpdatePreCreaturesMs, s.p95UpdatePreCreaturesMs));
        sb.append(String.format("    \"total_tick\": {\"avg\": %.4f, \"p95\": %.4f, \"max\": %.4f}\n",
                s.avgTotalTickMs, s.p95TotalTickMs, s.maxTotalTickMs));
        sb.append("  },\n");

        // Performance memoria
        sb.append("  \"performance_memory\": {\n");
        sb.append(jf("heap_avg_mb",             s.avgHeapUsedMb));
        sb.append(jf("heap_peak_mb",            s.peakHeapUsedMb));
        sb.append(jf("heap_min_mb",             s.minHeapUsedMb));
        sb.append(jf("heap_std_dev_mb",         s.stdDevHeapUsedMb));
        sb.append(jf("heap_final_mb",           s.finalHeapUsedMb));
        sb.append(jf("creatures_mem_peak_mb",   s.peakCreaturesMemMb));
        sb.append(jf("foods_mem_peak_mb",       s.peakFoodsMemMb));
        sb.append(jf("precreatures_mem_peak_mb",s.peakPreCreaturesMemMb));
        sb.append(jf("creatures_mem_avg_mb",    s.avgCreaturesMemMb));
        sb.append(jfl("foods_mem_avg_mb",       s.avgFoodsMemMb));
        sb.append("  },\n");

        // Serie temporali
        sb.append("  \"time_series\": {\n");
        sb.append(jfArray("population",              s.populationOverTime));
        sb.append(jfArray("precreature_count",       s.preCreatureCountOverTime));
        sb.append(jfArray("food_count",              s.foodCountOverTime));
        sb.append(jfArray("avg_energy",              s.avgEnergyOverTime));
        sb.append(jfArray("avg_nutrition",           s.avgNutritionOverTime));
        sb.append(jfArray("deaths",                  s.deathsOverTime));
        sb.append(jfArray("precreature_deaths",      s.preCreatureDeathsOverTime));
        sb.append(jfArray("births",                  s.birthsOverTime));
        sb.append(jfArray("growth_rate",             s.growthRateOverTime));
        sb.append(jfArray("food_eaten",              s.foodEatenOverTime));
        sb.append(jfArray("food_expired",            s.foodExpiredOverTime));
        sb.append(jfArray("food_spawned",            s.foodSpawnedOverTime));
        sb.append(jfArray("time_creatures_ms",       s.updateCreaturesMsOverTime));
        sb.append(jfArray("time_food_ms",            s.updateFoodMsOverTime));
        sb.append(jfArray("time_map_ms",             s.updateMapMsOverTime));
        sb.append(jfArray("time_precreatures_ms",    s.updatePreCreaturesMsOverTime));
        sb.append(jfArray("total_tick_ms",           s.totalTickMsOverTime));
        sb.append(jfArray("heap_used_mb",            s.heapUsedMbOverTime));
        sb.append(jfArray("creatures_mem_mb",        s.creaturesMemMbOverTime));
        sb.append(jfArray("foods_mem_mb",            s.foodsMemMbOverTime));
        sb.append(jfArrayLast("precreatures_mem_mb", s.preCreaturesMemMbOverTime));
        sb.append("  }\n");

        sb.append("}\n");
        return sb.toString();
    }

    // =========================================================================
    // JSON HELPERS
    // =========================================================================

    private static String jf(String key, Object value) {
        if (value instanceof Double d)  return String.format("    \"%s\": %.4f,\n", key, d);
        if (value instanceof Float f)   return String.format("    \"%s\": %.4f,\n", key, f);
        return String.format("    \"%s\": %s,\n", key, value);
    }

    private static String jfl(String key, Object value) {
        if (value instanceof Double d)  return String.format("    \"%s\": %.4f\n", key, d);
        if (value instanceof Float f)   return String.format("    \"%s\": %.4f\n", key, f);
        return String.format("    \"%s\": %s\n", key, value);
    }

    private static String jfObj(String name, double avg, double med, double min, double max, double std, double p95) {
        return String.format(
                "    \"%s\": {\"avg\": %.4f, \"median\": %.4f, \"min\": %.4f, \"max\": %.4f, \"std_dev\": %.4f, \"p95\": %.4f},\n",
                name, avg, med, min, max, std, p95);
    }

    private static <T> String jfArray(String key, List<T> list) {
        return String.format("    \"%s\": [%s],\n", key, listToJsonArray(list));
    }

    private static <T> String jfArrayLast(String key, List<T> list) {
        return String.format("    \"%s\": [%s]\n", key, listToJsonArray(list));
    }

    private static <T> String listToJsonArray(List<T> list) {
        if (list.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            T val = list.get(i);
            if (val instanceof Double d)      sb.append(String.format("%.4f", d));
            else if (val instanceof Float f)  sb.append(String.format("%.4f", f));
            else                              sb.append(val);
            if (i < list.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }

    // =========================================================================
    // UTILITY
    // =========================================================================

    private static String formatDuration(long ms) {
        long s = ms / 1000, m = s / 60, h = m / 60;
        return String.format("%02d:%02d:%02d.%03d", h, m % 60, s % 60, ms % 1000);
    }


    public static int getTickCount() {
        return snapshots.size();
    }

    public static SimulationSnapshot getLatestSnapshot() {
        if (snapshots.isEmpty()) return null;
        return snapshots.getLast();
    }

    public static List<SimulationSnapshot> getSnapshotsCopy() {
        return new ArrayList<>(snapshots);
    }

    public static long getCurrentDurationMs() {
        if (simulationStartMs == 0) return 0;
        return System.currentTimeMillis() - simulationStartMs;
    }
}
package stats;

import java.util.ArrayList;
import java.util.List;

/**
 * Calcola e aggrega tutte le statistiche finali a partire dalla lista di snapshot tick per tick.
 */
public class SimulationStats {

    // ══════════════════════════════════════════════════════════════════════════
    // DURATA
    // ══════════════════════════════════════════════════════════════════════════
    public final int    totalTicks;
    public final long   totalDurationMs;
    public final double avgTickDurationMs;

    // ══════════════════════════════════════════════════════════════════════════
    // POPOLAZIONE — base
    // ══════════════════════════════════════════════════════════════════════════
    public final int    peakPopulation;
    public final int    peakPopulationTick;
    public final int    minPopulation;
    public final int    minPopulationTick;
    public final double avgPopulation;

    // ══════════════════════════════════════════════════════════════════════════
    // DEMOGRAFIA ESTESA
    // ══════════════════════════════════════════════════════════════════════════
    public final int    totalDeaths;              // morti per energia esaurita
    public final int    totalPreCreatureDeaths;   // PreCreature morte per mancanza spazio
    public final int    totalDeathsAll;           // somma di entrambe
    public final int    totalBirths;              // nascite avvenute con successo
    public final double avgDeathsPerTick;
    public final double avgBirthsPerTick;
    public final double mortalityRate;            // % (totalDeaths / peakPop * 100)
    public final double birthDeathRatio;          // totalBirths / totalDeathsAll (>1 = crescita)
    public final int    peakGrowthTick;           // tick con massimo (births - deaths)
    public final int    peakDeclineTick;          // tick con massimo (deaths - births)
    public final int    peakPreCreatures;
    public final int    peakPreCreaturesTick;

    // ══════════════════════════════════════════════════════════════════════════
    // ENERGIA CREATURE (aggregata su tutti i tick)
    // ══════════════════════════════════════════════════════════════════════════
    public final double avgCreatureEnergy;
    public final double medianCreatureEnergy;
    public final float  minCreatureEnergy;
    public final float  maxCreatureEnergy;
    public final double stdDevCreatureEnergy;
    public final double p25CreatureEnergy;
    public final double p75CreatureEnergy;
    public final double p90CreatureEnergy;

    // ══════════════════════════════════════════════════════════════════════════
    // CIBO
    // ══════════════════════════════════════════════════════════════════════════
    public final int    totalFoodEaten;
    public final int    totalFoodExpired;
    public final int    totalFoodSpawned;         // cibo generato dinamicamente
    public final double foodConsumptionRate;      // % mangiato / (mangiato + scaduto)
    public final double avgFoodPerTick;
    public final double avgFoodSpawnedPerTick;
    public final int    peakFood;
    public final int    peakFoodTick;

    // ══════════════════════════════════════════════════════════════════════════
    // NUTRIZIONE CIBO (aggregata su tutti i tick)
    // ══════════════════════════════════════════════════════════════════════════
    public final double avgFoodNutrition;
    public final double medianFoodNutrition;
    public final float  minFoodNutrition;
    public final float  maxFoodNutrition;
    public final double stdDevFoodNutrition;
    public final double p25FoodNutrition;
    public final double p75FoodNutrition;

    // ══════════════════════════════════════════════════════════════════════════
    // PERFORMANCE — tempi (in ms)
    // ══════════════════════════════════════════════════════════════════════════
    public final double avgUpdateCreaturesMs;
    public final double medianUpdateCreaturesMs;
    public final double minUpdateCreaturesMs;
    public final double maxUpdateCreaturesMs;
    public final double stdDevUpdateCreaturesMs;
    public final double p95UpdateCreaturesMs;

    public final double avgUpdateFoodMs;
    public final double medianUpdateFoodMs;
    public final double minUpdateFoodMs;
    public final double maxUpdateFoodMs;
    public final double stdDevUpdateFoodMs;
    public final double p95UpdateFoodMs;

    public final double avgUpdateMapMs;
    public final double medianUpdateMapMs;
    public final double minUpdateMapMs;
    public final double maxUpdateMapMs;
    public final double stdDevUpdateMapMs;
    public final double p95UpdateMapMs;

    public final double avgUpdatePreCreaturesMs;
    public final double medianUpdatePreCreaturesMs;
    public final double minUpdatePreCreaturesMs;
    public final double maxUpdatePreCreaturesMs;
    public final double stdDevUpdatePreCreaturesMs;
    public final double p95UpdatePreCreaturesMs;

    public final double avgTotalTickMs;       // somma delle 4 funzioni per tick
    public final double maxTotalTickMs;
    public final double p95TotalTickMs;

    // ══════════════════════════════════════════════════════════════════════════
    // PERFORMANCE — memoria heap (in MB)
    // ══════════════════════════════════════════════════════════════════════════
    public final double peakHeapUsedMb;
    public final double avgHeapUsedMb;
    public final double minHeapUsedMb;
    public final double stdDevHeapUsedMb;
    public final double finalHeapUsedMb;

    // ══════════════════════════════════════════════════════════════════════════
    // PERFORMANCE — memoria strutture dati stimate (in MB)
    // ══════════════════════════════════════════════════════════════════════════
    public final double peakCreaturesMemMb;
    public final double peakFoodsMemMb;
    public final double peakPreCreaturesMemMb;
    public final double avgCreaturesMemMb;
    public final double avgFoodsMemMb;

    // ══════════════════════════════════════════════════════════════════════════
    // SERIE TEMPORALI — simulazione
    // ══════════════════════════════════════════════════════════════════════════
    public final List<Integer> populationOverTime;
    public final List<Integer> preCreatureCountOverTime;
    public final List<Integer> foodCountOverTime;
    public final List<Double>  avgEnergyOverTime;
    public final List<Double>  avgNutritionOverTime;
    public final List<Integer> deathsOverTime;
    public final List<Integer> preCreatureDeathsOverTime;
    public final List<Integer> birthsOverTime;
    public final List<Integer> foodEatenOverTime;
    public final List<Integer> foodExpiredOverTime;
    public final List<Integer> foodSpawnedOverTime;
    public final List<Double>  growthRateOverTime;  // (births - deaths) per tick

    // ══════════════════════════════════════════════════════════════════════════
    // SERIE TEMPORALI — performance
    // ══════════════════════════════════════════════════════════════════════════
    public final List<Double>  updateCreaturesMsOverTime;
    public final List<Double>  updateFoodMsOverTime;
    public final List<Double>  updateMapMsOverTime;
    public final List<Double>  updatePreCreaturesMsOverTime;
    public final List<Double>  totalTickMsOverTime;
    public final List<Double>  heapUsedMbOverTime;
    public final List<Double>  creaturesMemMbOverTime;
    public final List<Double>  foodsMemMbOverTime;
    public final List<Double>  preCreaturesMemMbOverTime;

    // ══════════════════════════════════════════════════════════════════════════
    // COSTRUTTORE
    // ══════════════════════════════════════════════════════════════════════════

    public SimulationStats(List<SimulationSnapshot> snapshots, long totalDurationMs) {
        this.totalTicks        = snapshots.size();
        this.totalDurationMs   = totalDurationMs;
        this.avgTickDurationMs = totalTicks > 0 ? (double) totalDurationMs / totalTicks : 0;

        // --- Inizializzazione serie temporali ---
        populationOverTime          = new ArrayList<>();
        preCreatureCountOverTime    = new ArrayList<>();
        foodCountOverTime           = new ArrayList<>();
        avgEnergyOverTime           = new ArrayList<>();
        avgNutritionOverTime        = new ArrayList<>();
        deathsOverTime              = new ArrayList<>();
        preCreatureDeathsOverTime   = new ArrayList<>();
        birthsOverTime              = new ArrayList<>();
        foodEatenOverTime           = new ArrayList<>();
        foodExpiredOverTime         = new ArrayList<>();
        foodSpawnedOverTime         = new ArrayList<>();
        growthRateOverTime          = new ArrayList<>();
        updateCreaturesMsOverTime   = new ArrayList<>();
        updateFoodMsOverTime        = new ArrayList<>();
        updateMapMsOverTime         = new ArrayList<>();
        updatePreCreaturesMsOverTime= new ArrayList<>();
        totalTickMsOverTime         = new ArrayList<>();
        heapUsedMbOverTime          = new ArrayList<>();
        creaturesMemMbOverTime      = new ArrayList<>();
        foodsMemMbOverTime          = new ArrayList<>();
        preCreaturesMemMbOverTime   = new ArrayList<>();

        // --- Accumulatori per statistiche aggregate ---
        List<Float>  allEnergies        = new ArrayList<>();
        List<Float>  allNutritions      = new ArrayList<>();
        List<Long>   timesCreatures     = new ArrayList<>();
        List<Long>   timesFood          = new ArrayList<>();
        List<Long>   timesMap           = new ArrayList<>();
        List<Long>   timesPreCreatures  = new ArrayList<>();
        List<Long>   totalTickTimes     = new ArrayList<>();
        List<Long>   heapUsedValues     = new ArrayList<>();
        List<Long>   creaturesMemValues = new ArrayList<>();
        List<Long>   foodsMemValues     = new ArrayList<>();

        int _peakPop = 0, _peakPopTick = 0;
        int _minPop  = Integer.MAX_VALUE, _minPopTick = 0;
        int _peakFood = 0, _peakFoodTick = 0;
        int _peakPreCreatures = 0, _peakPreCreaturesTick = 0;
        int _totalDeaths = 0, _totalPreDeaths = 0, _totalBirths = 0;
        int _totalEaten = 0, _totalExpired = 0, _totalSpawned = 0;
        int _peakGrowthTick = 0, _peakDeclineTick = 0;
        double _peakGrowth = Double.MIN_VALUE, _peakDecline = Double.MAX_VALUE;

        for (SimulationSnapshot s : snapshots) {
            // --- Popolazione ---
            populationOverTime.add(s.populationCount());
            preCreatureCountOverTime.add(s.preCreatureCount());
            foodCountOverTime.add(s.foodCount());
            deathsOverTime.add(s.deathsThisTick());
            preCreatureDeathsOverTime.add(s.preCreatureDeathsThisTick());
            birthsOverTime.add(s.birthsThisTick());
            foodEatenOverTime.add(s.foodEatenThisTick());
            foodExpiredOverTime.add(s.foodExpiredThisTick());
            foodSpawnedOverTime.add(s.foodSpawnedThisTick());

            double growth = s.birthsThisTick() - s.deathsThisTick() - s.preCreatureDeathsThisTick();
            growthRateOverTime.add(growth);

            avgEnergyOverTime.add(StatUtils.mean(s.creatureEnergies()));
            avgNutritionOverTime.add(StatUtils.mean(s.foodNutritions()));

            allEnergies.addAll(s.creatureEnergies());
            allNutritions.addAll(s.foodNutritions());

            _totalDeaths    += s.deathsThisTick();
            _totalPreDeaths += s.preCreatureDeathsThisTick();
            _totalBirths    += s.birthsThisTick();
            _totalEaten     += s.foodEatenThisTick();
            _totalExpired   += s.foodExpiredThisTick();
            _totalSpawned   += s.foodSpawnedThisTick();

            if (s.populationCount() > _peakPop)    { _peakPop = s.populationCount(); _peakPopTick = s.tick(); }
            if (s.populationCount() < _minPop)     { _minPop = s.populationCount();  _minPopTick = s.tick(); }
            if (s.foodCount() > _peakFood)          { _peakFood = s.foodCount();      _peakFoodTick = s.tick(); }
            if (s.preCreatureCount() > _peakPreCreatures) {
                _peakPreCreatures = s.preCreatureCount();
                _peakPreCreaturesTick = s.tick();
            }
            if (growth > _peakGrowth)   { _peakGrowth = growth;  _peakGrowthTick = s.tick(); }
            if (growth < _peakDecline)  { _peakDecline = growth; _peakDeclineTick = s.tick(); }

            // --- Performance timing ---
            long tCreatures = s.timeUpdateCreaturesNs();
            long tFood      = s.timeUpdateFoodNs();
            long tMap       = s.timeUpdateMapNs();
            long tPreC      = s.timeUpdatePreCreaturesNs();
            long tTotal     = tCreatures + tFood + tMap + tPreC;

            timesCreatures.add(tCreatures);
            timesFood.add(tFood);
            timesMap.add(tMap);
            timesPreCreatures.add(tPreC);
            totalTickTimes.add(tTotal);

            updateCreaturesMsOverTime.add(StatUtils.nsToMs(tCreatures));
            updateFoodMsOverTime.add(StatUtils.nsToMs(tFood));
            updateMapMsOverTime.add(StatUtils.nsToMs(tMap));
            updatePreCreaturesMsOverTime.add(StatUtils.nsToMs(tPreC));
            totalTickMsOverTime.add(StatUtils.nsToMs(tTotal));

            // --- Performance memoria ---
            heapUsedValues.add(s.heapUsedBytes());
            creaturesMemValues.add(s.creaturesMemBytes());
            foodsMemValues.add(s.foodsMemBytes());

            heapUsedMbOverTime.add(StatUtils.bytesToMb(s.heapUsedBytes()));
            creaturesMemMbOverTime.add(StatUtils.bytesToMb(s.creaturesMemBytes()));
            foodsMemMbOverTime.add(StatUtils.bytesToMb(s.foodsMemBytes()));
            preCreaturesMemMbOverTime.add(StatUtils.bytesToMb(s.preCreaturesMemBytes()));
        }

        // ── Popolazione ──
        this.peakPopulation     = _peakPop;
        this.peakPopulationTick = _peakPopTick;
        this.minPopulation      = (_minPop == Integer.MAX_VALUE) ? 0 : _minPop;
        this.minPopulationTick  = _minPopTick;
        this.avgPopulation      = StatUtils.meanDouble(populationOverTime.stream().map(Integer::doubleValue).toList());

        // ── Demografia ──
        this.totalDeaths            = _totalDeaths;
        this.totalPreCreatureDeaths = _totalPreDeaths;
        this.totalDeathsAll         = _totalDeaths + _totalPreDeaths;
        this.totalBirths            = _totalBirths;
        this.avgDeathsPerTick       = totalTicks > 0 ? (double) _totalDeaths / totalTicks : 0;
        this.avgBirthsPerTick       = totalTicks > 0 ? (double) _totalBirths / totalTicks : 0;
        this.mortalityRate          = _peakPop > 0 ? (_totalDeaths / (double) _peakPop) * 100.0 : 0;
        this.birthDeathRatio        = totalDeathsAll > 0 ? (double) _totalBirths / totalDeathsAll : 0;
        this.peakGrowthTick         = _peakGrowthTick;
        this.peakDeclineTick        = _peakDeclineTick;
        this.peakPreCreatures       = _peakPreCreatures;
        this.peakPreCreaturesTick   = _peakPreCreaturesTick;

        // ── Energia ──
        this.avgCreatureEnergy    = StatUtils.mean(allEnergies);
        this.medianCreatureEnergy = StatUtils.median(allEnergies);
        this.minCreatureEnergy    = StatUtils.min(allEnergies);
        this.maxCreatureEnergy    = StatUtils.max(allEnergies);
        this.stdDevCreatureEnergy = StatUtils.standardDeviation(allEnergies);
        this.p25CreatureEnergy    = StatUtils.percentile(allEnergies, 25);
        this.p75CreatureEnergy    = StatUtils.percentile(allEnergies, 75);
        this.p90CreatureEnergy    = StatUtils.percentile(allEnergies, 90);

        // ── Cibo ──
        this.totalFoodEaten        = _totalEaten;
        this.totalFoodExpired      = _totalExpired;
        this.totalFoodSpawned      = _totalSpawned;
        int totalFoodResolved      = _totalEaten + _totalExpired;
        this.foodConsumptionRate   = totalFoodResolved > 0 ? (_totalEaten / (double) totalFoodResolved) * 100.0 : 0;
        this.avgFoodPerTick        = StatUtils.meanDouble(foodCountOverTime.stream().map(Integer::doubleValue).toList());
        this.avgFoodSpawnedPerTick = totalTicks > 0 ? (double) _totalSpawned / totalTicks : 0;
        this.peakFood              = _peakFood;
        this.peakFoodTick          = _peakFoodTick;

        // ── Nutrizione ──
        this.avgFoodNutrition    = StatUtils.mean(allNutritions);
        this.medianFoodNutrition = StatUtils.median(allNutritions);
        this.minFoodNutrition    = StatUtils.min(allNutritions);
        this.maxFoodNutrition    = StatUtils.max(allNutritions);
        this.stdDevFoodNutrition = StatUtils.standardDeviation(allNutritions);
        this.p25FoodNutrition    = StatUtils.percentile(allNutritions, 25);
        this.p75FoodNutrition    = StatUtils.percentile(allNutritions, 75);

        // ── Performance timing ──
        this.avgUpdateCreaturesMs    = StatUtils.nsToMs(StatUtils.meanLong(timesCreatures));
        this.medianUpdateCreaturesMs = StatUtils.nsToMs(StatUtils.medianLong(timesCreatures));
        this.minUpdateCreaturesMs    = StatUtils.nsToMs(StatUtils.minLong(timesCreatures));
        this.maxUpdateCreaturesMs    = StatUtils.nsToMs(StatUtils.maxLong(timesCreatures));
        this.stdDevUpdateCreaturesMs = StatUtils.nsToMs(StatUtils.stdDevLong(timesCreatures));
        this.p95UpdateCreaturesMs    = StatUtils.nsToMs(StatUtils.percentileLong(timesCreatures, 95));

        this.avgUpdateFoodMs    = StatUtils.nsToMs(StatUtils.meanLong(timesFood));
        this.medianUpdateFoodMs = StatUtils.nsToMs(StatUtils.medianLong(timesFood));
        this.minUpdateFoodMs    = StatUtils.nsToMs(StatUtils.minLong(timesFood));
        this.maxUpdateFoodMs    = StatUtils.nsToMs(StatUtils.maxLong(timesFood));
        this.stdDevUpdateFoodMs = StatUtils.nsToMs(StatUtils.stdDevLong(timesFood));
        this.p95UpdateFoodMs    = StatUtils.nsToMs(StatUtils.percentileLong(timesFood, 95));

        this.avgUpdateMapMs    = StatUtils.nsToMs(StatUtils.meanLong(timesMap));
        this.medianUpdateMapMs = StatUtils.nsToMs(StatUtils.medianLong(timesMap));
        this.minUpdateMapMs    = StatUtils.nsToMs(StatUtils.minLong(timesMap));
        this.maxUpdateMapMs    = StatUtils.nsToMs(StatUtils.maxLong(timesMap));
        this.stdDevUpdateMapMs = StatUtils.nsToMs(StatUtils.stdDevLong(timesMap));
        this.p95UpdateMapMs    = StatUtils.nsToMs(StatUtils.percentileLong(timesMap, 95));

        this.avgUpdatePreCreaturesMs    = StatUtils.nsToMs(StatUtils.meanLong(timesPreCreatures));
        this.medianUpdatePreCreaturesMs = StatUtils.nsToMs(StatUtils.medianLong(timesPreCreatures));
        this.minUpdatePreCreaturesMs    = StatUtils.nsToMs(StatUtils.minLong(timesPreCreatures));
        this.maxUpdatePreCreaturesMs    = StatUtils.nsToMs(StatUtils.maxLong(timesPreCreatures));
        this.stdDevUpdatePreCreaturesMs = StatUtils.nsToMs(StatUtils.stdDevLong(timesPreCreatures));
        this.p95UpdatePreCreaturesMs    = StatUtils.nsToMs(StatUtils.percentileLong(timesPreCreatures, 95));

        this.avgTotalTickMs = StatUtils.nsToMs(StatUtils.meanLong(totalTickTimes));
        this.maxTotalTickMs = StatUtils.nsToMs(StatUtils.maxLong(totalTickTimes));
        this.p95TotalTickMs = StatUtils.nsToMs(StatUtils.percentileLong(totalTickTimes, 95));

        // ── Performance memoria ──
        this.peakHeapUsedMb    = StatUtils.bytesToMb(StatUtils.maxLong(heapUsedValues));
        this.avgHeapUsedMb     = StatUtils.bytesToMb(StatUtils.meanLong(heapUsedValues));
        this.minHeapUsedMb     = StatUtils.bytesToMb(StatUtils.minLong(heapUsedValues));
        this.stdDevHeapUsedMb  = StatUtils.bytesToMb(StatUtils.stdDevLong(heapUsedValues));
        this.finalHeapUsedMb   = heapUsedMbOverTime.isEmpty() ? 0 : heapUsedMbOverTime.getLast();

        this.peakCreaturesMemMb    = StatUtils.bytesToMb(StatUtils.maxLong(creaturesMemValues));
        this.peakFoodsMemMb        = StatUtils.bytesToMb(StatUtils.maxLong(foodsMemValues));
        this.peakPreCreaturesMemMb = preCreaturesMemMbOverTime.stream().mapToDouble(Double::doubleValue).max().orElse(0);
        this.avgCreaturesMemMb     = StatUtils.bytesToMb(StatUtils.meanLong(creaturesMemValues));
        this.avgFoodsMemMb         = StatUtils.bytesToMb(StatUtils.meanLong(foodsMemValues));
    }
}
package stats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class con metodi statici per calcoli statistici comuni.
 * Supporta List<Float> per valori di simulazione e List<Long> per timing in nanosecondi.
 */
public final class StatUtils {

    private StatUtils() {}

    // ==========================================================================
    // FLOAT — valori di simulazione (energia, nutrizione, ecc.)
    // ==========================================================================

    public static double mean(List<Float> values) {
        if (values == null || values.isEmpty()) return 0.0;
        return values.stream().mapToDouble(Float::doubleValue).average().orElse(0.0);
    }

    public static double median(List<Float> values) {
        if (values == null || values.isEmpty()) return 0.0;
        List<Float> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        int size = sorted.size();
        if (size % 2 == 0)
            return (sorted.get(size / 2 - 1) + sorted.get(size / 2)) / 2.0;
        return sorted.get(size / 2);
    }

    public static float min(List<Float> values) {
        if (values == null || values.isEmpty()) return 0f;
        return Collections.min(values);
    }

    public static float max(List<Float> values) {
        if (values == null || values.isEmpty()) return 0f;
        return Collections.max(values);
    }

    public static double standardDeviation(List<Float> values) {
        if (values == null || values.size() < 2) return 0.0;
        double avg = mean(values);
        double variance = values.stream()
                .mapToDouble(v -> Math.pow(v - avg, 2))
                .average().orElse(0.0);
        return Math.sqrt(variance);
    }

    public static double percentile(List<Float> values, double percentile) {
        if (values == null || values.isEmpty()) return 0.0;
        List<Float> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        double index = (percentile / 100.0) * (sorted.size() - 1);
        int lower = (int) Math.floor(index);
        int upper = (int) Math.ceil(index);
        if (lower == upper) return sorted.get(lower);
        return sorted.get(lower) * (1 - (index - lower)) + sorted.get(upper) * (index - lower);
    }

    public static double sum(List<Float> values) {
        if (values == null || values.isEmpty()) return 0.0;
        return values.stream().mapToDouble(Float::doubleValue).sum();
    }

    // ==========================================================================
    // LONG — timing in nanosecondi, memoria in bytes
    // ==========================================================================

    public static double meanLong(List<Long> values) {
        if (values == null || values.isEmpty()) return 0.0;
        return values.stream().mapToLong(Long::longValue).average().orElse(0.0);
    }

    public static double medianLong(List<Long> values) {
        if (values == null || values.isEmpty()) return 0.0;
        List<Long> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        int size = sorted.size();
        if (size % 2 == 0)
            return (sorted.get(size / 2 - 1) + sorted.get(size / 2)) / 2.0;
        return sorted.get(size / 2);
    }

    public static long minLong(List<Long> values) {
        if (values == null || values.isEmpty()) return 0L;
        return Collections.min(values);
    }

    public static long maxLong(List<Long> values) {
        if (values == null || values.isEmpty()) return 0L;
        return Collections.max(values);
    }

    public static double stdDevLong(List<Long> values) {
        if (values == null || values.size() < 2) return 0.0;
        double avg = meanLong(values);
        double variance = values.stream()
                .mapToDouble(v -> Math.pow(v - avg, 2))
                .average().orElse(0.0);
        return Math.sqrt(variance);
    }

    public static double percentileLong(List<Long> values, double percentile) {
        if (values == null || values.isEmpty()) return 0.0;
        List<Long> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        double index = (percentile / 100.0) * (sorted.size() - 1);
        int lower = (int) Math.floor(index);
        int upper = (int) Math.ceil(index);
        if (lower == upper) return sorted.get(lower);
        return sorted.get(lower) * (1 - (index - lower)) + sorted.get(upper) * (index - lower);
    }

    // ==========================================================================
    // DOUBLE — medie di serie temporali
    // ==========================================================================

    public static double meanDouble(List<Double> values) {
        if (values == null || values.isEmpty()) return 0.0;
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    // ==========================================================================
    // CONVERSIONI
    // ==========================================================================

    /** Converte nanosecondi in millisecondi con precisione decimale. */
    public static double nsToMs(long ns) {
        return ns / 1_000_000.0;
    }

    public static double nsToMs(double ns) {
        return ns / 1_000_000.0;
    }

    /** Converte bytes in megabyte. */
    public static double bytesToMb(long bytes) {
        return bytes / (1024.0 * 1024.0);
    }

    public static double bytesToMb(double bytes) {
        return bytes / (1024.0 * 1024.0);
    }
}
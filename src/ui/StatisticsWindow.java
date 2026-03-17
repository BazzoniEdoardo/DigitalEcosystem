package ui;

import core.App;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import managers.StatsManager;
import stats.SimulationSnapshot;

import java.util.List;

/**
 * Finestra statistiche live della simulazione.
 * Si aggiorna automaticamente ogni secondo leggendo gli snapshot di StatsManager.
 */
public class StatisticsWindow {

    // ── Stile ──
    private static final String BG_DARK       = "-fx-background-color: #0f1117;";
    private static final String BG_CARD       = "-fx-background-color: #181c27; -fx-background-radius: 8;";
    private static final String COLOR_TEXT    = "#e8eaf6";
    private static final String COLOR_DIM     = "#6b7280";
    private static final String COLOR_POP     = "#7c9cff";
    private static final String COLOR_ENERGY  = "#f0c36d";
    private static final String COLOR_FOOD    = "#5dbcb0";
    private static final String COLOR_DEATH   = "#e05c6a";
    private static final String COLOR_BIRTH   = "#6ee7b7";
    private static final String COLOR_ACCENT  = "#3d5afe";
    private static final String COLOR_PERF    = "#f472b6";
    private static final String COLOR_MEM     = "#34d399";

    private static final String FONT_MONO = "-fx-font-family: 'Courier New'; ";

    private Timeline refreshTimeline;

    // ── Sezione Overview ──
    private Label lblTick, lblDuration, lblRunning;

    // ── Sezione Popolazione ──
    private Label lblPop, lblPreCreatures, lblBirths, lblDeaths, lblDeathsSpace, lblGrowth;

    // ── Sezione Energia ──
    private Label lblEngAvg, lblEngMin, lblEngMax, lblEngMedian, lblEngStdDev;

    // ── Sezione Cibo ──
    private Label lblFoodCount, lblFoodEaten, lblFoodExpired, lblFoodSpawned, lblFoodNutrAvg;

    // ── Sezione Performance ──
    private Label lblTickMs, lblCreaturesMs, lblFoodMs, lblMapMs, lblPreCMs;
    private Label lblHeapUsed, lblHeapFree;

    public void show() {
        final Stage stage = new Stage();
        stage.setAlwaysOnTop(true);
        stage.setTitle("Live Statistics");

        final VBox root = new VBox(12);
        root.setStyle(BG_DARK);
        root.setPadding(new Insets(16));

        // Header
        Label header = styledLabel("LIVE STATISTICS", COLOR_TEXT, 15, true);
        header.setStyle(FONT_MONO + "-fx-font-size: 15; -fx-font-weight: bold; -fx-text-fill: white;");
        root.getChildren().add(header);
        root.getChildren().add(separator());

        // Sezioni
        root.getChildren().add(buildOverviewSection());
        root.getChildren().add(buildPopulationSection());
        root.getChildren().add(buildEnergySection());
        root.getChildren().add(buildFoodSection());
        root.getChildren().add(buildPerformanceSection());

        ScrollPane scroll = new ScrollPane(root);
        scroll.setStyle("-fx-background: #0f1117; -fx-background-color: #0f1117;");
        scroll.setFitToWidth(true);

        Scene scene = new Scene(scroll, 480, 680);
        scene.setFill(Color.web("#0f1117"));
        stage.setScene(scene);
        stage.show();

        startRefresh();

        stage.setOnCloseRequest(e -> stopRefresh());
    }

    // =========================================================================
    // SEZIONI
    // =========================================================================

    private VBox buildOverviewSection() {
        lblTick     = valueLabel("—");
        lblDuration = valueLabel("—");
        lblRunning  = valueLabel("—");

        return section("OVERVIEW", new String[][]{
                {"Tick",       null},
                {"Duration",   null},
                {"State",      null},
        }, new Label[]{lblTick, lblDuration, lblRunning});
    }

    private VBox buildPopulationSection() {
        lblPop         = valueLabel("—", COLOR_POP);
        lblPreCreatures= valueLabel("—", COLOR_ENERGY);
        lblBirths      = valueLabel("—", COLOR_BIRTH);
        lblDeaths      = valueLabel("—", COLOR_DEATH);
        lblDeathsSpace = valueLabel("—", COLOR_DEATH);
        lblGrowth      = valueLabel("—");

        return section("POPULATION", new String[][]{
                {"Population",      null},
                {"Pre-Creatures",   null},
                {"Births (total)",  null},
                {"Deaths (energy)", null},
                {"Deaths (space)",  null},
                {"Growth rate",     null},
        }, new Label[]{lblPop, lblPreCreatures, lblBirths, lblDeaths, lblDeathsSpace, lblGrowth});
    }

    private VBox buildEnergySection() {
        lblEngAvg    = valueLabel("—", COLOR_ENERGY);
        lblEngMedian = valueLabel("—", COLOR_ENERGY);
        lblEngMin    = valueLabel("—", COLOR_DEATH);
        lblEngMax    = valueLabel("—", COLOR_POP);
        lblEngStdDev = valueLabel("—");

        return section("CREATURE ENERGY (this tick)", new String[][]{
                {"Average",  null},
                {"Median",   null},
                {"Min",      null},
                {"Max",      null},
                {"Std Dev",  null},
        }, new Label[]{lblEngAvg, lblEngMedian, lblEngMin, lblEngMax, lblEngStdDev});
    }

    private VBox buildFoodSection() {
        lblFoodCount    = valueLabel("—", COLOR_FOOD);
        lblFoodEaten    = valueLabel("—", COLOR_ENERGY);
        lblFoodExpired  = valueLabel("—", COLOR_DEATH);
        lblFoodSpawned  = valueLabel("—", "#38bdf8");
        lblFoodNutrAvg  = valueLabel("—", "#a78bfa");

        return section("FOOD (this tick)", new String[][]{
                {"On map",        null},
                {"Eaten total",   null},
                {"Expired total", null},
                {"Spawned total", null},
                {"Avg nutrition", null},
        }, new Label[]{lblFoodCount, lblFoodEaten, lblFoodExpired, lblFoodSpawned, lblFoodNutrAvg});
    }

    private VBox buildPerformanceSection() {
        lblTickMs      = valueLabel("—", COLOR_PERF);
        lblCreaturesMs = valueLabel("—", COLOR_POP);
        lblFoodMs      = valueLabel("—", COLOR_FOOD);
        lblMapMs       = valueLabel("—", COLOR_ENERGY);
        lblPreCMs      = valueLabel("—", "#fbbf24");
        lblHeapUsed    = valueLabel("—", COLOR_MEM);
        lblHeapFree    = valueLabel("—");

        return section("PERFORMANCE (last tick)", new String[][]{
                {"Total tick ms",         null},
                {"updateCreatures ms",    null},
                {"updateFood ms",         null},
                {"updateMap ms",          null},
                {"updatePreCreatures ms", null},
                {"Heap used",             null},
                {"Heap free",             null},
        }, new Label[]{lblTickMs, lblCreaturesMs, lblFoodMs, lblMapMs, lblPreCMs, lblHeapUsed, lblHeapFree});
    }

    // =========================================================================
    // REFRESH
    // =========================================================================

    private void startRefresh() {
        refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> refresh()));
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }

    private void stopRefresh() {
        if (refreshTimeline != null) refreshTimeline.stop();
    }

    private void refresh() {
        final SimulationSnapshot snap = StatsManager.getLatestSnapshot();
        final List<SimulationSnapshot> all = StatsManager.getSnapshotsCopy();
        final boolean running = App.getSimManager().isRunning();

        // Overview
        lblTick.setText(snap != null ? String.valueOf(snap.tick()) : "—");
        lblDuration.setText(formatMs(StatsManager.getCurrentDurationMs()));
        lblRunning.setText(running ? "▶  Running" : "⏸  Paused");
        lblRunning.setStyle(FONT_MONO + "-fx-font-size: 12; -fx-text-fill: " +
                (running ? COLOR_BIRTH : COLOR_ENERGY) + ";");

        if (snap == null) return;

        // Popolazione
        lblPop.setText(String.valueOf(snap.populationCount()));
        lblPreCreatures.setText(String.valueOf(snap.preCreatureCount()));

        // Cumulative dai snapshot
        int totBirths = 0, totDeaths = 0, totDeathsSpace = 0,
                totEaten = 0, totExpired = 0, totSpawned = 0;
        for (SimulationSnapshot s : all) {
            totBirths      += s.birthsThisTick();
            totDeaths      += s.deathsThisTick();
            totDeathsSpace += s.preCreatureDeathsThisTick();
            totEaten       += s.foodEatenThisTick();
            totExpired     += s.foodExpiredThisTick();
            totSpawned     += s.foodSpawnedThisTick();
        }
        int growthThisTick = snap.birthsThisTick() - snap.deathsThisTick() - snap.preCreatureDeathsThisTick();
        lblBirths.setText(totBirths + "  (+" + snap.birthsThisTick() + "/tick)");
        lblDeaths.setText(totDeaths + "  (+" + snap.deathsThisTick() + "/tick)");
        lblDeathsSpace.setText(totDeathsSpace + "  (+" + snap.preCreatureDeathsThisTick() + "/tick)");
        lblGrowth.setText((growthThisTick >= 0 ? "+" : "") + growthThisTick + " this tick");
        lblGrowth.setStyle(FONT_MONO + "-fx-font-size: 12; -fx-text-fill: " +
                (growthThisTick >= 0 ? COLOR_BIRTH : COLOR_DEATH) + ";");

        // Energia
        var energies = snap.creatureEnergies();
        if (!energies.isEmpty()) {
            float eMin = energies.stream().min(Float::compare).orElse(0f);
            float eMax = energies.stream().max(Float::compare).orElse(0f);
            double eAvg = energies.stream().mapToDouble(Float::doubleValue).average().orElse(0);
            var sorted = energies.stream().sorted().toList();
            double eMedian = sorted.size() % 2 == 0
                    ? (sorted.get(sorted.size()/2-1) + sorted.get(sorted.size()/2)) / 2.0
                    : sorted.get(sorted.size()/2);
            double eStd = Math.sqrt(energies.stream()
                    .mapToDouble(v -> Math.pow(v - eAvg, 2)).average().orElse(0));
            lblEngAvg.setText(String.format("%.2f", eAvg));
            lblEngMedian.setText(String.format("%.2f", eMedian));
            lblEngMin.setText(String.format("%.2f", eMin));
            lblEngMax.setText(String.format("%.2f", eMax));
            lblEngStdDev.setText(String.format("%.2f", eStd));
        }

        // Cibo
        var nutritions = snap.foodNutritions();
        double nutrAvg = nutritions.isEmpty() ? 0
                : nutritions.stream().mapToDouble(Float::doubleValue).average().orElse(0);
        lblFoodCount.setText(String.valueOf(snap.foodCount()));
        lblFoodEaten.setText(String.valueOf(totEaten));
        lblFoodExpired.setText(String.valueOf(totExpired));
        lblFoodSpawned.setText(String.valueOf(totSpawned));
        lblFoodNutrAvg.setText(String.format("%.2f", nutrAvg));

        // Performance
        double tCreaturesMs = snap.timeUpdateCreaturesNs() / 1_000_000.0;
        double tFoodMs      = snap.timeUpdateFoodNs()      / 1_000_000.0;
        double tMapMs       = snap.timeUpdateMapNs()       / 1_000_000.0;
        double tPreCMs      = snap.timeUpdatePreCreaturesNs() / 1_000_000.0;
        double tTotalMs     = tCreaturesMs + tFoodMs + tMapMs + tPreCMs;
        double heapUsedMb   = (snap.heapTotalBytes() - snap.heapFreeBytes()) / (1024.0 * 1024.0);
        double heapFreeMb   = snap.heapFreeBytes() / (1024.0 * 1024.0);

        lblTickMs.setText(String.format("%.3f ms", tTotalMs));
        lblCreaturesMs.setText(String.format("%.3f ms", tCreaturesMs));
        lblFoodMs.setText(String.format("%.3f ms", tFoodMs));
        lblMapMs.setText(String.format("%.3f ms", tMapMs));
        lblPreCMs.setText(String.format("%.3f ms", tPreCMs));
        lblHeapUsed.setText(String.format("%.1f MB", heapUsedMb));
        lblHeapFree.setText(String.format("%.1f MB", heapFreeMb));
    }

    // =========================================================================
    // BUILDER HELPERS
    // =========================================================================

    private VBox section(String title, String[][] keys, Label[] values) {
        VBox card = new VBox(6);
        card.setStyle(BG_CARD);
        card.setPadding(new Insets(12, 16, 12, 16));

        Label titleLbl = new Label(title);
        titleLbl.setStyle(FONT_MONO + "-fx-font-size: 10; -fx-font-weight: bold; "
                + "-fx-text-fill: " + COLOR_ACCENT + "; -fx-letter-spacing: 2;");
        card.getChildren().add(titleLbl);
        card.getChildren().add(new Separator());

        for (int i = 0; i < keys.length; i++) {
            HBox row = new HBox();
            row.setAlignment(Pos.CENTER_LEFT);

            Label keyLbl = new Label(keys[i][0]);
            keyLbl.setStyle(FONT_MONO + "-fx-font-size: 11; -fx-text-fill: " + COLOR_DIM + ";");
            keyLbl.setMinWidth(160);

            Label valLbl = values[i];
            HBox.setHgrow(valLbl, Priority.ALWAYS);

            row.getChildren().addAll(keyLbl, valLbl);
            card.getChildren().add(row);
        }

        return card;
    }

    private Label valueLabel(String text) {
        return valueLabel(text, COLOR_TEXT);
    }

    private Label valueLabel(String text, String color) {
        Label l = new Label(text);
        l.setStyle(FONT_MONO + "-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        return l;
    }

    private Label styledLabel(String text, String color, int size, boolean bold) {
        Label l = new Label(text);
        l.setStyle(FONT_MONO + "-fx-font-size: " + size + "; "
                + (bold ? "-fx-font-weight: bold; " : "")
                + "-fx-text-fill: " + color + ";");
        return l;
    }

    private Separator separator() {
        Separator s = new Separator();
        s.setStyle("-fx-background-color: #232840;");
        return s;
    }

    private String formatMs(long ms) {
        long s = ms / 1000, m = s / 60, h = m / 60;
        return String.format("%02d:%02d:%02d", h, m % 60, s % 60);
    }
}
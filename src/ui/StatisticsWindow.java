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

import static ui.SimTheme.*;

/**
 * Live statistics window — refreshes every 500 ms.
 * Dark-minimal aesthetic: monochrome base with single accent colour.
 */
public class StatisticsWindow {

    private Timeline refreshTimeline;

    // Overview
    private Label lblTick, lblDuration, lblRunning;

    // Population
    private Label lblPop, lblPreCreatures, lblBirths, lblDeaths, lblDeathsSpace, lblGrowth;

    // Energy
    private Label lblEngAvg, lblEngMedian, lblEngMin, lblEngMax, lblEngStdDev;

    // Food
    private Label lblFoodCount, lblFoodEaten, lblFoodExpired, lblFoodSpawned, lblFoodNutrAvg;

    // Performance
    private Label lblTickMs, lblCreaturesMs, lblFoodMs, lblMapMs, lblPreCMs;
    private Label lblHeapUsed, lblHeapFree;

    // =========================================================================
    // SHOW
    // =========================================================================

    public void show() {
        final Stage stage = new Stage();
        stage.setAlwaysOnTop(true);
        stage.setTitle("Live Statistics");
        stage.setMinWidth(420);

        final VBox root = new VBox(0);
        root.setStyle(FX_BG_BASE);

        // Window header
        root.getChildren().add(windowHeader("LIVE STATISTICS"));

        // Sections
        root.getChildren().addAll(
                buildSection("OVERVIEW",     overviewRows()),
                buildSection("POPULATION",   populationRows()),
                buildSection("ENERGY",       energyRows()),
                buildSection("FOOD",         foodRows()),
                buildSection("PERFORMANCE",  performanceRows())
        );

        final ScrollPane scroll = new ScrollPane(root);
        scroll.setStyle(FX_BG_BASE + " -fx-background:" + C_BG_BASE + ";");
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        final Scene scene = new Scene(scroll, 440, 680);
        scene.setFill(Color.web(C_BG_BASE));
        SimTheme.applyUri(scene, SimTheme.buildDataUri());
        stage.setScene(scene);
        stage.show();

        startRefresh();
        stage.setOnCloseRequest(e -> stopRefresh());
    }

    // =========================================================================
    // SECTION BUILDERS — also initialise the Label fields
    // =========================================================================

    private Row[] overviewRows() {
        lblTick     = val("—");
        lblDuration = val("—");
        lblRunning  = val("—");
        return rows(
                new Row("Tick",     lblTick),
                new Row("Duration", lblDuration),
                new Row("State",    lblRunning)
        );
    }

    private Row[] populationRows() {
        lblPop          = val("—");
        lblPreCreatures = val("—");
        lblBirths       = val("—");
        lblDeaths       = val("—");
        lblDeathsSpace  = val("—");
        lblGrowth       = val("—");
        return rows(
                new Row("Population",       lblPop),
                new Row("Pre-Creatures",    lblPreCreatures),
                new Row("Births (total)",   lblBirths),
                new Row("Deaths (energy)",  lblDeaths),
                new Row("Deaths (space)",   lblDeathsSpace),
                new Row("Growth / tick",    lblGrowth)
        );
    }

    private Row[] energyRows() {
        lblEngAvg    = val("—");
        lblEngMedian = val("—");
        lblEngMin    = val("—");
        lblEngMax    = val("—");
        lblEngStdDev = val("—");
        return rows(
                new Row("Average",  lblEngAvg),
                new Row("Median",   lblEngMedian),
                new Row("Min",      lblEngMin),
                new Row("Max",      lblEngMax),
                new Row("Std Dev",  lblEngStdDev)
        );
    }

    private Row[] foodRows() {
        lblFoodCount   = val("—");
        lblFoodEaten   = val("—");
        lblFoodExpired = val("—");
        lblFoodSpawned = val("—");
        lblFoodNutrAvg = val("—");
        return rows(
                new Row("On map",         lblFoodCount),
                new Row("Eaten (total)",  lblFoodEaten),
                new Row("Expired (total)",lblFoodExpired),
                new Row("Spawned (total)",lblFoodSpawned),
                new Row("Avg nutrition",  lblFoodNutrAvg)
        );
    }

    private Row[] performanceRows() {
        lblTickMs      = val("—");
        lblCreaturesMs = val("—");
        lblFoodMs      = val("—");
        lblMapMs       = val("—");
        lblPreCMs      = val("—");
        lblHeapUsed    = val("—");
        lblHeapFree    = val("—");
        return rows(
                new Row("Total tick",           lblTickMs),
                new Row("updateCreatures",       lblCreaturesMs),
                new Row("updateFood",            lblFoodMs),
                new Row("updateMap",             lblMapMs),
                new Row("updatePreCreatures",    lblPreCMs),
                new Row("Heap used",             lblHeapUsed),
                new Row("Heap free",             lblHeapFree)
        );
    }

    // =========================================================================
    // REFRESH
    // =========================================================================

    private void startRefresh() {
        refreshTimeline = new Timeline(
                new KeyFrame(Duration.millis(500), e -> refresh()));
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }

    private void stopRefresh() {
        if (refreshTimeline != null) refreshTimeline.stop();
    }

    private void refresh() {
        final SimulationSnapshot snap    = StatsManager.getLatestSnapshot();
        final List<SimulationSnapshot> all = StatsManager.getSnapshotsCopy();
        final boolean running = App.getSimManager().isRunning();

        // Overview
        lblTick.setText(snap != null ? String.valueOf(snap.tick()) : "—");
        lblDuration.setText(formatMs(StatsManager.getCurrentDurationMs()));
        lblRunning.setText(running ? "▶  Running" : "⏸  Paused");
        lblRunning.setStyle(valStyle(running ? C_OK : C_GOLD));

        if (snap == null) return;

        // Population accumulators
        int totBirths = 0, totDeaths = 0, totDeathsSpace = 0,
                totEaten = 0, totExpired = 0, totSpawned = 0;
        for (SimulationSnapshot s : all) {
            totBirths       += s.birthsThisTick();
            totDeaths       += s.deathsThisTick();
            totDeathsSpace  += s.preCreatureDeathsThisTick();
            totEaten        += s.foodEatenThisTick();
            totExpired      += s.foodExpiredThisTick();
            totSpawned      += s.foodSpawnedThisTick();
        }

        lblPop.setText(String.valueOf(snap.populationCount()));
        lblPreCreatures.setText(String.valueOf(snap.preCreatureCount()));
        lblBirths.setText(totBirths + "  (+" + snap.birthsThisTick() + " /tick)");
        lblDeaths.setText(totDeaths + "  (+" + snap.deathsThisTick() + " /tick)");
        lblDeathsSpace.setText(totDeathsSpace + "  (+" + snap.preCreatureDeathsThisTick() + " /tick)");

        final int growth = snap.birthsThisTick() - snap.deathsThisTick() - snap.preCreatureDeathsThisTick();
        lblGrowth.setText((growth >= 0 ? "+" : "") + growth);
        lblGrowth.setStyle(valStyle(growth >= 0 ? C_OK : C_WARN));

        // Energy
        final var energies = snap.creatureEnergies();
        if (!energies.isEmpty()) {
            final float  eMin    = energies.stream().min(Float::compare).orElse(0f);
            final float  eMax    = energies.stream().max(Float::compare).orElse(0f);
            final double eAvg    = energies.stream().mapToDouble(Float::doubleValue).average().orElse(0);
            final var    sorted  = energies.stream().sorted().toList();
            final double eMedian = sorted.size() % 2 == 0
                    ? (sorted.get(sorted.size() / 2 - 1) + sorted.get(sorted.size() / 2)) / 2.0
                    : sorted.get(sorted.size() / 2);
            final double eStd    = Math.sqrt(
                    energies.stream().mapToDouble(v -> Math.pow(v - eAvg, 2)).average().orElse(0));

            lblEngAvg.setText(String.format("%.2f", eAvg));
            lblEngMedian.setText(String.format("%.2f", eMedian));
            lblEngMin.setText(String.format("%.2f", eMin));
            lblEngMax.setText(String.format("%.2f", eMax));
            lblEngStdDev.setText(String.format("%.2f", eStd));
        }

        // Food
        final var nutritions = snap.foodNutritions();
        final double nutrAvg = nutritions.isEmpty() ? 0
                : nutritions.stream().mapToDouble(Float::doubleValue).average().orElse(0);
        lblFoodCount.setText(String.valueOf(snap.foodCount()));
        lblFoodEaten.setText(String.valueOf(totEaten));
        lblFoodExpired.setText(String.valueOf(totExpired));
        lblFoodSpawned.setText(String.valueOf(totSpawned));
        lblFoodNutrAvg.setText(String.format("%.2f", nutrAvg));

        // Performance
        final double tC     = snap.timeUpdateCreaturesNs()    / 1_000_000.0;
        final double tF     = snap.timeUpdateFoodNs()          / 1_000_000.0;
        final double tM     = snap.timeUpdateMapNs()           / 1_000_000.0;
        final double tP     = snap.timeUpdatePreCreaturesNs()  / 1_000_000.0;
        final double tTotal = tC + tF + tM + tP;
        final double hUsed  = (snap.heapTotalBytes() - snap.heapFreeBytes()) / (1024.0 * 1024.0);
        final double hFree  = snap.heapFreeBytes() / (1024.0 * 1024.0);

        lblTickMs.setText(String.format("%.3f ms", tTotal));
        lblCreaturesMs.setText(String.format("%.3f ms", tC));
        lblFoodMs.setText(String.format("%.3f ms", tF));
        lblMapMs.setText(String.format("%.3f ms", tM));
        lblPreCMs.setText(String.format("%.3f ms", tP));
        lblHeapUsed.setText(String.format("%.1f MB", hUsed));
        lblHeapFree.setText(String.format("%.1f MB", hFree));
    }

    // =========================================================================
    // LAYOUT HELPERS
    // =========================================================================

    /** A section card with a title header + rows. */
    private VBox buildSection(final String title, final Row[] rows) {
        final VBox card = new VBox(0);

        // Section title bar
        final Label titleLbl = new Label(title);
        titleLbl.setStyle(FX_FONT
                + "-fx-font-size:9px; -fx-font-weight:bold; "
                + "-fx-text-fill:" + C_TEXT_DIM + "; "
                + "-fx-letter-spacing:2px;");
        titleLbl.setPadding(new Insets(10, 16, 6, 16));

        card.getChildren().add(titleLbl);
        card.getChildren().add(hairline());

        for (final Row r : rows) {
            final HBox row  = new HBox();
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(5, 16, 5, 16));
            row.setStyle("-fx-border-color: transparent transparent " + C_BORDER + " transparent;"
                    + "-fx-border-width: 0 0 1px 0;");

            final Label key = new Label(r.key);
            key.setStyle(FX_FONT + "-fx-font-size:11px; -fx-text-fill:" + C_TEXT_DIM + ";");
            key.setMinWidth(160);

            HBox.setHgrow(r.valueLabel, Priority.ALWAYS);
            row.getChildren().addAll(key, r.valueLabel);
            card.getChildren().add(row);
        }

        return card;
    }

    private Label windowHeader(final String text) {
        final Label l = new Label(text);
        l.setStyle(FX_FONT
                + "-fx-font-size:10px; -fx-font-weight:bold; "
                + "-fx-text-fill:" + C_TEXT + "; "
                + "-fx-letter-spacing:2px;");
        l.setPadding(new Insets(14, 16, 10, 16));
        return l;
    }

    private Separator hairline() {
        final Separator s = new Separator();
        s.setStyle("-fx-background-color:" + C_BORDER + "; -fx-pref-height:1px;");
        return s;
    }

    private Label val(final String text) {
        final Label l = new Label(text);
        l.setStyle(valStyle(C_TEXT));
        return l;
    }

    private String valStyle(final String color) {
        return FX_FONT + "-fx-font-size:12px; -fx-font-weight:bold; -fx-text-fill:" + color + ";";
    }

    private Row[] rows(final Row... r) { return r; }

    private record Row(String key, Label valueLabel) {}

    // =========================================================================
    // UTILITIES
    // =========================================================================

    private String formatMs(final long ms) {
        final long s = ms / 1000, m = s / 60, h = m / 60;
        return String.format("%02d:%02d:%02d", h, m % 60, s % 60);
    }
}
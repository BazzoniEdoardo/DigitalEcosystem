package ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import managers.StatsManager;
import stats.SimulationSnapshot;

import java.util.List;

import static ui.SimTheme.*;

/**
 * Charts window — 4 tabs: Population, Energy, Food, Performance.
 * Refreshes every 2 s from StatsManager snapshots.
 * Uses the unified SimTheme CSS; no inline colour strings except
 * for the chart series which need to be overridden in CSS.
 */
public class ChartsWindow {

    private static final int MAX_POINTS = 300;

    private Timeline refreshTimeline;

    // Population
    private XYChart.Series<Number, Number> seriesPopulation, seriesPreCreatures;

    // Energy
    private XYChart.Series<Number, Number> seriesAvgEnergy, seriesMinEnergy, seriesMaxEnergy;

    // Food
    private XYChart.Series<Number, Number> seriesFoodCount, seriesFoodEaten,
            seriesFoodExpired, seriesFoodSpawned;

    // Performance
    private XYChart.Series<Number, Number> seriesTotalMs, seriesCreaturesMs,
            seriesFoodMs, seriesMapMs, seriesHeapMb;

    // =========================================================================
    // SHOW
    // =========================================================================

    public void show() {
        final Stage stage = new Stage();
        stage.setAlwaysOnTop(false);
        stage.setTitle("Simulation Charts");
        stage.setMinWidth(700);
        stage.setMinHeight(460);

        initSeries();

        final TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getTabs().addAll(
                buildPopulationTab(),
                buildEnergyTab(),
                buildFoodTab(),
                buildPerformanceTab()
        );
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        final VBox root = new VBox(tabPane);
        root.setStyle(FX_BG_BASE);

        final Scene scene = new Scene(root, 820, 500);
        scene.setFill(Color.web(C_BG_BASE));
        SimTheme.applyUri(scene, SimTheme.buildDataUri());
        stage.setScene(scene);
        stage.show();

        startRefresh();
        stage.setOnCloseRequest(e -> stopRefresh());
    }

    // =========================================================================
    // TABS
    // =========================================================================

    private Tab buildPopulationTab() {
        final LineChart<Number, Number> chart = chart(tickAxis(), labelAxis("Creatures"));
        chart.getData().addAll(seriesPopulation, seriesPreCreatures);
        return tab("Population", chart);
    }

    private Tab buildEnergyTab() {
        final LineChart<Number, Number> chart = chart(tickAxis(), labelAxis("Energy"));
        chart.getData().addAll(seriesAvgEnergy, seriesMinEnergy, seriesMaxEnergy);
        return tab("Energy", chart);
    }

    private Tab buildFoodTab() {
        final LineChart<Number, Number> chart = chart(tickAxis(), labelAxis("Units"));
        chart.getData().addAll(seriesFoodCount, seriesFoodEaten, seriesFoodExpired, seriesFoodSpawned);
        return tab("Food", chart);
    }

    private Tab buildPerformanceTab() {
        final LineChart<Number, Number> timingChart = chart(tickAxis(), labelAxis("ms"));
        timingChart.getData().addAll(seriesTotalMs, seriesCreaturesMs, seriesFoodMs, seriesMapMs);
        timingChart.setPrefHeight(230);

        final LineChart<Number, Number> memChart = chart(tickAxis(), labelAxis("MB"));
        memChart.getData().add(seriesHeapMb);
        memChart.setPrefHeight(200);

        final VBox box = new VBox(4, timingChart, memChart);
        box.setStyle(FX_BG_BASE);
        box.setPadding(new Insets(8));
        VBox.setVgrow(timingChart, Priority.ALWAYS);

        final Tab t = new Tab("Performance");
        t.setContent(box);
        return t;
    }

    // =========================================================================
    // SERIES INIT
    // =========================================================================

    private void initSeries() {
        seriesPopulation   = named("Population");
        seriesPreCreatures = named("Pre-Creatures");

        seriesAvgEnergy    = named("Avg");
        seriesMinEnergy    = named("Min");
        seriesMaxEnergy    = named("Max");

        seriesFoodCount    = named("On map");
        seriesFoodEaten    = named("Eaten (cum.)");
        seriesFoodExpired  = named("Expired (cum.)");
        seriesFoodSpawned  = named("Spawned (cum.)");

        seriesTotalMs      = named("Total tick");
        seriesCreaturesMs  = named("Creatures");
        seriesFoodMs       = named("Food");
        seriesMapMs        = named("Map");
        seriesHeapMb       = named("Heap MB");
    }

    // =========================================================================
    // REFRESH
    // =========================================================================

    private void startRefresh() {
        refreshTimeline = new Timeline(
                new KeyFrame(Duration.seconds(2), e -> refreshCharts()));
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
        refreshCharts();
    }

    private void stopRefresh() {
        if (refreshTimeline != null) refreshTimeline.stop();
    }

    private void refreshCharts() {
        final List<SimulationSnapshot> snaps = StatsManager.getSnapshotsCopy();
        if (snaps.isEmpty()) return;

        final int step = Math.max(1, snaps.size() / MAX_POINTS);
        clearAll();

        int cumEaten = 0, cumExpired = 0, cumSpawned = 0;

        for (int i = 0; i < snaps.size(); i += step) {
            final SimulationSnapshot s    = snaps.get(i);
            final int                tick = s.tick();

            for (int j = i; j < Math.min(i + step, snaps.size()); j++) {
                cumEaten   += snaps.get(j).foodEatenThisTick();
                cumExpired += snaps.get(j).foodExpiredThisTick();
                cumSpawned += snaps.get(j).foodSpawnedThisTick();
            }

            // Population
            seriesPopulation.getData().add(pt(tick, s.populationCount()));
            seriesPreCreatures.getData().add(pt(tick, s.preCreatureCount()));

            // Energy
            final var energies = s.creatureEnergies();
            if (!energies.isEmpty()) {
                final double avg = energies.stream().mapToDouble(Float::doubleValue).average().orElse(0);
                final float  min = energies.stream().min(Float::compare).orElse(0f);
                final float  max = energies.stream().max(Float::compare).orElse(0f);
                seriesAvgEnergy.getData().add(pt(tick, avg));
                seriesMinEnergy.getData().add(pt(tick, min));
                seriesMaxEnergy.getData().add(pt(tick, max));
            }

            // Food
            seriesFoodCount.getData().add(pt(tick, s.foodCount()));
            seriesFoodEaten.getData().add(pt(tick, cumEaten));
            seriesFoodExpired.getData().add(pt(tick, cumExpired));
            seriesFoodSpawned.getData().add(pt(tick, cumSpawned));

            // Performance
            final double tC    = s.timeUpdateCreaturesNs()   / 1_000_000.0;
            final double tF    = s.timeUpdateFoodNs()         / 1_000_000.0;
            final double tM    = s.timeUpdateMapNs()          / 1_000_000.0;
            final double tP    = s.timeUpdatePreCreaturesNs() / 1_000_000.0;
            final double hMb   = (s.heapTotalBytes() - s.heapFreeBytes()) / (1024.0 * 1024.0);

            seriesTotalMs.getData().add(pt(tick, tC + tF + tM + tP));
            seriesCreaturesMs.getData().add(pt(tick, tC));
            seriesFoodMs.getData().add(pt(tick, tF));
            seriesMapMs.getData().add(pt(tick, tM));
            seriesHeapMb.getData().add(pt(tick, hMb));
        }
    }

    private void clearAll() {
        seriesPopulation.getData().clear();
        seriesPreCreatures.getData().clear();
        seriesAvgEnergy.getData().clear();
        seriesMinEnergy.getData().clear();
        seriesMaxEnergy.getData().clear();
        seriesFoodCount.getData().clear();
        seriesFoodEaten.getData().clear();
        seriesFoodExpired.getData().clear();
        seriesFoodSpawned.getData().clear();
        seriesTotalMs.getData().clear();
        seriesCreaturesMs.getData().clear();
        seriesFoodMs.getData().clear();
        seriesMapMs.getData().clear();
        seriesHeapMb.getData().clear();
    }

    // =========================================================================
    // FACTORY HELPERS
    // =========================================================================

    private XYChart.Series<Number, Number> named(final String name) {
        final XYChart.Series<Number, Number> s = new XYChart.Series<>();
        s.setName(name);
        return s;
    }

    private XYChart.Data<Number, Number> pt(final int x, final double y) {
        return new XYChart.Data<>(x, y);
    }

    private NumberAxis tickAxis() {
        final NumberAxis ax = new NumberAxis();
        ax.setLabel("Tick");
        return ax;
    }

    private NumberAxis labelAxis(final String label) {
        final NumberAxis ax = new NumberAxis();
        ax.setLabel(label);
        return ax;
    }

    private LineChart<Number, Number> chart(final NumberAxis x, final NumberAxis y) {
        final LineChart<Number, Number> c = new LineChart<>(x, y);
        c.setCreateSymbols(false);
        c.setAnimated(false);
        c.setLegendVisible(true);
        c.setPadding(new Insets(8));
        return c;
    }

    private Tab tab(final String title, final javafx.scene.Node content) {
        final VBox box = new VBox(content);
        box.setStyle(FX_BG_BASE);
        box.setPadding(new Insets(8));
        VBox.setVgrow(content, Priority.ALWAYS);
        final Tab t = new Tab(title);
        t.setContent(box);
        return t;
    }
}
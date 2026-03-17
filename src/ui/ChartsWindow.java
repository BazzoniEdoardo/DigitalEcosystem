package ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import managers.StatsManager;
import stats.SimulationSnapshot;

import java.util.List;

/**
 * Finestra grafici della simulazione con aggiornamento live.
 * 4 tab: Population, Energy, Food, Performance.
 * I grafici vengono ridisegnati ogni 2 secondi dagli snapshot di StatsManager.
 */
public class ChartsWindow {

    private static final String BG_DARK  = "-fx-background-color: #0f1117;";
    private static final String BG_CHART = "-fx-background-color: #181c27;";

    // Max punti mostrati nel grafico prima di sottocampionare
    private static final int MAX_POINTS = 300;

    private Timeline refreshTimeline;

    // ── Serie population tab ──
    private XYChart.Series<Number, Number> seriesPopulation;
    private XYChart.Series<Number, Number> seriesPreCreatures;

    // ── Serie energy tab ──
    private XYChart.Series<Number, Number> seriesAvgEnergy;
    private XYChart.Series<Number, Number> seriesMinEnergy;
    private XYChart.Series<Number, Number> seriesMaxEnergy;

    // ── Serie food tab ──
    private XYChart.Series<Number, Number> seriesFoodCount;
    private XYChart.Series<Number, Number> seriesFoodEaten;
    private XYChart.Series<Number, Number> seriesFoodExpired;
    private XYChart.Series<Number, Number> seriesFoodSpawned;

    // ── Serie performance tab ──
    private XYChart.Series<Number, Number> seriesTotalTickMs;
    private XYChart.Series<Number, Number> seriesCreaturesMs;
    private XYChart.Series<Number, Number> seriesFoodMs;
    private XYChart.Series<Number, Number> seriesMapMs;
    private XYChart.Series<Number, Number> seriesHeapMb;

    public void show() {
        final Stage stage = new Stage();
        stage.setAlwaysOnTop(false);
        stage.setTitle("Simulation Charts");

        initSeries();

        final TabPane tabPane = new TabPane();
        tabPane.setStyle(BG_DARK + " -fx-tab-min-width: 120;");
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tabPane.getTabs().addAll(
                buildPopulationTab(),
                buildEnergyTab(),
                buildFoodTab(),
                buildPerformanceTab()
        );

        final VBox root = new VBox(tabPane);
        root.setStyle(BG_DARK);

        final Scene scene = new Scene(root, 820, 500);
        scene.setFill(Color.web("#0f1117"));
        applyChartCss(scene);

        stage.setScene(scene);
        stage.show();

        startRefresh();
        stage.setOnCloseRequest(e -> stopRefresh());
    }

    // =========================================================================
    // TAB BUILDERS
    // =========================================================================

    private Tab buildPopulationTab() {
        final NumberAxis xAxis = tickAxis();
        final NumberAxis yAxis = countAxis("Creatures");

        final LineChart<Number, Number> chart = makeChart(xAxis, yAxis);
        chart.getData().addAll(seriesPopulation, seriesPreCreatures);

        return tab("Population", chart);
    }

    private Tab buildEnergyTab() {
        final NumberAxis xAxis = tickAxis();
        final NumberAxis yAxis = countAxis("Energy");

        final LineChart<Number, Number> chart = makeChart(xAxis, yAxis);
        chart.getData().addAll(seriesAvgEnergy, seriesMinEnergy, seriesMaxEnergy);

        return tab("Energy", chart);
    }

    private Tab buildFoodTab() {
        final NumberAxis xAxis = tickAxis();
        final NumberAxis yAxis = countAxis("Units");

        final LineChart<Number, Number> chart = makeChart(xAxis, yAxis);
        chart.getData().addAll(seriesFoodCount, seriesFoodEaten, seriesFoodExpired, seriesFoodSpawned);

        return tab("Food", chart);
    }

    private Tab buildPerformanceTab() {
        final NumberAxis xAxis  = tickAxis();
        final NumberAxis yAxis  = countAxis("ms");
        final NumberAxis yAxis2 = countAxis("MB");

        // Timing chart
        final LineChart<Number, Number> timingChart = makeChart(xAxis, yAxis);
        timingChart.getData().addAll(seriesTotalTickMs, seriesCreaturesMs, seriesFoodMs, seriesMapMs);
        timingChart.setPrefHeight(240);

        // Memory chart
        final NumberAxis xAxis2 = tickAxis();
        final LineChart<Number, Number> memChart = makeChart(xAxis2, yAxis2);
        memChart.getData().add(seriesHeapMb);
        memChart.setPrefHeight(200);

        final VBox box = new VBox(4, timingChart, memChart);
        box.setStyle(BG_DARK);
        box.setPadding(new Insets(8));

        final Tab t = new Tab("Performance");
        t.setContent(box);
        return t;
    }

    // =========================================================================
    // SERIES INIT
    // =========================================================================

    private void initSeries() {
        seriesPopulation   = series("Population",         "#7c9cff");
        seriesPreCreatures = series("Pre-Creatures",      "#fbbf24");

        seriesAvgEnergy    = series("Avg Energy",         "#f0c36d");
        seriesMinEnergy    = series("Min Energy",         "#e05c6a");
        seriesMaxEnergy    = series("Max Energy",         "#6ee7b7");

        seriesFoodCount    = series("On map",             "#5dbcb0");
        seriesFoodEaten    = series("Eaten (cum.)",       "#f0c36d");
        seriesFoodExpired  = series("Expired (cum.)",     "#e05c6a");
        seriesFoodSpawned  = series("Spawned (cum.)",     "#38bdf8");

        seriesTotalTickMs  = series("Total tick ms",      "#f472b6");
        seriesCreaturesMs  = series("updateCreatures ms", "#7c9cff");
        seriesFoodMs       = series("updateFood ms",      "#5dbcb0");
        seriesMapMs        = series("updateMap ms",       "#f0c36d");
        seriesHeapMb       = series("Heap used MB",       "#34d399");
    }

    // =========================================================================
    // REFRESH
    // =========================================================================

    private void startRefresh() {
        refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> refreshCharts()));
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
        refreshCharts(); // primo aggiornamento immediato
    }

    private void stopRefresh() {
        if (refreshTimeline != null) refreshTimeline.stop();
    }

    private void refreshCharts() {
        final List<SimulationSnapshot> snaps = StatsManager.getSnapshotsCopy();
        if (snaps.isEmpty()) return;

        // Sottocampiona se necessario
        final int step = Math.max(1, snaps.size() / MAX_POINTS);

        clearAllSeries();

        int cumEaten = 0, cumExpired = 0, cumSpawned = 0;

        for (int i = 0; i < snaps.size(); i += step) {
            final SimulationSnapshot s = snaps.get(i);
            final int tick = s.tick();

            // Accumula cumulativi fino a questo indice
            for (int j = i; j < Math.min(i + step, snaps.size()); j++) {
                cumEaten   += snaps.get(j).foodEatenThisTick();
                cumExpired += snaps.get(j).foodExpiredThisTick();
                cumSpawned += snaps.get(j).foodSpawnedThisTick();
            }

            // Population
            seriesPopulation.getData().add(point(tick, s.populationCount()));
            seriesPreCreatures.getData().add(point(tick, s.preCreatureCount()));

            // Energy
            var energies = s.creatureEnergies();
            if (!energies.isEmpty()) {
                double avg = energies.stream().mapToDouble(Float::doubleValue).average().orElse(0);
                float  min = energies.stream().min(Float::compare).orElse(0f);
                float  max = energies.stream().max(Float::compare).orElse(0f);
                seriesAvgEnergy.getData().add(point(tick, avg));
                seriesMinEnergy.getData().add(point(tick, min));
                seriesMaxEnergy.getData().add(point(tick, max));
            }

            // Food
            seriesFoodCount.getData().add(point(tick, s.foodCount()));
            seriesFoodEaten.getData().add(point(tick, cumEaten));
            seriesFoodExpired.getData().add(point(tick, cumExpired));
            seriesFoodSpawned.getData().add(point(tick, cumSpawned));

            // Performance
            double tCreatures = s.timeUpdateCreaturesNs() / 1_000_000.0;
            double tFood      = s.timeUpdateFoodNs()      / 1_000_000.0;
            double tMap       = s.timeUpdateMapNs()       / 1_000_000.0;
            double tTotal     = tCreatures + tFood + tMap + s.timeUpdatePreCreaturesNs() / 1_000_000.0;
            double heapMb     = (s.heapTotalBytes() - s.heapFreeBytes()) / (1024.0 * 1024.0);

            seriesTotalTickMs.getData().add(point(tick, tTotal));
            seriesCreaturesMs.getData().add(point(tick, tCreatures));
            seriesFoodMs.getData().add(point(tick, tFood));
            seriesMapMs.getData().add(point(tick, tMap));
            seriesHeapMb.getData().add(point(tick, heapMb));
        }
    }

    private void clearAllSeries() {
        seriesPopulation.getData().clear();
        seriesPreCreatures.getData().clear();
        seriesAvgEnergy.getData().clear();
        seriesMinEnergy.getData().clear();
        seriesMaxEnergy.getData().clear();
        seriesFoodCount.getData().clear();
        seriesFoodEaten.getData().clear();
        seriesFoodExpired.getData().clear();
        seriesFoodSpawned.getData().clear();
        seriesTotalTickMs.getData().clear();
        seriesCreaturesMs.getData().clear();
        seriesFoodMs.getData().clear();
        seriesMapMs.getData().clear();
        seriesHeapMb.getData().clear();
    }

    // =========================================================================
    // FACTORY HELPERS
    // =========================================================================

    private XYChart.Series<Number, Number> series(String name, String hexColor) {
        XYChart.Series<Number, Number> s = new XYChart.Series<>();
        s.setName(name);
        // Il colore viene applicato via CSS dopo che il chart è aggiunto alla scena
        return s;
    }

    private XYChart.Data<Number, Number> point(int x, double y) {
        return new XYChart.Data<>(x, y);
    }

    private NumberAxis tickAxis() {
        NumberAxis ax = new NumberAxis();
        ax.setLabel("Tick");
        ax.setStyle("-fx-tick-label-fill: #6b7280; -fx-font-family: 'Courier New';");
        return ax;
    }

    private NumberAxis countAxis(String label) {
        NumberAxis ax = new NumberAxis();
        ax.setLabel(label);
        ax.setStyle("-fx-tick-label-fill: #6b7280; -fx-font-family: 'Courier New';");
        return ax;
    }

    private LineChart<Number, Number> makeChart(NumberAxis xAxis, NumberAxis yAxis) {
        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setStyle(BG_CHART);
        chart.setCreateSymbols(false);
        chart.setAnimated(false);
        chart.setLegendVisible(true);
        chart.setPadding(new Insets(8));
        return chart;
    }

    private Tab tab(String title, javafx.scene.Node content) {
        VBox box = new VBox(content);
        box.setStyle(BG_DARK);
        box.setPadding(new Insets(8));
        VBox.setVgrow(content, javafx.scene.layout.Priority.ALWAYS);

        Tab t = new Tab(title);
        t.setContent(box);
        return t;
    }

    private void applyChartCss(Scene scene) {
        // CSS inline per colorare le serie e lo sfondo dei chart
        scene.getStylesheets().add(
                "data:text/css," + chartCss().replace(" ", "%20").replace("\n", "")
        );
    }

    private String chartCss() {
        return """
            .chart-plot-background { -fx-background-color: #181c27; }
            .chart-vertical-grid-lines { -fx-stroke: #232840; }
            .chart-horizontal-grid-lines { -fx-stroke: #232840; }
            .chart-alternative-row-fill { -fx-background-color: transparent; }
            .axis { -fx-tick-label-fill: #6b7280; }
            .axis-label { -fx-text-fill: #6b7280; }
            .chart-legend { -fx-background-color: #181c27; }
            .chart-legend-item { -fx-text-fill: #e8eaf6; }
            .tab-pane { -fx-background-color: #0f1117; }
            .tab-pane .tab-header-area { -fx-background-color: #181c27; }
            .tab-pane .tab { -fx-background-color: #232840; }
            .tab-pane .tab:selected { -fx-background-color: #3d5afe; }
            .tab .tab-label { -fx-text-fill: #e8eaf6; -fx-font-family: 'Courier New'; }
            .default-color0.chart-series-line { -fx-stroke: #7c9cff; -fx-stroke-width: 1.8px; }
            .default-color1.chart-series-line { -fx-stroke: #fbbf24; -fx-stroke-width: 1.8px; }
            .default-color2.chart-series-line { -fx-stroke: #5dbcb0; -fx-stroke-width: 1.8px; }
            .default-color3.chart-series-line { -fx-stroke: #f0c36d; -fx-stroke-width: 1.8px; }
            .default-color4.chart-series-line { -fx-stroke: #e05c6a; -fx-stroke-width: 1.8px; }
            .default-color5.chart-series-line { -fx-stroke: #6ee7b7; -fx-stroke-width: 1.8px; }
            .default-color6.chart-series-line { -fx-stroke: #38bdf8; -fx-stroke-width: 1.8px; }
            .default-color7.chart-series-line { -fx-stroke: #f472b6; -fx-stroke-width: 1.8px; }
        """;
    }
}
package ui;

import settings.Settings;
import core.App;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import managers.SimulationManager;
import managers.StatsManager;
import settings.categories.CreatureSettings;
import settings.categories.FoodSettings;
import settings.categories.SimulationSettings;
import settings.categories.WorldSettings;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ui.SimTheme.*;

public class UiManager {

    //UI COMPONENTS
    public static MenuBar createMenuBar(final SimulationManager simulationManager,
                                        final Stage stage) {
        // File
        final Menu fileMenu   = new Menu("File");
        final MenuItem open   = new MenuItem("Open…");
        final MenuItem save   = new MenuItem("Save…");
        final MenuItem export = new MenuItem("Export Data");

        open.setOnAction(e -> {
            final FileChooser fc = new FileChooser();
            fc.setTitle("Load Simulation");
            fc.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Simulation files", "*.sim"));
            final File file = fc.showOpenDialog(stage);
            if (file == null) return;
            SimulationManager loaded = SimulationManager.loadFromFile(file);
            if (loaded != null) {
                simulationManager.stopSimulation();
                simulationManager.loadStateFrom(loaded);
                simulationManager.startSimulation();
            }
        });

        save.setOnAction(e -> {
            final FileChooser fc = new FileChooser();
            fc.setTitle("Save Simulation");
            fc.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Simulation files", "*.sim"));
            fc.setInitialFileName("simulation_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                    + ".sim");
            final File file = fc.showSaveDialog(stage);
            if (file == null) return;
            simulationManager.saveToFile(file);
        });

        export.setOnAction(e -> StatsManager.printFinalReport());
        fileMenu.getItems().addAll(open, save, new SeparatorMenuItem(), export);

        // Configuration
        final Menu configMenu            = new Menu("Config");
        final MenuItem applicationConfig = new MenuItem("Application");
        final MenuItem simulationConfig  = new MenuItem("Simulation");
        final MenuItem worldConfig       = new MenuItem("World");
        final MenuItem creatureConfig    = new MenuItem("Creatures");
        final MenuItem foodConfig        = new MenuItem("Food");

        applicationConfig.setOnAction(e -> openSettingsWindow(stage));
        simulationConfig.setOnAction(e -> openSimulationSettingsWindow(stage));
        worldConfig.setOnAction(e -> openWorldSettingsWindow(stage));
        creatureConfig.setOnAction(e -> openCreatureSettingsWindow(stage));
        foodConfig.setOnAction(e -> openFoodSettingsWindow(stage));
        configMenu.getItems().addAll(
                applicationConfig, simulationConfig,
                new SeparatorMenuItem(), worldConfig, creatureConfig, foodConfig);

        // View
        final Menu viewMenu   = new Menu("View");
        final MenuItem stats  = new MenuItem("Statistics");
        final MenuItem graphs = new MenuItem("Graphs");
        stats.setOnAction(e -> openStatisticsWindow());
        graphs.setOnAction(e -> openChartsWindow());
        viewMenu.getItems().addAll(stats, graphs);

        // State
        final Menu stateMenu    = new Menu("State");
        final MenuItem play     = new MenuItem("Play");
        final MenuItem pause    = new MenuItem("Pause");
        final MenuItem end      = new MenuItem("End");
        final MenuItem restart  = new MenuItem("Restart");
        play.setOnAction(e -> simulationManager.startSimulation());
        pause.setOnAction(e -> simulationManager.pauseSimulation());
        end.setOnAction(e -> simulationManager.stopSimulation());
        restart.setOnAction(e -> simulationManager.restartSimulation());
        stateMenu.getItems().addAll(play, pause, new SeparatorMenuItem(), end, restart);

        return new MenuBar(fileMenu, configMenu, stateMenu, viewMenu);
    }

    public static ToolBar createToolBar(final SimulationManager simulationManager) {
        final Settings settings = App.getSimManager().getSettings();
        final SimulationSettings simulationSettings = settings.getSimulationSettings();

        final Button play    = new Button("▶  Play");
        final Button pause   = new Button("⏸  Pause");
        final Button end     = new Button("■  End");
        final Button restart = new Button("↺  Restart");

        play.setOnAction(e -> simulationManager.startSimulation());
        pause.setOnAction(e -> simulationManager.pauseSimulation());
        end.setOnAction(e -> simulationManager.stopSimulation());
        restart.setOnAction(e -> simulationManager.restartSimulation());

        final Label speedLabel = new Label("SPEED");
        speedLabel.setStyle(FX_FONT + "-fx-font-size:10px; -fx-text-fill:" + C_TEXT_DIM + ";");

        final Slider speed = new Slider(0.1, 10, 1);
        speed.setPrefWidth(120);
        speed.setValue(simulationSettings.speedMultiplier());
        speed.valueProperty().addListener((obs, o, n) ->
                settings.setSimulationSettings(simulationSettings.withSpeedMultiplier(n.floatValue())));

        final Label speedVal = new Label("1.0×");
        speedVal.setStyle(FX_FONT + "-fx-font-size:11px; -fx-text-fill:" + C_TEXT + "; -fx-min-width:36px;");
        speed.valueProperty().addListener((obs, o, n) ->
                speedVal.setText(String.format("%.1f×", n.doubleValue())));

        return new ToolBar(play, pause, end, restart,
                new Separator(), speedLabel, speed, speedVal);
    }

    public static Canvas createDynamicCanvas(final Pane container) {
        final Canvas canvas = new Canvas();
        canvas.widthProperty().bind(container.widthProperty());
        canvas.heightProperty().bind(container.heightProperty());
        return canvas;
    }

    private static GridPane createSettingsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(15));
        return grid;
    }

    private static TextField addRow(GridPane grid, int row, String labelText, String currentValue) {
        grid.add(new Label(labelText), 0, row);
        TextField tf = new TextField(currentValue);
        tf.setPrefWidth(100);
        grid.add(tf, 1, row);
        return tf;
    }

    //WINDOW COMPONENTS
    private static void openStatisticsWindow() {

        new StatisticsWindow().show();

    }

    private static void openChartsWindow() {

        new ChartsWindow().show();

    }

    private static void openSettingsWindow(final Stage mainStage) {
        final Stage stage = buildWindow("Application Settings", 360, 160);

        final CheckBox fullscreen = new CheckBox("Fullscreen mode");
        fullscreen.setSelected(mainStage.isFullScreen());
        fullscreen.setOnAction(e -> mainStage.setFullScreen(fullscreen.isSelected()));

        final VBox layout = panelLayout(fullscreen);
        final Scene scene = buildScene(layout, 360, 160);
        stage.setScene(scene);
        stage.show();
    }

    private static void openSimulationSettingsWindow(final Stage mainStage) {
        final Settings s = App.getSimManager().getSettings();
        final SimulationSettings simulationSettings = s.getSimulationSettings();
        final Stage stage = buildWindow("Simulation Settings", 340, 140);

        final GridPane grid = settingsGrid();
        final TextField speedMultiplier = row(grid, 0, "Speed Multiplier", String.valueOf(simulationSettings.speedMultiplier()));

        final Button apply = applyButton(() -> {
            try {
                s.setSimulationSettings(simulationSettings.withSpeedMultiplier(Float.parseFloat(speedMultiplier.getText())));
            } catch (NumberFormatException ex) {
                showError("Invalid value for Speed Multiplier.");
            }
        });

        stage.setScene(buildScene(panelLayout(sectionLabel("Simulation"), grid, apply), 340, 140));
        stage.show();
    }

    private static void openWorldSettingsWindow(final Stage mainStage) {
        final Settings s = App.getSimManager().getSettings();
        final WorldSettings worldSettings = s.getWorldSettings();
        final Stage stage = buildWindow("World Settings", 340, 290);

        final GridPane grid = settingsGrid();
        final TextField width       = row(grid, 0, "Width",           String.valueOf(worldSettings.width()));
        final TextField height      = row(grid, 1, "Height",          String.valueOf(worldSettings.height()));
        final TextField basePop     = row(grid, 2, "Base Population", String.valueOf(worldSettings.basePopulation()));
        final TextField baseFood    = row(grid, 3, "Base Food",       String.valueOf(worldSettings.baseFood()));
        final TextField foodPerTick = row(grid, 4, "Food Per Tick",   String.valueOf(worldSettings.foodPerTick()));

        final Button apply = applyButton(() -> {
            try {
                s.setWorldSettings(new WorldSettings(
                        Integer.parseInt(width.getText()),
                        Integer.parseInt(height.getText()),
                        Integer.parseInt(basePop.getText()),
                        Integer.parseInt(baseFood.getText()),
                        Float.parseFloat(foodPerTick.getText())
                ));
            } catch (NumberFormatException ex) {
                showError("One or more values are invalid.");
            }
        });

        stage.setScene(buildScene(panelLayout(sectionLabel("World"), grid, apply), 340, 290));
        stage.show();
    }

    private static void openCreatureSettingsWindow(final Stage mainStage) {
        final Settings s = App.getSimManager().getSettings();
        final CreatureSettings creatureSettings = s.getCreatureSettings();
        final Stage stage = buildWindow("Creature Settings", 340, 390);

        final GridPane grid = settingsGrid();
        final TextField baseEnergy            = row(grid, 0, "Base Energy",            String.valueOf(creatureSettings.baseEnergy()));
        final TextField baseHunger            = row(grid, 1, "Base Hunger",            String.valueOf(creatureSettings.baseHunger()));
        final TextField energyLossPerTick     = row(grid, 2, "Energy Loss / Tick",     String.valueOf(creatureSettings.energyLossPerTick()));
        final TextField energyLossPerMove     = row(grid, 3, "Energy Loss / Move",     String.valueOf(creatureSettings.energyLossPerMovement()));
        final TextField reproductionCost      = row(grid, 5, "Reproduction Cost",      String.valueOf(creatureSettings.reproductionCost()));
        final TextField pregnancyTicks        = row(grid, 6, "Pregnancy Ticks",        String.valueOf(creatureSettings.pregnancyTicks()));

        final Button apply = applyButton(() -> {
            try {
                s.setCreatureSettings(new CreatureSettings(
                        Float.parseFloat(baseEnergy.getText()),
                        Float.parseFloat(baseHunger.getText()),
                        Float.parseFloat(energyLossPerTick.getText()),
                        Float.parseFloat(energyLossPerMove.getText()),
                        Float.parseFloat(reproductionCost.getText()),
                        Float.parseFloat(pregnancyTicks.getText())
                        ));
            } catch (NumberFormatException ex) {
                showError("One or more values are invalid.");
            }
        });

        stage.setScene(buildScene(panelLayout(sectionLabel("Creatures"), grid, apply), 340, 390));
        stage.show();
    }

    private static void openFoodSettingsWindow(final Stage mainStage) {
        final Settings s = App.getSimManager().getSettings();
        final FoodSettings foodSettings = s.getFoodSettings();
        final Stage stage = buildWindow("Food Settings", 340, 190);

        final GridPane grid = settingsGrid();
        final TextField baseNutrition    = row(grid, 0, "Base Nutrition",   String.valueOf(foodSettings.baseNutrition()));
        final TextField decaymentPerTick = row(grid, 1, "Decay Per Tick",   String.valueOf(foodSettings.decaymentPerTick()));

        final Button apply = applyButton(() -> {
            try {
                s.setFoodSettings(new FoodSettings(
                        Float.parseFloat(baseNutrition.getText()),
                        Float.parseFloat(decaymentPerTick.getText())
                ));
            } catch (NumberFormatException ex) {
                showError("One or more values are invalid.");
            }
        });

        stage.setScene(buildScene(panelLayout(sectionLabel("Food"), grid, apply), 340, 190));
        stage.show();
    }

    private static Stage buildWindow(final String title, final double w, final double h) {
        final Stage s = new Stage();
        s.setAlwaysOnTop(true);
        s.setTitle(title);
        s.setMinWidth(w);
        s.setMinHeight(h);
        s.setResizable(false);
        return s;
    }

    private static Scene buildScene(final javafx.scene.Parent root,
                                    final double w, final double h) {
        final Scene s = new Scene(root, w, h);
        SimTheme.applyUri(s, SimTheme.buildDataUri());
        return s;
    }

    private static GridPane settingsGrid() {
        final GridPane g = new GridPane();
        g.setHgap(12);
        g.setVgap(8);
        final ColumnConstraints labelCol = new ColumnConstraints();
        labelCol.setMinWidth(160);
        labelCol.setPrefWidth(180);
        final ColumnConstraints fieldCol = new ColumnConstraints();
        fieldCol.setPrefWidth(110);
        g.getColumnConstraints().addAll(labelCol, fieldCol);
        return g;
    }

    private static TextField row(final GridPane grid, final int rowIdx,
                                 final String labelText, final String currentValue) {
        final Label lbl = new Label(labelText);
        lbl.setStyle(FX_FONT + "-fx-font-size:11px; -fx-text-fill:" + C_TEXT_DIM + ";");
        final TextField tf = new TextField(currentValue);
        tf.setPrefWidth(110);
        grid.add(lbl, 0, rowIdx);
        grid.add(tf,  1, rowIdx);
        return tf;
    }

    private static Label sectionLabel(final String text) {
        final Label l = new Label(text.toUpperCase());
        l.setStyle(FX_FONT
                + "-fx-font-size:9px; -fx-font-weight:bold; "
                + "-fx-text-fill:" + C_TEXT_DIM + "; "
                + "-fx-letter-spacing:2px;");
        return l;
    }

    private static Button applyButton(final Runnable action) {
        final Button b = new Button("Apply");
        b.setOnAction(e -> action.run());
        b.setDefaultButton(true);
        return b;
    }

    private static VBox panelLayout(final javafx.scene.Node... nodes) {
        final VBox box = new VBox(12, nodes);
        box.setPadding(new Insets(18));
        box.setStyle(FX_BG_BASE);
        return box;
    }

    static void showError(final String message) {
        final Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setHeaderText("Input Error");
        alert.getDialogPane().getScene().getStylesheets().add(SimTheme.buildDataUri());
        alert.showAndWait();
    }

}

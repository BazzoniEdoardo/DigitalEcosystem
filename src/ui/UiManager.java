package ui;

import configuration.ApplicationConfig;
import configuration.Settings;
import core.App;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import managers.SimulationManager;
import managers.StatsManager;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UiManager {

    //UI COMPONENTS
    public static MenuBar createMenuBar(final SimulationManager simulationManager, final Stage stage) {
        //First Menu
        final Menu fileMenu = new Menu("File");
        final MenuItem open = new MenuItem("Open Simulation");
        final MenuItem save = new MenuItem("Save Simulation");
        final MenuItem exportData = new MenuItem("Export Simulation Data");

        open.setOnAction(e -> {
            final FileChooser fc = new FileChooser();

            fc.setTitle("Load Simulation");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Simulation files", "*.sim"));

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
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Simulation files", "*.sim"));
            fc.setInitialFileName("simulation_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".sim");

            final File file = fc.showSaveDialog(stage);
            if (file == null) return;

            simulationManager.saveToFile(file);
        });

        exportData.setOnAction(e -> StatsManager.printFinalReport());

        fileMenu.getItems().addAll(open, save, exportData);

        //Second Menu
        final Menu configMenu = new Menu("Configuration");
        final MenuItem applicationConfig = new MenuItem("Settings");
        final MenuItem simulationConfig = new MenuItem("Simulation Settings");
        final MenuItem worldConfig = new MenuItem("World Settings");
        final MenuItem creatureConfig = new MenuItem("Creature Settings");
        final MenuItem foodConfig = new MenuItem("Food Settings");

        configMenu.getItems().addAll(applicationConfig, simulationConfig, worldConfig, creatureConfig, foodConfig);

        applicationConfig.setOnAction(e -> openSettingsWindow(stage));
        simulationConfig.setOnAction(e -> openSimulationSettingsWindow(stage));
        worldConfig.setOnAction(e -> openWorldSettingsWindow(stage));
        creatureConfig.setOnAction(e -> openCreatureSettingsWindow(stage));
        foodConfig.setOnAction(e -> openFoodSettingsWindow(stage));

        //Third Menu
        final Menu viewMenu = new Menu("View");
        final MenuItem stats = new MenuItem("Statistics");
        final MenuItem graphs = new MenuItem("Graphs");

        stats.setOnAction(e -> openStatisticsWindow());
        graphs.setOnAction(e -> openChartsWindow());

        viewMenu.getItems().addAll(stats, graphs);

        //Fourth Menu
        final Menu stateMenu = new Menu("State");
        final MenuItem play = new MenuItem("Play");
        final MenuItem pause = new MenuItem("Pause");
        final MenuItem end = new MenuItem("End");
        final MenuItem restart = new MenuItem("Restart");

        play.setOnAction(e -> simulationManager.startSimulation());
        pause.setOnAction(e -> simulationManager.pauseSimulation());
        end.setOnAction(e -> simulationManager.stopSimulation());
        restart.setOnAction(e -> simulationManager.restartSimulation());

        stateMenu.getItems().addAll(play, pause, end, restart);

        return new MenuBar(fileMenu, configMenu, stateMenu, viewMenu);

    }

    public static ToolBar createToolBar(final SimulationManager simulationManager) {

        final Button play = new Button("Play");
        final Button pause = new Button("Pause");
        final Button end = new Button("End");
        final Button restart = new Button("Restart");

        play.setOnAction(e -> simulationManager.startSimulation());
        pause.setOnAction(e -> simulationManager.pauseSimulation());
        end.setOnAction(e -> simulationManager.stopSimulation());
        restart.setOnAction(e -> simulationManager.restartSimulation());

        final Slider speed = new Slider(0.1, 10, 1);
        final Label speedLabel = new Label("Speed");

        speed.setValue(App.getSimManager().getSettings().getSpeedMultiplier());
        speed.valueProperty().addListener((obs, oldVal, newVal) ->
                App.getSimManager().getSettings().setSpeedMultiplier(newVal.floatValue()));


        return new ToolBar(play, pause, end, restart, new Separator(), speedLabel, speed);
    }

    public static Canvas createCanvas() {
        return new Canvas(ApplicationConfig.WIDTH, ApplicationConfig.HEIGHT - 100);
    }

    public static HBox createSidePanel(final SimulationManager simulationManager) {
        final Label currentEntity = new Label("Current Entity: ");

        return new HBox(200, currentEntity);
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

        final Stage stage = new Stage();
        stage.setAlwaysOnTop(true);

        final VBox layoutManager = new VBox();
        layoutManager.getChildren().add(new Label("Statistics here"));

        final Scene scene = new Scene(layoutManager, 720, 405);

        stage.setTitle("Statistics");
        stage.setScene(scene);
        stage.show();

    }

    private static void openChartsWindow() {

        final Stage stage = new Stage();
        stage.setAlwaysOnTop(true);

        final VBox layoutManager = new VBox();
        layoutManager.getChildren().add(new Label("Charts here"));

        final Scene scene = new Scene(layoutManager, 720, 405);

        stage.setTitle("Charts");
        stage.setScene(scene);
        stage.show();

    }

    private static void openSettingsWindow(final Stage mainStage) {
        final Stage stage = new Stage();
        stage.setAlwaysOnTop(true);

        final VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        final CheckBox fullscreen = new CheckBox("Fullscreen");
        fullscreen.setSelected(mainStage.isFullScreen());
        fullscreen.setOnAction(e -> mainStage.setFullScreen(fullscreen.isSelected()));

        layout.getChildren().add(fullscreen);

        stage.setTitle("Settings");
        stage.setScene(new Scene(layout, 400, 200));
        stage.show();
    }

    private static void openSimulationSettingsWindow(final Stage mainStage) {
        final Settings s = App.getSimManager().getSettings();
        final Stage stage = new Stage();
        stage.setAlwaysOnTop(true);

        GridPane grid = createSettingsGrid();

        TextField speedMultiplier = addRow(grid, 0, "Speed Multiplier:", String.valueOf(s.getSpeedMultiplier()));

        Button apply = new Button("Apply");
        apply.setOnAction(e -> {
            try {
                s.setSpeedMultiplier(Float.parseFloat(speedMultiplier.getText()));
            } catch (NumberFormatException ex) {
                showError("Invalid value for Speed Multiplier.");
            }
        });

        VBox layout = new VBox(10, grid, apply);
        layout.setPadding(new Insets(10));

        stage.setTitle("Simulation Settings");
        stage.setScene(new Scene(layout, 350, 150));
        stage.show();
    }

    private static void openWorldSettingsWindow(final Stage mainStage) {
        final Settings s = App.getSimManager().getSettings();
        final Stage stage = new Stage();
        stage.setAlwaysOnTop(true);

        GridPane grid = createSettingsGrid();

        TextField width          = addRow(grid, 0, "Width:",            String.valueOf(s.getWidth()));
        TextField height         = addRow(grid, 1, "Height:",           String.valueOf(s.getHeight()));
        TextField basePopulation = addRow(grid, 2, "Base Population:",  String.valueOf(s.getBasePopulation()));
        TextField baseFood       = addRow(grid, 3, "Base Food:",        String.valueOf(s.getBaseFood()));
        TextField foodPerTick    = addRow(grid, 4, "Food Per Tick:",    String.valueOf(s.getFoodPerTick()));

        Button apply = new Button("Apply");
        apply.setOnAction(e -> {
            try {
                s.setWidth(Integer.parseInt(width.getText()));
                s.setHeight(Integer.parseInt(height.getText()));
                s.setBasePopulation(Integer.parseInt(basePopulation.getText()));
                s.setBaseFood(Integer.parseInt(baseFood.getText()));
                s.setFoodPerTick(Float.parseFloat(foodPerTick.getText()));
            } catch (NumberFormatException ex) {
                showError("One or more values are invalid.");
            }
        });

        VBox layout = new VBox(10, grid, apply);
        layout.setPadding(new Insets(10));

        stage.setTitle("World Settings");
        stage.setScene(new Scene(layout, 350, 280));
        stage.show();
    }

    private static void openCreatureSettingsWindow(final Stage mainStage) {
        final Settings s = App.getSimManager().getSettings();
        final Stage stage = new Stage();
        stage.setAlwaysOnTop(true);

        GridPane grid = createSettingsGrid();

        TextField baseEnergy             = addRow(grid, 0, "Base Energy:",              String.valueOf(s.getBaseEnergy()));
        TextField baseHunger             = addRow(grid, 1, "Base Hunger:",              String.valueOf(s.getBaseHunger()));
        TextField energyLossPerTick      = addRow(grid, 2, "Energy Loss Per Tick:",     String.valueOf(s.getEnergyLossPerTick()));
        TextField energyLossPerMove      = addRow(grid, 3, "Energy Loss Per Move:",     String.valueOf(s.getEnergyLossPerMove()));
        TextField reproductionThreshold  = addRow(grid, 4, "Reproduction Threshold:",  String.valueOf(s.getReproductionThreshold()));
        TextField reproductionCost       = addRow(grid, 5, "Reproduction Cost:",        String.valueOf(s.getReproductionCost()));
        TextField pregnancyTicks         = addRow(grid, 6, "Pregnancy Ticks:",          String.valueOf(s.getPregnancyTicks()));

        Button apply = new Button("Apply");
        apply.setOnAction(e -> {
            try {
                s.setBaseEnergy(Float.parseFloat(baseEnergy.getText()));
                s.setBaseHunger(Float.parseFloat(baseHunger.getText()));
                s.setEnergyLossPerTick(Float.parseFloat(energyLossPerTick.getText()));
                s.setEnergyLossPerMove(Float.parseFloat(energyLossPerMove.getText()));
                s.setReproductionThreshold(Float.parseFloat(reproductionThreshold.getText()));
                s.setReproductionCost(Float.parseFloat(reproductionCost.getText()));
                s.setPregnancyTicks(Float.parseFloat(pregnancyTicks.getText()));
            } catch (NumberFormatException ex) {
                showError("One or more values are invalid.");
            }
        });

        VBox layout = new VBox(10, grid, apply);
        layout.setPadding(new Insets(10));

        stage.setTitle("Creature Settings");
        stage.setScene(new Scene(layout, 350, 370));
        stage.show();
    }

    private static void openFoodSettingsWindow(final Stage mainStage) {
        final Settings s = App.getSimManager().getSettings();
        final Stage stage = new Stage();
        stage.setAlwaysOnTop(true);

        GridPane grid = createSettingsGrid();

        TextField baseNutrition    = addRow(grid, 0, "Base Nutrition:",    String.valueOf(s.getBaseNutrition()));
        TextField decaymentPerTick = addRow(grid, 1, "Decayment Per Tick:", String.valueOf(s.getDecaymentPerTick()));

        Button apply = new Button("Apply");
        apply.setOnAction(e -> {
            try {
                s.setBaseNutrition(Float.parseFloat(baseNutrition.getText()));
                s.setDecaymentPerTick(Float.parseFloat(decaymentPerTick.getText()));
            } catch (NumberFormatException ex) {
                showError("One or more values are invalid.");
            }
        });

        VBox layout = new VBox(10, grid, apply);
        layout.setPadding(new Insets(10));

        stage.setTitle("Food Settings");
        stage.setScene(new Scene(layout, 350, 180));
        stage.show();
    }

    private static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setHeaderText("Input Error");
        alert.showAndWait();
    }
}

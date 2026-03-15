package ui;

import configuration.ApplicationConfig;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
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

        return new ToolBar(play, pause, end, restart, new Separator(), speedLabel, speed);
    }

    public static Canvas createCanvas() {
        return new Canvas(ApplicationConfig.WIDTH, ApplicationConfig.HEIGHT - 100);
    }


    //WINDOW COMPONENTS
    private static void openStatisticsWindow() {

        final Stage stage = new Stage();

        final VBox layoutManager = new VBox();
        layoutManager.getChildren().add(new Label("Statistics here"));

        final Scene scene = new Scene(layoutManager, 720, 405);

        stage.setTitle("Statistics");
        stage.setScene(scene);
        stage.show();

    }

    private static void openChartsWindow() {

        final Stage stage = new Stage();

        final VBox layoutManager = new VBox();
        layoutManager.getChildren().add(new Label("Charts here"));

        final Scene scene = new Scene(layoutManager, 720, 405);

        stage.setTitle("Charts");
        stage.setScene(scene);
        stage.show();

    }
}

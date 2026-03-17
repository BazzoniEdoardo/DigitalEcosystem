package core;

import configuration.ApplicationConfig;
import entities.map.World;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import managers.SimulationManager;
import render.SimulationRenderer;
import ui.SidePanel;
import ui.UiManager;

public class App extends Application {

    //TODO: AGGIUNGERE GRAFICI E STATS, DOPODICHE AGGIUNGERE IL DNA

    private static SimulationManager simManager;

    @Override
    public void start(Stage stage) {

        //Creazione della simulazione
        SimulationManager simulation = new SimulationManager();
        setSimManager(simulation);

        //Manager del layout
        final BorderPane layoutManager = new BorderPane();

        //Creazione della UI
        final Canvas canvas = UiManager.createCanvas();
        final MenuBar menuBar = UiManager.createMenuBar(simulation, stage);
        final ToolBar toolBar = UiManager.createToolBar(simulation);
        SidePanel sidePanel = new SidePanel();

        layoutManager.setTop(menuBar);
        layoutManager.setCenter(canvas);
        layoutManager.setBottom(toolBar);
        layoutManager.setLeft(sidePanel.getRoot());

        final Scene scene = new Scene(layoutManager, ApplicationConfig.WIDTH, ApplicationConfig.HEIGHT);

        stage.setTitle("DigitalEcosystem Simulation");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();

        //Application Loop

        final AnimationTimer timer = getAnimationTimer(canvas, sidePanel, simulation);

        timer.start();

        stage.setOnCloseRequest(e -> simulation.end());

    }

    private static AnimationTimer getAnimationTimer(Canvas canvas, SidePanel sidePanel, SimulationManager simulation) {
        SimulationRenderer renderer = new SimulationRenderer(canvas, sidePanel);

        //Simulation Loop
        Thread simulationThread = new Thread(simulation);
        simulationThread.setDaemon(true);
        simulationThread.start();

        final AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {

                final World world = simulation.getWorld();
                if (world == null) return;

                renderer.render(simulation.getWorld());
            }
        };
        return timer;
    }

    public static void main(String[] args) throws InterruptedException {
        launch();
    }

    private static void setSimManager(final SimulationManager manager) {
        simManager = manager;
    }

    public static SimulationManager getSimManager() {
        return simManager;
    }
}
package core;

import entities.map.World;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import managers.SimulationManager;
import render.SimulationRenderer;
import ui.SidePanel;
import ui.SimTheme;
import ui.UiManager;

public class App extends Application {

    //TODO: AGGIUNGERE GRAFICI E STATS, DOPODICHE AGGIUNGERE IL DNA

    private static SimulationManager simManager;

    @Override
    public void start(final Stage stage) {

        // Creazione della simulazione
        final SimulationManager simulation = new SimulationManager();
        setSimManager(simulation);

        final BorderPane root = new BorderPane();

        final StackPane canvasContainer = new StackPane();
        final Canvas canvas = UiManager.createDynamicCanvas(canvasContainer);
        canvasContainer.getChildren().add(canvas);

        final MenuBar menuBar = UiManager.createMenuBar(simulation, stage);
        final ToolBar toolBar = UiManager.createToolBar(simulation);
        final SidePanel sidePanel = new SidePanel();

        root.setTop(menuBar);
        root.setCenter(canvasContainer);
        root.setBottom(toolBar);
        root.setRight(sidePanel.getRoot());

        final Scene scene = new Scene(root, 1280, 800);
        SimTheme.applyUri(scene, SimTheme.buildDataUri());

        stage.setTitle("DigitalEcosystem Simulation");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();

        final AnimationTimer timer = buildLoop(canvas, sidePanel, simulation);
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

    private static AnimationTimer buildLoop(final Canvas canvas,
                                            final SidePanel sidePanel,
                                            final SimulationManager simulation) {
        final SimulationRenderer renderer = new SimulationRenderer(canvas, sidePanel);

        final Thread simThread = new Thread(simulation);
        simThread.setDaemon(true);
        simThread.start();

        return new AnimationTimer() {
            @Override
            public void handle(final long now) {
                final World world = simulation.getWorld();
                if (world == null) return;
                renderer.render(world);
            }
        };
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
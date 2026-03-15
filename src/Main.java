import configuration.ApplicationConfig;
import entities.World;
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
import ui.UiManager;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        //Creazione della simulazione
        SimulationManager simulation = new SimulationManager();

        //Manager del layout
        final BorderPane layoutManager = new BorderPane();

        //Creazione della UI
        final Canvas canvas = UiManager.createCanvas();
        final MenuBar menuBar = UiManager.createMenuBar(simulation, stage);
        final ToolBar toolBar = UiManager.createToolBar(simulation);

        layoutManager.setTop(menuBar);
        layoutManager.setCenter(canvas);
        layoutManager.setBottom(toolBar);

        final Scene scene = new Scene(layoutManager, ApplicationConfig.WIDTH, ApplicationConfig.HEIGHT);

        stage.setTitle("DigitalEcosystem Simulation");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();

        //Application Loop
        SimulationRenderer renderer = new SimulationRenderer(canvas);

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

        timer.start();

        stage.setOnCloseRequest(e -> simulation.end());

    }

    public static void main(String[] args) throws InterruptedException {
        launch();
    }
}
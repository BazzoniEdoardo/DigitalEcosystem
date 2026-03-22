package managers;

import settings.Settings;
import entities.map.World;

import java.io.*;

public class SimulationManager implements Runnable, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    //Sim attributes
    //TODO: fare in modo che i parametri si possano passare come argomento al programma, cosi' da automatizzare con uno script python
    private boolean initialized = false;
    private volatile boolean running = false;
    private boolean ended = false;
    private int tickCount = 0;

    private World world;

    private final Settings settings;

    private transient Thread simulationThread;

    public SimulationManager() {

        settings = new Settings();
    }

    //MAIN METHODS
    @Override
    public void run() {
        init();

        while (running) {

            running = !world.hasSimulationEnded();

            update();

            tickCount++;

            try {
                Thread.sleep((long) (settings.getSimulationSettings().tickDuration() / settings.getSimulationSettings().speedMultiplier()));
            }catch (InterruptedException e) {
                break;
            }

        }

        //end();
    }

    private void init() {
        if (initialized) return;

        initialized = true;
        running = true;

        StatsManager.startSimulation();

        this.world = new World();
        populateSimulation();
    }

    private synchronized void update() {
        this.world.update();
    }

    public void end() {
        if (ended) return;

        ended = true;
        StatsManager.printFinalReport();
    }

    //GENERATION METHODS

    private void populateSimulation() {
        this.world.populate();
    }

    public boolean isInitialized() {
        return initialized;
    }

    public boolean isRunning() {
        return running;
    }

    public int getTickCount() {
        return tickCount;
    }

    public synchronized World getWorld() {
        return world;
    }

    //PUBLIC METHODS

    public void startSimulation() {
        if (ended) return;

        if (simulationThread == null || !simulationThread.isAlive()) {
            running = true;
            simulationThread = new Thread(this);
            simulationThread.setDaemon(true);
            simulationThread.start();
        }else {
            running = true;
        }
    }

    public void pauseSimulation() {
        running = false;
    }

    public void stopSimulation() {
        running = false;
        ended = true;

        if (simulationThread != null && simulationThread.isAlive()) {
            simulationThread.interrupt();
        }

        end();
    }

    public void restartSimulation() {
        stopSimulation();

        initialized = false;
        ended = false;
        tickCount = 0;

        startSimulation();
    }

    //SAVE AND LOAD
    public void saveToFile(final File file) {
        pauseSimulation();

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this);
        }catch (IOException e) {
            System.err.println("[Save] Errore: " + e.getMessage());
        }
    }

    public static SimulationManager loadFromFile(final File file) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (SimulationManager) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[Load] Errore: " + e.getMessage());
            return null;
        }
    }

    public synchronized void loadStateFrom(final SimulationManager manager) {
        this.world = manager.world;
        this.tickCount = manager.tickCount;
        this.initialized = manager.initialized;
        this.ended = false;
    }

    public Settings getSettings() {
        return settings;
    }
}

package render;

import entities.Food;
import entities.World;
import entities.population.Creature;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public final class SimulationRenderer {

    private final Canvas canvas;
    private final GraphicsContext graphicsContext;

    private double zoom = 1.0;
    private double offsetX = 0;
    private double offsetY = 0;
    private double dragStartX = 0;
    private double dragStartY = 0;

    public SimulationRenderer(final Canvas canvas) {
        this.canvas = canvas;
        this.graphicsContext = canvas.getGraphicsContext2D();

        handleZoom();
        handleDragging();
    }

    public void render(final World world) {
        if (world == null) return;

        graphicsContext.clearRect(0,0,canvas.getWidth(),canvas.getHeight());

        final int worldWidth = world.getWidth();
        final int worldHeight = world.getHeight();

        double cellSize = Math.min(canvas.getWidth() / worldWidth, canvas.getHeight() / worldHeight) * zoom;

        double gridPixelWidth = worldWidth * cellSize;
        double gridPixelHeight = worldHeight * cellSize;

        double baseOffsetX = (canvas.getWidth() - gridPixelWidth) / 2 + offsetX;
        double baseOffsetY = (canvas.getHeight() - gridPixelHeight) / 2 + offsetY;

        for (Creature creature : world.getCreatures()) {
            double drawX = baseOffsetX + creature.getPosition().x() * cellSize;
            double drawY = baseOffsetY + creature.getPosition().y() * cellSize;

            graphicsContext.setFill(Color.BLACK);
            graphicsContext.fillOval(drawX, drawY, cellSize, cellSize);
        }

        for (Food food : world.getFoods()) {
            double drawX = baseOffsetX + food.getPosition().x() * cellSize;
            double drawY = baseOffsetY + food.getPosition().y() * cellSize;

            graphicsContext.setFill(Color.YELLOW);
            graphicsContext.fillOval(drawX, drawY, cellSize, cellSize);
        }
    }

    private void handleZoom() {

        canvas.setOnScroll(event -> {
            if (event.isControlDown()) {
                double zoomFactor = 1.1;

                if (event.getDeltaY() < 0) zoomFactor = 1 / zoomFactor ;

                zoom*= zoomFactor;

                event.consume();
            }
        });
    }

    private void handleDragging() {

        canvas.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown()) {
                dragStartX = event.getX();
                dragStartY = event.getY();
            }
        });

        canvas.setOnMouseDragged(event -> {
            if (event.isPrimaryButtonDown()) {

                final double deltaX = event.getX() - dragStartX;
                final double deltaY = event.getY() - dragStartY;

                offsetX += deltaX;
                offsetY += deltaY;

                dragStartX = event.getX();
                dragStartY = event.getY();

            }
        });
    }

}

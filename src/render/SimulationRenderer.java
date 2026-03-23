package render;

import entities.population.living.Creature;
import entities.population.enviroment.Food;
import entities.map.World;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import render.entities.AbstractRenderedEntity;
import ui.SidePanel;

import java.util.ArrayList;
import java.util.List;

//Fixare gli errori di sincronizzazione tra threads
public final class SimulationRenderer {

    private final Canvas canvas;
    private final GraphicsContext gc;

    // Zoom e pan
    private double zoom       = 1.0;
    private double offsetX    = 0;
    private double offsetY    = 0;
    private double dragStartX = 0;
    private double dragStartY = 0;
    private boolean isDragging = false;

    // Mouse
    private double mouseX = -1;
    private double mouseY = -1;

    // Entità renderizzate nel tick corrente (per hit-test), provare con arraylist normale
    private final List<AbstractRenderedEntity> renderedEntities = new ArrayList<>();

    // Entità attualmente selezionata (click)
    private AbstractRenderedEntity selectedEntity = null;

    // Callback verso la sidebar
    private final SidePanel sidePanel;

    // ── Stile tooltip ──
    private static final Color TOOLTIP_BG     = Color.web("#181c27");
    private static final Color TOOLTIP_BORDER = Color.web("#3d5afe");
    private static final Color TOOLTIP_TEXT   = Color.web("#e8eaf6");
    private static final Color COLOR_HOVER    = Color.web("#ffffff", 0.25);
    private static final Color COLOR_SELECTED = Color.web("#3d5afe", 0.45);

    public SimulationRenderer(final Canvas canvas, final SidePanel sidePanel) {
        this.canvas    = canvas;
        this.gc        = canvas.getGraphicsContext2D();
        this.sidePanel = sidePanel;

        handleZoom();
        handleDragging();
        handleMouse();
    }

    // =========================================================================
    // RENDER
    // =========================================================================

    public synchronized void render(final World world) {
        if (world == null) return;

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        final int worldWidth  = world.getWidth();
        final int worldHeight = world.getHeight();

        final double cellSize      = Math.min(canvas.getWidth() / worldWidth, canvas.getHeight() / worldHeight) * zoom;
        final double gridPixelW    = worldWidth  * cellSize;
        final double gridPixelH    = worldHeight * cellSize;
        final double baseOffsetX   = (canvas.getWidth()  - gridPixelW)  / 2 + offsetX;
        final double baseOffsetY   = (canvas.getHeight() - gridPixelH) / 2 + offsetY;

        final List<Creature> creatures = new ArrayList<>(world.getCreatures());
        final List<Food> foods = new ArrayList<>(world.getFoods());

        renderedEntities.clear();

        // ── Food ──
        for (Food food : foods) {
            double drawX = baseOffsetX + food.getPosition().x() * cellSize;
            double drawY = baseOffsetY + food.getPosition().y() * cellSize;

            food.setX(drawX);
            food.setY(drawY);
            food.setWidth(cellSize);
            food.setHeight(cellSize);

            boolean isSelected = food == selectedEntity;
            boolean isHovered  = !isSelected && food.contains(mouseX, mouseY);

            if (isSelected) {
                gc.setFill(COLOR_SELECTED);
                gc.fillOval(drawX - 2, drawY - 2, cellSize + 4, cellSize + 4);
            } else if (isHovered) {
                gc.setFill(COLOR_HOVER);
                gc.fillOval(drawX - 1, drawY - 1, cellSize + 2, cellSize + 2);
            }

            gc.setFill(Color.web("#f0c36d")); // giallo ambra
            gc.fillOval(drawX, drawY, cellSize, cellSize);

            renderedEntities.add(food);
        }

        // ── Creatures ──
        AbstractRenderedEntity hoveredEntity = null;

        for (Creature creature : creatures) {
            double drawX = baseOffsetX + creature.getPosition().x() * cellSize;
            double drawY = baseOffsetY + creature.getPosition().y() * cellSize;

            creature.setX(drawX);
            creature.setY(drawY);
            creature.setWidth(cellSize);
            creature.setHeight(cellSize);

            boolean isSelected = creature == selectedEntity;
            boolean isHovered  = !isSelected && creature.contains(mouseX, mouseY);

            if (isHovered) hoveredEntity = creature;

            if (isSelected) {
                gc.setFill(COLOR_SELECTED);
                gc.fillOval(drawX - 2, drawY - 2, cellSize + 4, cellSize + 4);
            } else if (isHovered) {
                gc.setFill(COLOR_HOVER);
                gc.fillOval(drawX - 1, drawY - 1, cellSize + 2, cellSize + 2);
            }

            gc.setFill(Color.web("#7c9cff")); // blu ghiaccio
            gc.fillOval(drawX, drawY, cellSize, cellSize);

            renderedEntities.add(creature);
        }

        // ── Hovered food (se nessuna creature è hovered) ──
        if (hoveredEntity == null) {
            for (AbstractRenderedEntity e : renderedEntities) {
                if (e instanceof Food && e.contains(mouseX, mouseY)) {
                    hoveredEntity = e;
                    break;
                }
            }
        }

        // ── Tooltip sull'entity hovered ──
        if (hoveredEntity != null) {
            drawTooltip(hoveredEntity, cellSize);
        }

        // ── Aggiorna sidebar se c'è un'entità selezionata ──
        if (selectedEntity != null) {
            // Verifica che l'entità sia ancora viva/presente nel mondo
            boolean stillPresent = renderedEntities.contains(selectedEntity);
            if (stillPresent) {
                sidePanel.updateEntity(selectedEntity);
            } else {
                selectedEntity = null;
                sidePanel.clear();
            }
        }
    }

    // =========================================================================
    // TOOLTIP
    // =========================================================================

    private void drawTooltip(final AbstractRenderedEntity entity, final double cellSize) {
        final String label = entity.getEntityTypeName() + " #" + entity.getEntity().getId();

        gc.setFont(Font.font("Courier New", Math.max(10, cellSize * 0.7)));
        final double textW = label.length() * gc.getFont().getSize() * 0.6;
        final double textH = gc.getFont().getSize();

        final double pad   = 4;
        final double boxW  = textW + pad * 2;
        final double boxH  = textH + pad * 2;

        // Posiziona il tooltip a destra dell'entità, con correzione bordo canvas
        double tx = entity.getX() + cellSize + 4;
        double ty = entity.getY() - boxH / 2;
        if (tx + boxW > canvas.getWidth())  tx = entity.getX() - boxW - 4;
        if (ty < 0)                          ty = 0;
        if (ty + boxH > canvas.getHeight()) ty = canvas.getHeight() - boxH;

        // Sfondo
        gc.setFill(TOOLTIP_BG);
        gc.fillRoundRect(tx, ty, boxW, boxH, 4, 4);

        // Bordo
        gc.setStroke(TOOLTIP_BORDER);
        gc.setLineWidth(1);
        gc.strokeRoundRect(tx, ty, boxW, boxH, 4, 4);

        // Testo
        gc.setFill(TOOLTIP_TEXT);
        gc.fillText(label, tx + pad, ty + pad + textH * 0.85);
    }

    // =========================================================================
    // INPUT HANDLERS
    // =========================================================================

    private void handleMouse() {
        // Traccia posizione mouse per hover
        canvas.setOnMouseMoved(event -> {
            mouseX = event.getX();
            mouseY = event.getY();
        });

        // Click: seleziona entità
        canvas.setOnMouseClicked(event -> {
            if (isDragging) return; // ignora click se era un drag

            final double mx = event.getX();
            final double my = event.getY();

            AbstractRenderedEntity clicked = null;

            // Creatures hanno priorità visiva (sono sopra il food)
            for (AbstractRenderedEntity e : renderedEntities) {
                if (e instanceof Creature && e.contains(mx, my)) {
                    clicked = e;
                    break;
                }
            }
            // Se non c'è una creature, cerca food
            if (clicked == null) {
                for (AbstractRenderedEntity e : renderedEntities) {
                    if (e instanceof Food && e.contains(mx, my)) {
                        clicked = e;
                        break;
                    }
                }
            }

            if (clicked != null) {
                selectedEntity = clicked;
                sidePanel.showEntity(selectedEntity);
            } else {
                // Click nel vuoto: deseleziona
                selectedEntity = null;
                sidePanel.clear();
            }
        });
    }

    private void handleZoom() {
        canvas.setOnScroll(event -> {
            if (event.isControlDown()) {
                double factor = event.getDeltaY() < 0 ? 1 / 1.1 : 1.1;
                zoom *= factor;
                event.consume();
            }
        });
    }

    private void handleDragging() {
        canvas.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown()) {
                dragStartX = event.getX();
                dragStartY = event.getY();
                isDragging = false;
            }
        });

        canvas.setOnMouseDragged(event -> {
            if (event.isPrimaryButtonDown()) {
                double dx = event.getX() - dragStartX;
                double dy = event.getY() - dragStartY;

                // Considera drag solo se il mouse si è spostato abbastanza
                if (Math.abs(dx) > 3 || Math.abs(dy) > 3) isDragging = true;

                offsetX    += dx;
                offsetY    += dy;
                dragStartX  = event.getX();
                dragStartY  = event.getY();
            }
        });

        canvas.setOnMouseReleased(event -> {
            // Reset isDragging dopo un breve delay (il click arriva dopo il released)
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(
                    javafx.util.Duration.millis(50));
            pause.setOnFinished(e -> isDragging = false);
            pause.play();
        });
    }
}
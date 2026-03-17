package ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import render.entities.AbstractRenderedEntity;

import java.util.Map;

/**
 * Sidebar che mostra le info dell'entità selezionata nel canvas.
 * Viene aggiornata dal SimulationRenderer tramite showEntity() e clear().
 * Thread-safe: usa Platform.runLater per aggiornamenti dall'AnimationTimer.
 */
public class SidePanel {

    private static final String BG_DARK   = "-fx-background-color: #0f1117;";
    private static final String BG_CARD   = "-fx-background-color: #181c27; -fx-background-radius: 6;";
    private static final String FONT_MONO = "-fx-font-family: 'Courier New';";

    private static final String COLOR_TEXT    = "#e8eaf6";
    private static final String COLOR_DIM     = "#6b7280";
    private static final String COLOR_ACCENT  = "#3d5afe";
    private static final String COLOR_CREATURE = "#7c9cff";
    private static final String COLOR_FOOD    = "#5dbcb0";
    private static final String COLOR_BORDER  = "#232840";

    private final VBox root;
    private final VBox contentBox;
    private final Label titleLabel;
    private final Label noSelectionLabel;

    public SidePanel() {
        root = new VBox(8);
        root.setStyle(BG_DARK);
        root.setPadding(new Insets(12, 10, 12, 10));
        root.setPrefWidth(180);
        root.setMinWidth(160);

        // Header fisso
        Label header = new Label("ENTITY INFO");
        header.setStyle(FONT_MONO
                + "-fx-font-size: 10; -fx-font-weight: bold; "
                + "-fx-text-fill: " + COLOR_DIM + "; -fx-letter-spacing: 1;");

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: " + COLOR_BORDER + ";");

        // Titolo entità selezionata (tipo + id)
        titleLabel = new Label("—");
        titleLabel.setStyle(FONT_MONO
                + "-fx-font-size: 13; -fx-font-weight: bold; "
                + "-fx-text-fill: " + COLOR_TEXT + ";");
        titleLabel.setWrapText(true);

        // Placeholder quando niente è selezionato
        noSelectionLabel = new Label("Click an entity\nto see its info.");
        noSelectionLabel.setStyle(FONT_MONO
                + "-fx-font-size: 11; -fx-text-fill: " + COLOR_DIM + ";");
        noSelectionLabel.setAlignment(Pos.CENTER);
        noSelectionLabel.setWrapText(true);

        // Contenuto dinamico (righe info)
        contentBox = new VBox(4);
        contentBox.setStyle(BG_CARD);
        contentBox.setPadding(new Insets(10, 10, 10, 10));
        contentBox.setVisible(false);

        ScrollPane scroll = new ScrollPane(contentBox);
        scroll.setStyle("-fx-background: #0f1117; -fx-background-color: #0f1117;");
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        root.getChildren().addAll(header, sep, titleLabel, noSelectionLabel, scroll);
    }

    /**
     * Mostra le info di un'entità nella sidebar.
     * Può essere chiamato da qualsiasi thread.
     */
    public void showEntity(final AbstractRenderedEntity entity) {
        Platform.runLater(() -> {
            final Map<String, String> info = entity.getInfo();
            final String typeName = entity.getEntityTypeName();
            final String idVal    = info.getOrDefault("ID", "?");
            final String color    = typeName.equals("Creature") ? COLOR_CREATURE : COLOR_FOOD;

            titleLabel.setText(typeName + " #" + idVal);
            titleLabel.setStyle(FONT_MONO
                    + "-fx-font-size: 13; -fx-font-weight: bold; "
                    + "-fx-text-fill: " + color + ";");

            contentBox.getChildren().clear();

            for (Map.Entry<String, String> entry : info.entrySet()) {
                if (entry.getKey().equals("ID")) continue; // già nel titolo

                VBox row = buildRow(entry.getKey(), entry.getValue());
                contentBox.getChildren().add(row);
            }

            noSelectionLabel.setVisible(false);
            contentBox.setVisible(true);
        });
    }

    /**
     * Aggiorna i valori di un'entità già selezionata senza ricostruire la struttura.
     * Chiamato ogni tick mentre l'entità è ancora selezionata.
     */
    public void updateEntity(final AbstractRenderedEntity entity) {
        Platform.runLater(() -> {
            final Map<String, String> info = entity.getInfo();
            int rowIdx = 0;

            for (Map.Entry<String, String> entry : info.entrySet()) {
                if (entry.getKey().equals("ID")) continue;

                if (rowIdx < contentBox.getChildren().size()) {
                    VBox row = (VBox) contentBox.getChildren().get(rowIdx);
                    // Il valore è la seconda Label dentro il VBox
                    if (row.getChildren().size() >= 2) {
                        ((Label) row.getChildren().get(1)).setText(entry.getValue());
                    }
                }
                rowIdx++;
            }
        });
    }

    /**
     * Svuota la sidebar (nessuna entità selezionata).
     */
    public void clear() {
        Platform.runLater(() -> {
            titleLabel.setText("—");
            titleLabel.setStyle(FONT_MONO
                    + "-fx-font-size: 13; -fx-font-weight: bold; "
                    + "-fx-text-fill: " + COLOR_TEXT + ";");
            contentBox.getChildren().clear();
            contentBox.setVisible(false);
            noSelectionLabel.setVisible(true);
        });
    }

    /**
     * Restituisce il nodo root da inserire nel layout.
     */
    public VBox getRoot() {
        return root;
    }

    // =========================================================================
    // HELPERS
    // =========================================================================

    private VBox buildRow(String key, String value) {
        Label keyLabel = new Label(key);
        keyLabel.setStyle(FONT_MONO
                + "-fx-font-size: 9; -fx-text-fill: " + COLOR_DIM + "; "
                + "-fx-letter-spacing: 0.5;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle(FONT_MONO
                + "-fx-font-size: 12; -fx-font-weight: bold; "
                + "-fx-text-fill: " + COLOR_TEXT + ";");
        valueLabel.setWrapText(true);

        VBox row = new VBox(1, keyLabel, valueLabel);
        row.setPadding(new Insets(0, 0, 6, 0));

        // Separatore sottile sotto ogni riga
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: " + COLOR_BORDER + "; -fx-opacity: 0.5;");
        row.getChildren().add(sep);

        return row;
    }
}
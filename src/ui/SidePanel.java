package ui;

import entities.population.living.Creature;
import entities.population.living.genetics.DNA;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import render.entities.AbstractRenderedEntity;

import java.util.LinkedHashMap;
import java.util.Map;

import static ui.SimTheme.*;

/**
 * Sidebar that shows info for a selected entity.
 *
 * Key design decision:
 *   - Every value-Label stores its exact map-key in userData.
 *   - updateEntity() walks all labels and refreshes by key lookup — no index math.
 *   - Gene sections are rebuilt only on showEntity(); they don't change per-tick.
 */
public class SidePanel {

    private final VBox  root;
    private final VBox  contentBox;
    private final Label titleLabel;
    private final Label hintLabel;

    public SidePanel() {
        root = new VBox(0);
        root.setStyle(FX_BG_BASE);
        root.setPrefWidth(210);
        root.setMinWidth(190);

        // ── Fixed header ──────────────────────────────────────────────
        final Label header = new Label("ENTITY INFO");
        header.setStyle(FX_FONT
                + "-fx-font-size:9px; -fx-font-weight:bold;"
                + "-fx-text-fill:" + C_TEXT_DIM + ";"
                + "-fx-letter-spacing:2px;");
        header.setPadding(new Insets(12, 12, 8, 12));

        // ── Entity title ──────────────────────────────────────────────
        titleLabel = new Label("—");
        titleLabel.setStyle(FX_FONT
                + "-fx-font-size:13px; -fx-font-weight:bold;"
                + "-fx-text-fill:" + C_TEXT + ";");
        titleLabel.setPadding(new Insets(10, 12, 8, 12));
        titleLabel.setWrapText(true);

        // ── Placeholder ───────────────────────────────────────────────
        hintLabel = new Label("Click an entity\nto inspect it.");
        hintLabel.setStyle(FX_FONT
                + "-fx-font-size:11px; -fx-text-fill:" + C_TEXT_DIM + ";");
        hintLabel.setAlignment(Pos.CENTER);
        hintLabel.setPadding(new Insets(24, 12, 0, 12));

        // ── Scrollable content ────────────────────────────────────────
        contentBox = new VBox(0);
        contentBox.setVisible(false);

        final ScrollPane scroll = new ScrollPane(contentBox);
        scroll.setStyle(FX_BG_BASE + "-fx-background:" + C_BG_BASE + ";");
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        root.getChildren().addAll(header, hairline(), titleLabel, hintLabel, scroll);
    }

    // =========================================================================
    // PUBLIC API
    // =========================================================================

    /**
     * Full rebuild — call when a new entity is selected.
     */
    public void showEntity(final AbstractRenderedEntity entity) {
        Platform.runLater(() -> {
            contentBox.getChildren().clear();

            final Map<String, String> info      = entity.getInfo();
            final String             typeName   = entity.getEntityTypeName();
            final String             id         = info.getOrDefault("ID", "?");
            final boolean            isCreature = "Creature".equals(typeName);

            // Title
            titleLabel.setText(typeName + " #" + id);
            titleLabel.setStyle(FX_FONT
                    + "-fx-font-size:13px; -fx-font-weight:bold;"
                    + "-fx-text-fill:" + (isCreature ? C_ACCENT : C_OK) + ";");

            // ── CORE section ──────────────────────────────────────────
            // Copy the map and drop ID — it's already in the title.
            final Map<String, String> coreFields = new LinkedHashMap<>(info);
            coreFields.remove("ID");
            // buildSection tags every value-label with its exact map key.
            contentBox.getChildren().add(buildSection("CORE", coreFields));

            // ── Gene sections (Creature only) ─────────────────────────
            if (isCreature) {
                try {
                    final Creature creature = (Creature) entity.getEntity();
                    final DNA      dna      = creature.getDna();
                    contentBox.getChildren().add(buildGeneSection("BEHAVIOUR",    dna.getBehaviourGene().getGenes()));
                    contentBox.getChildren().add(buildGeneSection("METABOLISM",   dna.getMetabolismGene().getGenes()));
                    contentBox.getChildren().add(buildGeneSection("MOVEMENT",     dna.getMovementGene().getGenes()));
                    contentBox.getChildren().add(buildGeneSection("PERCEPTION",   dna.getPerceptionGene().getGenes()));
                    contentBox.getChildren().add(buildGeneSection("REPRODUCTION", dna.getReproductionGene().getGenes()));
                } catch (Exception ignored) { }
            }

            hintLabel.setVisible(false);
            contentBox.setVisible(true);
        });
    }

    /**
     * Per-tick refresh — only updates CORE values (the ones from getInfo()).
     * Gene values don't change tick-to-tick in a meaningful way so we skip them.
     * Uses userData key lookup — completely index-independent.
     */
    public void updateEntity(final AbstractRenderedEntity entity) {
        Platform.runLater(() -> {
            if (contentBox.getChildren().isEmpty()) return;

            final Map<String, String> info = entity.getInfo();

            // Walk every node in the entire contentBox tree looking for
            // value-Labels whose userData matches a key in info.
            for (final Node sectionNode : contentBox.getChildren()) {
                if (!(sectionNode instanceof VBox section)) continue;
                for (final Node child : section.getChildren()) {
                    if (!(child instanceof VBox row)) continue;
                    // row layout: [keyLabel(index 0), valueLabel(index 1), ...]
                    if (row.getChildren().size() < 2) continue;
                    final Node second = row.getChildren().get(1);
                    if (!(second instanceof Label valLabel)) continue;
                    final Object tag = valLabel.getUserData();
                    if (tag instanceof String key && info.containsKey(key)) {
                        valLabel.setText(info.get(key));
                    }
                }
            }
        });
    }

    /** Clear sidebar to empty / no-selection state. */
    public void clear() {
        Platform.runLater(() -> {
            titleLabel.setText("—");
            titleLabel.setStyle(FX_FONT
                    + "-fx-font-size:13px; -fx-font-weight:bold;"
                    + "-fx-text-fill:" + C_TEXT + ";");
            contentBox.getChildren().clear();
            contentBox.setVisible(false);
            hintLabel.setVisible(true);
        });
    }

    public VBox getRoot() { return root; }

    // =========================================================================
    // SECTION BUILDERS
    // =========================================================================

    /**
     * Generic section from a String→String map.
     * Each value-Label gets userData = the exact map key passed in.
     */
    private VBox buildSection(final String title, final Map<String, String> fields) {
        final VBox section = new VBox(0);
        section.getChildren().add(sectionHeader(title));
        section.getChildren().add(hairline());
        for (Map.Entry<String, String> e : fields.entrySet()) {
            // tag = exact key from the map (e.g. "Position", "Energy", …)
            section.getChildren().add(buildRow(e.getKey(), e.getValue(), e.getKey()));
        }
        return section;
    }

    /**
     * Gene section from a String→Float map.
     * Value-Labels are tagged with the camelCase gene attribute name so that
     * future live-updates can find them if needed.
     */
    private VBox buildGeneSection(final String title, final Map<String, Float> attrs) {
        final VBox section = new VBox(0);
        section.getChildren().add(sectionHeader(title));
        section.getChildren().add(hairline());
        for (Map.Entry<String, Float> e : attrs.entrySet()) {
            final String displayKey = camelToLabel(e.getKey());
            final String value      = String.format("%.4f", e.getValue());
            // tag = camelCase original key (unique within the gene's map)
            section.getChildren().add(buildRow(displayKey, value, e.getKey()));
        }
        return section;
    }

    // =========================================================================
    // ROW / LABEL HELPERS
    // =========================================================================

    /**
     * @param displayKey  The label text shown on the left.
     * @param value       The value text shown on the right / below.
     * @param tagKey      Stored in valueLabel.userData for later lookup.
     */
    private VBox buildRow(final String displayKey, final String value, final String tagKey) {
        final Label keyLabel = new Label(displayKey);
        keyLabel.setStyle(FX_FONT
                + "-fx-font-size:9px;"
                + "-fx-text-fill:" + C_TEXT_DIM + ";");

        final Label valueLabel = new Label(value);
        valueLabel.setStyle(FX_FONT
                + "-fx-font-size:12px; -fx-font-weight:bold;"
                + "-fx-text-fill:" + C_TEXT + ";");
        valueLabel.setWrapText(true);
        valueLabel.setUserData(tagKey);   // ← the key used by updateEntity()

        final VBox row = new VBox(2, keyLabel, valueLabel);
        row.setPadding(new Insets(5, 12, 5, 12));
        row.setStyle("-fx-border-color:transparent transparent "
                + C_BORDER + " transparent; -fx-border-width:0 0 1px 0;");
        return row;
    }

    private Label sectionHeader(final String title) {
        final Label l = new Label(title);
        l.setStyle(FX_FONT
                + "-fx-font-size:9px; -fx-font-weight:bold;"
                + "-fx-text-fill:" + C_TEXT_DIM + ";"
                + "-fx-letter-spacing:1.5px;");
        l.setPadding(new Insets(10, 12, 4, 12));
        return l;
    }

    private Separator hairline() {
        final Separator s = new Separator();
        s.setStyle("-fx-background-color:" + C_BORDER + "; -fx-pref-height:1px;");
        return s;
    }

    /** "reproductionThreshold" → "Reproduction Threshold" */
    private static String camelToLabel(final String camel) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < camel.length(); i++) {
            final char c = camel.charAt(i);
            if (Character.isUpperCase(c) && i > 0) sb.append(' ');
            sb.append(i == 0 ? Character.toUpperCase(c) : c);
        }
        return sb.toString();
    }
}
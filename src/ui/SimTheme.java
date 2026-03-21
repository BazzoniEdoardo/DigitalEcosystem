package ui;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Single source of truth for every colour, font, and CSS snippet.
 *
 * Palette:
 *   BG_BASE   #0a0b0e   deepest background
 *   BG_PANEL  #111318   panel / card surface
 *   BG_HOVER  #181c24   hover / raised surface
 *   BORDER    #1e2330   subtle separator
 *   TEXT      #d4d8e2   primary text
 *   TEXT_DIM  #4a5068   secondary / labels
 *   ACCENT    #4f7cff   single accent
 *   WARN      #e05c6a   danger / death
 *   OK        #4ecb8d   success / birth
 *   GOLD      #c8a84b   energy / warmth
 */
public final class SimTheme {

    private SimTheme() {}

    public static final String C_BG_BASE   = "#0a0b0e";
    public static final String C_BG_PANEL  = "#111318";
    public static final String C_BG_HOVER  = "#181c24";
    public static final String C_BORDER    = "#1e2330";
    public static final String C_TEXT      = "#d4d8e2";
    public static final String C_TEXT_DIM  = "#4a5068";
    public static final String C_ACCENT    = "#4f7cff";
    public static final String C_WARN      = "#e05c6a";
    public static final String C_OK        = "#4ecb8d";
    public static final String C_GOLD      = "#c8a84b";

    public static final String FX_FONT     = "-fx-font-family: 'JetBrains Mono', 'Courier New', monospace;";
    public static final String FX_BG_BASE  = "-fx-background-color: " + C_BG_BASE  + ";";
    public static final String FX_BG_PANEL = "-fx-background-color: " + C_BG_PANEL + ";";
    public static final String FX_BG_HOVER = "-fx-background-color: " + C_BG_HOVER + ";";

    // CSS is written once to a temp file; all scenes load it via file:// URI.
    private static String cachedUri = null;

    /**
     * Returns a file:// URI for the theme CSS.
     * Created once on first call, reused for the JVM lifetime.
     */
    public static String getStylesheetUri() {
        if (cachedUri != null) return cachedUri;
        try {
            final Path tmp = Files.createTempFile("simtheme-", ".css");
            tmp.toFile().deleteOnExit();
            try (PrintWriter pw = new PrintWriter(tmp.toFile(), StandardCharsets.UTF_8)) {
                pw.print(css());
            }
            cachedUri = tmp.toUri().toString();
        } catch (IOException e) {
            System.err.println("[SimTheme] Failed to write CSS temp file: " + e.getMessage());
            cachedUri = "";
        }
        return cachedUri;
    }

    /** Apply the unified theme to any Scene. */
    public static void apply(final javafx.scene.Scene scene) {
        final String uri = getStylesheetUri();
        if (uri != null && !uri.isEmpty()) {
            scene.getStylesheets().clear();
            scene.getStylesheets().add(uri);
        }
    }

    // Keep old call-site names as aliases so nothing else breaks.
    @Deprecated public static void applyUri(javafx.scene.Scene s, String ignored) { apply(s); }
    @Deprecated public static String buildDataUri() { return getStylesheetUri(); }

    // =========================================================================
    // CSS — written as plain Java strings (no text blocks) to avoid
    // any encoding / escape-sequence issues with the JavaFX CSS parser.
    // =========================================================================
    public static String css() {
        final StringBuilder sb = new StringBuilder();

        // Base
        sb.append(".root {");
        sb.append("-fx-background-color:#0a0b0e;");
        sb.append("-fx-font-family:'JetBrains Mono','Courier New',monospace;");
        sb.append("-fx-font-size:12px;");
        sb.append("}");

        // Menu bar
        sb.append(".menu-bar {");
        sb.append("-fx-background-color:#111318;");
        sb.append("-fx-border-color:transparent transparent #1e2330 transparent;");
        sb.append("-fx-border-width:1px;");
        sb.append("-fx-padding:2 8 2 8;");
        sb.append("}");
        sb.append(".menu-bar .label {");
        sb.append("-fx-text-fill:#d4d8e2;");
        sb.append("-fx-font-family:'JetBrains Mono','Courier New',monospace;");
        sb.append("-fx-font-size:12px;");
        sb.append("}");
        sb.append(".menu { -fx-background-color:transparent; }");
        sb.append(".menu:hover,.menu:focused,.menu:showing {");
        sb.append("-fx-background-color:#1e2330;");
        sb.append("-fx-background-radius:3px;");
        sb.append("}");
        sb.append(".context-menu {");
        sb.append("-fx-background-color:#111318;");
        sb.append("-fx-border-color:#1e2330;");
        sb.append("-fx-border-width:1px;");
        sb.append("-fx-border-radius:4px;");
        sb.append("-fx-background-radius:4px;");
        sb.append("-fx-padding:4 0 4 0;");
        sb.append("}");
        sb.append(".menu-item {");
        sb.append("-fx-background-color:transparent;");
        sb.append("-fx-padding:5 16 5 12;");
        sb.append("}");
        sb.append(".menu-item .label {");
        sb.append("-fx-text-fill:#d4d8e2;");
        sb.append("-fx-font-family:'JetBrains Mono','Courier New',monospace;");
        sb.append("-fx-font-size:12px;");
        sb.append("}");
        sb.append(".menu-item:focused { -fx-background-color:#1e2330; }");
        sb.append(".menu-item:focused .label { -fx-text-fill:#4f7cff; }");
        sb.append(".separator .line {");
        sb.append("-fx-border-color:#1e2330;");
        sb.append("-fx-border-width:1px 0 0 0;");
        sb.append("}");

        // Tool bar
        sb.append(".tool-bar {");
        sb.append("-fx-background-color:#111318;");
        sb.append("-fx-border-color:transparent transparent #1e2330 transparent;");
        sb.append("-fx-border-width:1px;");
        sb.append("-fx-padding:4 8 4 8;");
        sb.append("-fx-spacing:4px;");
        sb.append("}");
        sb.append(".tool-bar .button {");
        sb.append("-fx-background-color:#1e2330;");
        sb.append("-fx-text-fill:#d4d8e2;");
        sb.append("-fx-font-family:'JetBrains Mono','Courier New',monospace;");
        sb.append("-fx-font-size:11px;");
        sb.append("-fx-padding:4 12 4 12;");
        sb.append("-fx-background-radius:3px;");
        sb.append("-fx-cursor:hand;");
        sb.append("}");
        sb.append(".tool-bar .button:hover {");
        sb.append("-fx-background-color:#2a3050;");
        sb.append("-fx-text-fill:#4f7cff;");
        sb.append("}");
        sb.append(".tool-bar .button:pressed {");
        sb.append("-fx-background-color:#4f7cff;");
        sb.append("-fx-text-fill:#0a0b0e;");
        sb.append("}");
        sb.append(".tool-bar .label {");
        sb.append("-fx-text-fill:#4a5068;");
        sb.append("-fx-font-size:11px;");
        sb.append("}");

        // Slider
        sb.append(".slider .track {");
        sb.append("-fx-background-color:#1e2330;");
        sb.append("-fx-background-radius:2px;");
        sb.append("-fx-pref-height:3px;");
        sb.append("}");
        sb.append(".slider .thumb {");
        sb.append("-fx-background-color:#4f7cff;");
        sb.append("-fx-background-radius:50%;");
        sb.append("-fx-pref-width:12px;");
        sb.append("-fx-pref-height:12px;");
        sb.append("-fx-effect:none;");
        sb.append("}");
        sb.append(".slider .thumb:hover { -fx-background-color:#7fa0ff; }");

        // Scroll pane
        sb.append(".scroll-pane {");
        sb.append("-fx-background-color:#0a0b0e;");
        sb.append("-fx-background:#0a0b0e;");
        sb.append("-fx-border-color:transparent;");
        sb.append("}");
        sb.append(".scroll-pane > .viewport { -fx-background-color:#0a0b0e; }");
        sb.append(".scroll-bar {");
        sb.append("-fx-background-color:#111318;");
        sb.append("-fx-pref-width:6px;");
        sb.append("-fx-pref-height:6px;");
        sb.append("}");
        sb.append(".scroll-bar .thumb {");
        sb.append("-fx-background-color:#1e2330;");
        sb.append("-fx-background-radius:3px;");
        sb.append("}");
        sb.append(".scroll-bar .thumb:hover { -fx-background-color:#2a3050; }");
        sb.append(".scroll-bar .increment-button,");
        sb.append(".scroll-bar .decrement-button {");
        sb.append("-fx-background-color:transparent;");
        sb.append("-fx-pref-height:0;");
        sb.append("-fx-pref-width:0;");
        sb.append("}");

        // Button
        sb.append(".button {");
        sb.append("-fx-background-color:#1e2330;");
        sb.append("-fx-text-fill:#d4d8e2;");
        sb.append("-fx-font-family:'JetBrains Mono','Courier New',monospace;");
        sb.append("-fx-font-size:11px;");
        sb.append("-fx-padding:5 14 5 14;");
        sb.append("-fx-background-radius:3px;");
        sb.append("-fx-cursor:hand;");
        sb.append("}");
        sb.append(".button:hover {");
        sb.append("-fx-background-color:#2a3050;");
        sb.append("-fx-border-color:#4f7cff;");
        sb.append("-fx-text-fill:#4f7cff;");
        sb.append("}");
        sb.append(".button:pressed {");
        sb.append("-fx-background-color:#4f7cff;");
        sb.append("-fx-text-fill:#0a0b0e;");
        sb.append("}");

        // TextField
        sb.append(".text-field {");
        sb.append("-fx-background-color:#0a0b0e;");
        sb.append("-fx-text-fill:#d4d8e2;");
        sb.append("-fx-font-family:'JetBrains Mono','Courier New',monospace;");
        sb.append("-fx-font-size:12px;");
        sb.append("-fx-border-color:#1e2330;");
        sb.append("-fx-border-width:1px;");
        sb.append("-fx-border-radius:3px;");
        sb.append("-fx-background-radius:3px;");
        sb.append("-fx-padding:4 8 4 8;");
        sb.append("-fx-highlight-fill:#4f7cff;");
        sb.append("}");
        sb.append(".text-field:focused { -fx-border-color:#4f7cff; }");

        // CheckBox
        sb.append(".check-box .box {");
        sb.append("-fx-background-color:#0a0b0e;");
        sb.append("-fx-border-color:#1e2330;");
        sb.append("-fx-border-radius:2px;");
        sb.append("-fx-background-radius:2px;");
        sb.append("-fx-pref-width:14px;");
        sb.append("-fx-pref-height:14px;");
        sb.append("}");
        sb.append(".check-box:selected .box {");
        sb.append("-fx-background-color:#4f7cff;");
        sb.append("-fx-border-color:#4f7cff;");
        sb.append("}");
        sb.append(".check-box .mark { -fx-background-color:#0a0b0e; }");
        sb.append(".check-box .label { -fx-text-fill:#d4d8e2; -fx-font-size:12px; }");

        // Label
        sb.append(".label {");
        sb.append("-fx-text-fill:#d4d8e2;");
        sb.append("-fx-font-family:'JetBrains Mono','Courier New',monospace;");
        sb.append("}");

        // Tab pane
        sb.append(".tab-pane {");
        sb.append("-fx-background-color:#0a0b0e;");
        sb.append("-fx-tab-min-width:100px;");
        sb.append("}");
        sb.append(".tab-pane .tab-header-area {");
        sb.append("-fx-background-color:#111318;");
        sb.append("-fx-border-color:transparent transparent #1e2330 transparent;");
        sb.append("-fx-border-width:1px;");
        sb.append("-fx-padding:0 0 0 8;");
        sb.append("}");
        sb.append(".tab-pane .tab-header-background { -fx-background-color:transparent; }");
        sb.append(".tab {");
        sb.append("-fx-background-color:transparent;");
        sb.append("-fx-padding:6 16 6 16;");
        sb.append("-fx-border-color:transparent;");
        sb.append("}");
        sb.append(".tab .tab-label {");
        sb.append("-fx-text-fill:#4a5068;");
        sb.append("-fx-font-family:'JetBrains Mono','Courier New',monospace;");
        sb.append("-fx-font-size:11px;");
        sb.append("}");
        sb.append(".tab:hover .tab-label { -fx-text-fill:#d4d8e2; }");
        sb.append(".tab:selected {");
        sb.append("-fx-background-color:transparent;");
        sb.append("-fx-border-color:transparent transparent #4f7cff transparent;");
        sb.append("-fx-border-width:0 0 2px 0;");
        sb.append("}");
        sb.append(".tab:selected .tab-label { -fx-text-fill:#4f7cff; }");
        sb.append(".tab-content-area { -fx-background-color:#0a0b0e; }");

        // Chart
        sb.append(".chart { -fx-background-color:#0a0b0e; -fx-padding:8; }");
        sb.append(".chart-plot-background { -fx-background-color:#111318; }");
        sb.append(".chart-vertical-grid-lines { -fx-stroke:#1e2330; -fx-stroke-width:1px; }");
        sb.append(".chart-horizontal-grid-lines { -fx-stroke:#1e2330; -fx-stroke-width:1px; }");
        sb.append(".chart-alternative-row-fill { -fx-background-color:transparent; }");
        sb.append(".chart-vertical-zero-line { -fx-stroke:#1e2330; }");
        sb.append(".chart-horizontal-zero-line { -fx-stroke:#1e2330; }");
        sb.append(".axis {");
        sb.append("-fx-tick-label-fill:#4a5068;");
        sb.append("-fx-font-family:'JetBrains Mono','Courier New',monospace;");
        sb.append("-fx-font-size:10px;");
        sb.append("}");
        sb.append(".axis-label { -fx-text-fill:#4a5068; -fx-font-size:10px; }");
        sb.append(".chart-legend {");
        sb.append("-fx-background-color:#111318;");
        sb.append("-fx-border-color:#1e2330;");
        sb.append("-fx-border-radius:3px;");
        sb.append("-fx-background-radius:3px;");
        sb.append("-fx-padding:4 10 4 10;");
        sb.append("}");
        sb.append(".chart-legend-item { -fx-text-fill:#d4d8e2; -fx-font-size:10px; }");
        sb.append(".default-color0.chart-series-line{-fx-stroke:#4f7cff;-fx-stroke-width:1.5px;}");
        sb.append(".default-color1.chart-series-line{-fx-stroke:#c8a84b;-fx-stroke-width:1.5px;}");
        sb.append(".default-color2.chart-series-line{-fx-stroke:#4ecb8d;-fx-stroke-width:1.5px;}");
        sb.append(".default-color3.chart-series-line{-fx-stroke:#e05c6a;-fx-stroke-width:1.5px;}");
        sb.append(".default-color4.chart-series-line{-fx-stroke:#7fa0ff;-fx-stroke-width:1.5px;}");
        sb.append(".default-color5.chart-series-line{-fx-stroke:#e8a45a;-fx-stroke-width:1.5px;}");
        sb.append(".default-color6.chart-series-line{-fx-stroke:#38c4b8;-fx-stroke-width:1.5px;}");
        sb.append(".default-color7.chart-series-line{-fx-stroke:#c07aff;-fx-stroke-width:1.5px;}");
        sb.append(".default-color0.chart-legend-item-symbol{-fx-background-color:#4f7cff;}");
        sb.append(".default-color1.chart-legend-item-symbol{-fx-background-color:#c8a84b;}");
        sb.append(".default-color2.chart-legend-item-symbol{-fx-background-color:#4ecb8d;}");
        sb.append(".default-color3.chart-legend-item-symbol{-fx-background-color:#e05c6a;}");
        sb.append(".default-color4.chart-legend-item-symbol{-fx-background-color:#7fa0ff;}");
        sb.append(".default-color5.chart-legend-item-symbol{-fx-background-color:#e8a45a;}");
        sb.append(".default-color6.chart-legend-item-symbol{-fx-background-color:#38c4b8;}");
        sb.append(".default-color7.chart-legend-item-symbol{-fx-background-color:#c07aff;}");

        // Dialog
        sb.append(".dialog-pane {");
        sb.append("-fx-background-color:#111318;");
        sb.append("-fx-border-color:#1e2330;");
        sb.append("-fx-border-width:1px;");
        sb.append("}");
        sb.append(".dialog-pane .header-panel { -fx-background-color:#0a0b0e; }");
        sb.append(".dialog-pane .header-panel .label {");
        sb.append("-fx-text-fill:#d4d8e2;");
        sb.append("-fx-font-size:13px;");
        sb.append("}");
        sb.append(".dialog-pane .content.label {");
        sb.append("-fx-text-fill:#4a5068;");
        sb.append("-fx-font-size:12px;");
        sb.append("}");
        sb.append(".dialog-pane .button-bar .button {");
        sb.append("-fx-background-color:#1e2330;");
        sb.append("-fx-text-fill:#d4d8e2;");
        sb.append("}");
        sb.append(".dialog-pane .button-bar .button:hover {");
        sb.append("-fx-background-color:#4f7cff;");
        sb.append("-fx-text-fill:#0a0b0e;");
        sb.append("}");

        return sb.toString();
    }
}
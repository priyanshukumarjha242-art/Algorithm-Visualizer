package com.visualizer.ui;

import com.visualizer.core.VisualizerListener;
import com.visualizer.core.VisualizerModule;
import com.visualizer.graph.GraphPanel;
import com.visualizer.searching.SearchingPanel;
import com.visualizer.sorting.SortingPanel;
import com.visualizer.ui.components.ModernButton;
import com.visualizer.ui.components.ModernSliderUI;
import com.visualizer.ui.components.StatusCard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main application window featuring a stunning high-contrast header,
 * tab switcher, center module container, and control dock with custom slider.
 */
public class MainWindow extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel centerContainer;

    private final SortingPanel sortingPanel;
    private final SearchingPanel searchingPanel;
    private final GraphPanel graphPanel;

    private VisualizerModule currentModule;

    // Navigation Buttons
    private ModernButton btnNavSorting;
    private ModernButton btnNavSearching;
    private ModernButton btnNavGraph;

    // Controls
    private ModernButton btnPlay;
    private ModernButton btnPause;
    private ModernButton btnReset;
    private JSlider speedSlider;
    private JLabel speedLabel;

    // Telemetry Cards
    private StatusCard cardStatus;
    private StatusCard cardComparisons;
    private StatusCard cardOperations;
    private StatusCard cardTime;

    public MainWindow() {
        super("Algorithm Visualizer - By group 17");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1260, 830);
        setMinimumSize(new Dimension(1020, 680));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG_DARK);
        setLayout(new BorderLayout());

        // Initialize modules
        sortingPanel = new SortingPanel();
        searchingPanel = new SearchingPanel();
        graphPanel = new GraphPanel();

        cardLayout = new CardLayout();
        centerContainer = new JPanel(cardLayout);
        centerContainer.setBackground(Theme.BG_DARK);

        centerContainer.add(sortingPanel, "SORTING");
        centerContainer.add(searchingPanel, "SEARCHING");
        centerContainer.add(graphPanel, "GRAPH");

        currentModule = sortingPanel;

        // Wire Observer Telemetry
        setupTelemetryListeners(sortingPanel);
        setupTelemetryListeners(searchingPanel);
        setupTelemetryListeners(graphPanel);

        // Assemble Top Header & Bottom Control Dock
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(centerContainer, BorderLayout.CENTER);
        add(createControlDock(), BorderLayout.SOUTH);

        // Apply initial speed
        updateSpeed();
        updateNavState("SORTING");
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.SURFACE_HEADER);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_COLOR));
        header.setPreferredSize(new Dimension(1200, 62));

        // App Branding with Custom Painted Logo Icon
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        brandPanel.setOpaque(false);

        // Geometric Vector Logo
        JPanel logoIcon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(0x1C, 0x1C, 0x1C));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(0x38, 0x38, 0x38));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

                // Draw mini animated graph/bars mark
                g2.setColor(Theme.ACCENT_CYAN);
                g2.fillRoundRect(8, 16, 4, 12, 2, 2);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(14, 10, 4, 18, 2, 2);
                g2.setColor(Theme.COLOR_COMPARING);
                g2.fillRoundRect(20, 6, 4, 22, 2, 2);

                g2.dispose();
            }
        };
        logoIcon.setPreferredSize(new Dimension(34, 34));
        logoIcon.setOpaque(false);

        JLabel titleText = new JLabel("ALGORITHM VISUALIZER");
        titleText.setFont(Theme.FONT_APP_TITLE);
        titleText.setForeground(Color.WHITE);

        JLabel badge = new JLabel(" BY GROUP 17 ");
        badge.setFont(Theme.FONT_MONO);
        badge.setForeground(Theme.TEXT_MUTED);
        badge.setBorder(BorderFactory.createLineBorder(new Color(0x33, 0x33, 0x33), 1, true));

        brandPanel.add(logoIcon);
        brandPanel.add(titleText);
        brandPanel.add(badge);

        // Navigation Tabs
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        navPanel.setOpaque(false);

        btnNavSorting = new ModernButton("Sorting");
        btnNavSearching = new ModernButton("Searching");
        btnNavGraph = new ModernButton("Graph & Pathfinding");

        Dimension navBtnDim = new Dimension(150, 34);
        btnNavSorting.setPreferredSize(navBtnDim);
        btnNavSearching.setPreferredSize(navBtnDim);
        btnNavGraph.setPreferredSize(navBtnDim);

        btnNavSorting.addActionListener(e -> switchTab("SORTING", sortingPanel));
        btnNavSearching.addActionListener(e -> switchTab("SEARCHING", searchingPanel));
        btnNavGraph.addActionListener(e -> switchTab("GRAPH", graphPanel));

        navPanel.add(btnNavSorting);
        navPanel.add(btnNavSearching);
        navPanel.add(btnNavGraph);

        header.add(brandPanel, BorderLayout.WEST);
        header.add(navPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createControlDock() {
        JPanel dock = new JPanel(new BorderLayout());
        dock.setBackground(Theme.SURFACE_HEADER);
        dock.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER_COLOR));
        dock.setPreferredSize(new Dimension(1200, 72));

        // Left Controls: Start, Pause, Reset (Step feature removed as requested)
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 16));
        controls.setOpaque(false);

        // Premium CTA Start Button
        btnPlay = new ModernButton("START", Color.WHITE, new Color(0xE2, 0xE8, 0xF0), Color.BLACK);
        btnPlay.setPreferredSize(new Dimension(100, 38));
        btnPlay.addActionListener(e -> {
            if (currentModule.isPaused()) {
                currentModule.resumeVisualization();
            } else {
                currentModule.startVisualization();
            }
        });

        // Pause Button
        btnPause = new ModernButton("PAUSE", new Color(0x18, 0x18, 0x18), new Color(0x28, 0x28, 0x28), Theme.COLOR_COMPARING);
        btnPause.setPreferredSize(new Dimension(95, 38));
        btnPause.addActionListener(e -> currentModule.pauseVisualization());

        // Reset Button
        btnReset = new ModernButton("RESET", new Color(0x18, 0x18, 0x18), new Color(0x28, 0x28, 0x28), Theme.COLOR_SWAPPING);
        btnReset.setPreferredSize(new Dimension(95, 38));
        btnReset.addActionListener(e -> currentModule.resetVisualization());

        // Custom Modern Speed Slider
        speedSlider = new JSlider(1, 100, 65);
        speedSlider.setUI(new ModernSliderUI(speedSlider));
        speedSlider.setBackground(Theme.SURFACE_HEADER);
        speedSlider.setOpaque(false);
        speedSlider.setPreferredSize(new Dimension(120, 24));

        speedLabel = new JLabel("SPEED: 65%");
        speedLabel.setFont(Theme.FONT_BOLD);
        speedLabel.setForeground(Theme.TEXT_MUTED);

        speedSlider.addChangeListener(e -> updateSpeed());

        controls.add(btnPlay);
        controls.add(btnPause);
        controls.add(btnReset);
        controls.add(Box.createHorizontalStrut(14));
        controls.add(speedLabel);
        controls.add(speedSlider);

        // Right Telemetry Dashboard Cards
        JPanel telemetry = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        telemetry.setOpaque(false);

        cardStatus = new StatusCard("Status", "READY");
        cardComparisons = new StatusCard("Comparisons", "0");
        cardOperations = new StatusCard("Operations", "0");
        cardTime = new StatusCard("Elapsed", "0 ms");

        cardStatus.setAccentColor(Theme.ACCENT_CYAN);
        cardComparisons.setAccentColor(Theme.COLOR_COMPARING);
        cardOperations.setAccentColor(Theme.COLOR_SORTED);
        cardTime.setAccentColor(Theme.COLOR_PIVOT);

        telemetry.add(cardStatus);
        telemetry.add(cardComparisons);
        telemetry.add(cardOperations);
        telemetry.add(cardTime);

        dock.add(controls, BorderLayout.WEST);
        dock.add(telemetry, BorderLayout.EAST);

        return dock;
    }

    private void updateSpeed() {
        int speed = speedSlider.getValue();
        speedLabel.setText("SPEED: " + speed + "%");
        if (currentModule != null) {
            currentModule.setSpeed(speed);
        }
    }

    private void switchTab(String cardName, VisualizerModule module) {
        if (currentModule != null && currentModule.isRunning()) {
            currentModule.resetVisualization();
        }
        currentModule = module;
        cardLayout.show(centerContainer, cardName);
        updateSpeed();
        updateNavState(cardName);
        resetTelemetryDisplay();
    }

    private void updateNavState(String activeTab) {
        Color activeBg = new Color(0x28, 0x28, 0x28);
        Color activeHover = new Color(0x33, 0x33, 0x33);
        Color activeBorder = Color.WHITE;
        Color activeText = Color.WHITE;

        Color inactiveBg = new Color(0x10, 0x10, 0x10);
        Color inactiveHover = new Color(0x1C, 0x1C, 0x1C);
        Color inactiveBorder = new Color(0x28, 0x28, 0x28);
        Color inactiveText = Theme.TEXT_MUTED;

        btnNavSorting.setCustomColors(activeTab.equals("SORTING") ? activeBg : inactiveBg,
                                      activeTab.equals("SORTING") ? activeHover : inactiveHover,
                                      activeTab.equals("SORTING") ? activeBorder : inactiveBorder,
                                      activeTab.equals("SORTING") ? activeText : inactiveText);

        btnNavSearching.setCustomColors(activeTab.equals("SEARCHING") ? activeBg : inactiveBg,
                                        activeTab.equals("SEARCHING") ? activeHover : inactiveHover,
                                        activeTab.equals("SEARCHING") ? activeBorder : inactiveBorder,
                                        activeTab.equals("SEARCHING") ? activeText : inactiveText);

        btnNavGraph.setCustomColors(activeTab.equals("GRAPH") ? activeBg : inactiveBg,
                                    activeTab.equals("GRAPH") ? activeHover : inactiveHover,
                                    activeTab.equals("GRAPH") ? activeBorder : inactiveBorder,
                                    activeTab.equals("GRAPH") ? activeText : inactiveText);
    }

    private void resetTelemetryDisplay() {
        cardStatus.setValue("READY");
        cardComparisons.setValue("0");
        cardOperations.setValue("0");
        cardTime.setValue("0 ms");
    }

    private void setupTelemetryListeners(VisualizerModule module) {
        module.addListener(new VisualizerListener() {
            @Override
            public void onStatusChanged(String status) {
                if (currentModule == module) {
                    SwingUtilities.invokeLater(() -> cardStatus.setValue(status));
                }
            }

            @Override
            public void onMetricsUpdated(long comparisons, long operations, long elapsedTimeMs) {
                if (currentModule == module) {
                    SwingUtilities.invokeLater(() -> {
                        cardComparisons.setValue(String.valueOf(comparisons));
                        cardOperations.setValue(String.valueOf(operations));
                        cardTime.setValue(elapsedTimeMs + " ms");
                    });
                }
            }

            @Override
            public void onCompleted() {
                if (currentModule == module) {
                    SwingUtilities.invokeLater(() -> cardStatus.setValue("FINISHED"));
                }
            }
        });
    }
}

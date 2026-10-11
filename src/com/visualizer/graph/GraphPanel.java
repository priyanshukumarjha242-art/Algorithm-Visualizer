package com.visualizer.graph;

import com.visualizer.core.AnimationController;
import com.visualizer.core.VisualizerListener;
import com.visualizer.core.VisualizerModule;
import com.visualizer.graph.algorithms.BFS;
import com.visualizer.graph.algorithms.DFS;
import com.visualizer.graph.algorithms.Dijkstra;
import com.visualizer.ui.Theme;
import com.visualizer.ui.components.ModernButton;
import com.visualizer.ui.components.ModernComboBox;
import com.visualizer.ui.components.ModernSliderUI;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GraphPanel extends JPanel implements VisualizerModule {
    private final GraphCanvas canvas;
    private final AnimationController controller;
    private Thread workerThread;

    private ModernComboBox<GraphAlgorithm> algoComboBox;
    private ModernComboBox<String> graphTypeComboBox;
    private ModernComboBox<Integer> startNodeComboBox;
    private ModernComboBox<Integer> targetNodeComboBox;
    private JLabel targetNodeLabel;
    private JSlider vertexSlider;
    private JLabel vertexCountLabel;
    private JLabel complexityLabel;

    private int nodeCount = 7;

    public GraphPanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.BG_DARK);

        this.controller = new AnimationController();
        this.canvas = new GraphCanvas();

        add(createControlBar(), BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);
        add(createLegendBar(), BorderLayout.SOUTH);

        // Defer initial generation after panel gets dimension
        SwingUtilities.invokeLater(this::generateGraph);
    }

    private JPanel createControlBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        bar.setBackground(Theme.SURFACE_HEADER);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_COLOR));

        // Algorithm Selector
        JLabel algoLbl = new JLabel("Algorithm:");
        algoLbl.setFont(Theme.FONT_BOLD);
        algoLbl.setForeground(Theme.TEXT_MUTED);

        GraphAlgorithm[] algorithms = new GraphAlgorithm[] {
            new Dijkstra(),
            new BFS(),
            new DFS()
        };

        algoComboBox = new ModernComboBox<>(algorithms);
        algoComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                l.setOpaque(true);
                l.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                if (value instanceof GraphAlgorithm) {
                    l.setText(((GraphAlgorithm) value).getName());
                }
                if (isSelected) {
                    l.setBackground(new Color(0x28, 0x28, 0x28));
                    l.setForeground(Color.WHITE);
                } else {
                    l.setBackground(new Color(0x14, 0x14, 0x14));
                    l.setForeground(Color.WHITE);
                }
                return l;
            }
        });

        // Graph Type (Directed/Undirected, Weighted/Unweighted)
        JLabel typeLbl = new JLabel("Graph Type:");
        typeLbl.setFont(Theme.FONT_BOLD);
        typeLbl.setForeground(Theme.TEXT_MUTED);

        graphTypeComboBox = new ModernComboBox<>(new String[] {
            "Directed (Weighted)",
            "Undirected (Unweighted)",
            "Undirected (Weighted)",
            "Directed (Unweighted)"
        });
        graphTypeComboBox.addActionListener(e -> {
            if (!controller.isRunning()) generateGraph();
        });

        // Node count slider
        vertexCountLabel = new JLabel("Nodes (" + nodeCount + "):");
        vertexCountLabel.setFont(Theme.FONT_BOLD);
        vertexCountLabel.setForeground(Theme.TEXT_MUTED);

        vertexSlider = new JSlider(4, 12, nodeCount);
        vertexSlider.setUI(new ModernSliderUI(vertexSlider));
        vertexSlider.setBackground(Theme.SURFACE_HEADER);
        vertexSlider.setOpaque(false);
        vertexSlider.setPreferredSize(new Dimension(85, 24));
        vertexSlider.addChangeListener(e -> {
            if (!controller.isRunning()) {
                nodeCount = vertexSlider.getValue();
                vertexCountLabel.setText("Nodes (" + nodeCount + "):");
                generateGraph();
            }
        });

        // Start & Target Node Selectors
        JLabel startLbl = new JLabel("Start:");
        startLbl.setFont(Theme.FONT_BOLD);
        startLbl.setForeground(Theme.TEXT_MUTED);

        startNodeComboBox = new ModernComboBox<>();
        startNodeComboBox.setPreferredSize(new Dimension(65, 28));

        targetNodeLabel = new JLabel("Target:");
        targetNodeLabel.setFont(Theme.FONT_BOLD);
        targetNodeLabel.setForeground(Theme.TEXT_MUTED);

        targetNodeComboBox = new ModernComboBox<>();
        targetNodeComboBox.setPreferredSize(new Dimension(65, 28));

        // Generate Graph Button
        ModernButton btnGen = new ModernButton("Generate Graph", Theme.SURFACE_CARD, Theme.BORDER_COLOR.brighter(), Color.WHITE);
        btnGen.setPreferredSize(new Dimension(135, 30));
        btnGen.addActionListener(e -> {
            if (!controller.isRunning()) generateGraph();
        });

        // Complexity Label
        complexityLabel = new JLabel();
        complexityLabel.setFont(Theme.FONT_MONO);
        complexityLabel.setForeground(Theme.TEXT_ACCENT);

        algoComboBox.addActionListener(e -> updateAlgorithmUI());

        bar.add(algoLbl);
        bar.add(algoComboBox);
        bar.add(Box.createHorizontalStrut(6));
        bar.add(typeLbl);
        bar.add(graphTypeComboBox);
        bar.add(Box.createHorizontalStrut(6));
        bar.add(vertexCountLabel);
        bar.add(vertexSlider);
        bar.add(Box.createHorizontalStrut(6));
        bar.add(startLbl);
        bar.add(startNodeComboBox);
        bar.add(targetNodeLabel);
        bar.add(targetNodeComboBox);
        bar.add(Box.createHorizontalStrut(6));
        bar.add(btnGen);
        bar.add(Box.createHorizontalStrut(10));
        bar.add(complexityLabel);

        updateAlgorithmUI();
        return bar;
    }

    private JPanel createLegendBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 6));
        bar.setBackground(Theme.SURFACE_HEADER);
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER_COLOR));

        bar.add(createLegendItem("Start Node", Theme.COLOR_SORTED));
        bar.add(createLegendItem("Target Node", Theme.COLOR_SWAPPING));
        bar.add(createLegendItem("Visiting", Theme.COLOR_COMPARING));
        bar.add(createLegendItem("Visited", Theme.COLOR_BAR_DEFAULT));
        bar.add(createLegendItem("Shortest Path", Theme.COLOR_SORTED));
        bar.add(createLegendItem("Traversing Edge", Theme.COLOR_COMPARING));

        JLabel hint = new JLabel("  |  Tip: Click and drag any circle to reposition vertices");
        hint.setFont(Theme.FONT_REGULAR);
        hint.setForeground(Theme.TEXT_MUTED);
        bar.add(hint);

        return bar;
    }

    private JPanel createLegendItem(String label, Color color) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        p.setOpaque(false);

        JPanel badge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(color);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        badge.setPreferredSize(new Dimension(14, 14));
        badge.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(Theme.FONT_REGULAR);
        lbl.setForeground(Theme.TEXT_PRIMARY);

        p.add(badge);
        p.add(lbl);
        return p;
    }

    private void updateAlgorithmUI() {
        GraphAlgorithm algo = (GraphAlgorithm) algoComboBox.getSelectedItem();
        if (algo != null) {
            boolean reqTarget = algo.requiresTarget();
            targetNodeLabel.setVisible(reqTarget);
            targetNodeComboBox.setVisible(reqTarget);
            complexityLabel.setText(String.format("Time: %s | Space: %s", algo.getTimeComplexity(), algo.getSpaceComplexity()));
        }
    }

    private void styleComboBox(JComboBox<?> box) {
        box.setBackground(Theme.SURFACE_CARD);
        box.setForeground(Theme.TEXT_PRIMARY);
        box.setFont(Theme.FONT_REGULAR);
        box.setFocusable(false);
    }

    public void generateGraph() {
        String type = (String) graphTypeComboBox.getSelectedItem();
        boolean directed = type != null && type.startsWith("Directed");
        boolean weighted = type != null && type.contains("Weighted");

        int w = canvas.getWidth() > 100 ? canvas.getWidth() : 1000;
        int h = canvas.getHeight() > 100 ? canvas.getHeight() : 500;

        Graph g = new Graph();
        g.generateRandom(nodeCount, directed, weighted, w, h);
        canvas.setGraph(g);

        // Update Start & Target Node dropdown options
        startNodeComboBox.removeAllItems();
        targetNodeComboBox.removeAllItems();

        List<Node> nodes = g.getNodes();
        for (Node n : nodes) {
            startNodeComboBox.addItem(n.id);
            targetNodeComboBox.addItem(n.id);
        }

        if (nodes.size() > 0) {
            startNodeComboBox.setSelectedIndex(0);
            targetNodeComboBox.setSelectedIndex(nodes.size() - 1);
        }

        controller.reset();
    }

    @Override
    public String getModuleName() {
        return "Graph Algorithms";
    }

    @Override
    public void startVisualization() {
        if (controller.isRunning()) return;

        canvas.clearNodeStates();
        canvas.clearEdgeStates();
        canvas.setPathResult("");
        canvas.redraw();

        GraphAlgorithm algo = (GraphAlgorithm) algoComboBox.getSelectedItem();
        if (algo == null) return;

        Integer startId = (Integer) startNodeComboBox.getSelectedItem();
        Integer targetId = (Integer) targetNodeComboBox.getSelectedItem();
        if (startId == null) return;
        if (targetId == null) targetId = startId;

        final int sId = startId;
        final int tId = targetId;

        setControlsEnabled(false);
        controller.start();

        workerThread = new Thread(() -> {
            try {
                algo.execute(canvas.getGraph(), sId, tId, canvas, controller);
                controller.complete();
            } catch (InterruptedException e) {
                // Interrupted
            } finally {
                SwingUtilities.invokeLater(() -> setControlsEnabled(true));
            }
        });
        workerThread.setName("GraphVisualizer-Thread");
        workerThread.start();
    }

    @Override
    public void pauseVisualization() {
        controller.pause();
    }

    @Override
    public void resumeVisualization() {
        controller.resume();
    }

    @Override
    public void resetVisualization() {
        controller.stop();
        if (workerThread != null && workerThread.isAlive()) {
            workerThread.interrupt();
        }
        controller.reset();
        canvas.clearNodeStates();
        canvas.clearEdgeStates();
        canvas.setPathResult("");
        canvas.redraw();
        setControlsEnabled(true);
    }

    @Override
    public void stepForward() {
        if (controller.isPaused()) {
            controller.step();
        } else if (!controller.isRunning()) {
            startVisualization();
            SwingUtilities.invokeLater(() -> {
                controller.pause();
                controller.step();
            });
        }
    }

    @Override
    public void setSpeed(int speedPercent) {
        int delay = (int) (400 * (1.0 - (speedPercent / 100.0)));
        controller.setDelayMs(delay);
    }

    @Override
    public boolean isRunning() {
        return controller.isRunning();
    }

    @Override
    public boolean isPaused() {
        return controller.isPaused();
    }

    @Override
    public void addListener(VisualizerListener listener) {
        controller.addListener(listener);
    }

    private void setControlsEnabled(boolean enabled) {
        algoComboBox.setEnabled(enabled);
        graphTypeComboBox.setEnabled(enabled);
        startNodeComboBox.setEnabled(enabled);
        targetNodeComboBox.setEnabled(enabled);
        vertexSlider.setEnabled(enabled);
    }
}

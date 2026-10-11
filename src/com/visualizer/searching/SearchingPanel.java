package com.visualizer.searching;

import com.visualizer.core.AnimationController;
import com.visualizer.core.VisualizerListener;
import com.visualizer.core.VisualizerModule;
import com.visualizer.searching.algorithms.BinarySearch;
import com.visualizer.searching.algorithms.LinearSearch;
import com.visualizer.ui.Theme;
import com.visualizer.ui.components.ModernButton;
import com.visualizer.ui.components.ModernComboBox;
import com.visualizer.ui.components.ModernSliderUI;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Random;

public class SearchingPanel extends JPanel implements VisualizerModule {
    private final SearchingCanvas canvas;
    private final AnimationController controller;
    private Thread workerThread;

    private ModernComboBox<SearchAlgorithm> algoComboBox;
    private JSlider sizeSlider;
    private JTextField targetField;
    private JLabel complexityLabel;

    private int[] currentArray;
    private int currentSize = 18;

    public SearchingPanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.BG_DARK);

        this.controller = new AnimationController();
        this.canvas = new SearchingCanvas();

        add(createControlBar(), BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);

        generateArray();
    }

    private JPanel createControlBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
        bar.setBackground(Theme.SURFACE_HEADER);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_COLOR));

        JLabel algoLbl = new JLabel("Algorithm:");
        algoLbl.setFont(Theme.FONT_BOLD);
        algoLbl.setForeground(Theme.TEXT_MUTED);

        SearchAlgorithm[] algorithms = new SearchAlgorithm[] {
            new BinarySearch(),
            new LinearSearch()
        };

        algoComboBox = new ModernComboBox<>(algorithms);
        algoComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                l.setOpaque(true);
                l.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                if (value instanceof SearchAlgorithm) {
                    l.setText(((SearchAlgorithm) value).getName());
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

        JLabel sizeLbl = new JLabel("Items (" + currentSize + "):");
        sizeLbl.setFont(Theme.FONT_BOLD);
        sizeLbl.setForeground(Theme.TEXT_MUTED);

        sizeSlider = new JSlider(10, 30, currentSize);
        sizeSlider.setUI(new ModernSliderUI(sizeSlider));
        sizeSlider.setBackground(Theme.SURFACE_HEADER);
        sizeSlider.setOpaque(false);
        sizeSlider.setPreferredSize(new Dimension(100, 24));
        sizeSlider.addChangeListener(e -> {
            if (!controller.isRunning()) {
                currentSize = sizeSlider.getValue();
                sizeLbl.setText("Items (" + currentSize + "):");
                generateArray();
            }
        });

        JLabel targetLbl = new JLabel("Target:");
        targetLbl.setFont(Theme.FONT_BOLD);
        targetLbl.setForeground(Theme.TEXT_MUTED);

        targetField = new JTextField("42", 4);
        targetField.setFont(Theme.FONT_MONO);
        targetField.setBackground(Theme.SURFACE_CARD);
        targetField.setForeground(Theme.TEXT_PRIMARY);
        targetField.setCaretColor(Theme.TEXT_PRIMARY);

        ModernButton btnPickRandom = new ModernButton("Pick Random Target", Theme.SURFACE_CARD, Theme.BORDER_COLOR.brighter());
        btnPickRandom.setPreferredSize(new Dimension(145, 30));
        btnPickRandom.addActionListener(e -> pickRandomTarget());

        ModernButton btnRegen = new ModernButton("Regenerate Array", Theme.SURFACE_CARD, Theme.BORDER_COLOR.brighter());
        btnRegen.setPreferredSize(new Dimension(135, 30));
        btnRegen.addActionListener(e -> {
            if (!controller.isRunning()) {
                generateArray();
            }
        });

        complexityLabel = new JLabel();
        complexityLabel.setFont(Theme.FONT_MONO);
        complexityLabel.setForeground(Theme.TEXT_ACCENT);
        updateComplexityLabel();

        algoComboBox.addActionListener(e -> {
            updateComplexityLabel();
            if (!controller.isRunning()) {
                generateArray();
            }
        });

        bar.add(algoLbl);
        bar.add(algoComboBox);
        bar.add(Box.createHorizontalStrut(8));
        bar.add(sizeLbl);
        bar.add(sizeSlider);
        bar.add(Box.createHorizontalStrut(8));
        bar.add(targetLbl);
        bar.add(targetField);
        bar.add(btnPickRandom);
        bar.add(btnRegen);
        bar.add(Box.createHorizontalStrut(12));
        bar.add(complexityLabel);

        return bar;
    }

    private void updateComplexityLabel() {
        SearchAlgorithm algo = (SearchAlgorithm) algoComboBox.getSelectedItem();
        if (algo != null) {
            complexityLabel.setText(String.format("Time: %s | Space: %s", algo.getTimeComplexity(), algo.getSpaceComplexity()));
        }
    }

    private void styleComboBox(JComboBox<?> box) {
        box.setBackground(Theme.SURFACE_CARD);
        box.setForeground(Theme.TEXT_PRIMARY);
        box.setFont(Theme.FONT_REGULAR);
        box.setFocusable(false);
    }

    public void generateArray() {
        int[] arr = new int[currentSize];
        Random rand = new Random();
        SearchAlgorithm algo = (SearchAlgorithm) algoComboBox.getSelectedItem();

        if (algo != null && algo.requiresSortedArray()) {
            int val = rand.nextInt(10) + 5;
            for (int i = 0; i < currentSize; i++) {
                val += rand.nextInt(7) + 2;
                arr[i] = val;
            }
        } else {
            for (int i = 0; i < currentSize; i++) {
                arr[i] = rand.nextInt(90) + 10;
            }
        }

        currentArray = arr;
        canvas.setArray(arr);
        controller.reset();
        pickRandomTarget();
    }

    private void pickRandomTarget() {
        if (currentArray != null && currentArray.length > 0) {
            Random rand = new Random();
            // 75% chance pick existing element, 25% chance pick random number
            if (rand.nextInt(4) < 3) {
                int randomIdx = rand.nextInt(currentArray.length);
                targetField.setText(String.valueOf(currentArray[randomIdx]));
            } else {
                targetField.setText(String.valueOf(rand.nextInt(100) + 5));
            }
        }
    }

    @Override
    public String getModuleName() {
        return "Searching Algorithms";
    }

    @Override
    public void startVisualization() {
        if (controller.isRunning()) return;

        canvas.clearAllStates();
        canvas.redraw();

        SearchAlgorithm algo = (SearchAlgorithm) algoComboBox.getSelectedItem();
        if (algo == null || currentArray == null) return;

        int target;
        try {
            target = Integer.parseInt(targetField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid integer target.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        setControlsEnabled(false);
        controller.start();

        workerThread = new Thread(() -> {
            try {
                int[] workingArray = canvas.getArrayCopy();
                algo.search(workingArray, target, canvas, controller);
                controller.complete();
            } catch (InterruptedException e) {
                // Interrupted
            } finally {
                SwingUtilities.invokeLater(() -> setControlsEnabled(true));
            }
        });
        workerThread.setName("SearchingVisualizer-Thread");
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
        canvas.clearAllStates();
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
        int delay = (int) (350 * (1.0 - (speedPercent / 100.0)));
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
        sizeSlider.setEnabled(enabled);
        targetField.setEnabled(enabled);
    }
}

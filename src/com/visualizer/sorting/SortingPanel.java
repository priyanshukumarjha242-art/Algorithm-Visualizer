package com.visualizer.sorting;

import com.visualizer.core.AnimationController;
import com.visualizer.core.VisualizerListener;
import com.visualizer.core.VisualizerModule;
import com.visualizer.sorting.algorithms.*;
import com.visualizer.ui.Theme;
import com.visualizer.ui.components.ModernButton;
import com.visualizer.ui.components.ModernComboBox;
import com.visualizer.ui.components.ModernSliderUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Random;

public class SortingPanel extends JPanel implements VisualizerModule {
    private final SortingCanvas canvas;
    private final AnimationController controller;
    private Thread workerThread;

    private ModernComboBox<SortingAlgorithm> algoComboBox;
    private ModernComboBox<String> distributionComboBox;
    private JSlider sizeSlider;
    private JLabel complexityLabel;

    private int[] originalArray;
    private int currentSize = 35;

    public SortingPanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.BG_DARK);

        this.controller = new AnimationController();
        this.canvas = new SortingCanvas();

        add(createControlBar(), BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);

        generateArray("Random");
    }

    private JPanel createControlBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
        bar.setBackground(Theme.SURFACE_HEADER);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_COLOR));

        // Algorithm Selector
        JLabel algoLbl = new JLabel("Algorithm:");
        algoLbl.setFont(Theme.FONT_BOLD);
        algoLbl.setForeground(Theme.TEXT_MUTED);

        SortingAlgorithm[] algorithms = new SortingAlgorithm[] {
            new BubbleSort(),
            new SelectionSort(),
            new InsertionSort(),
            new MergeSort(),
            new QuickSort(),
            new HeapSort()
        };

        algoComboBox = new ModernComboBox<>(algorithms);
        algoComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                l.setOpaque(true);
                l.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                if (value instanceof SortingAlgorithm) {
                    l.setText(((SortingAlgorithm) value).getName());
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

        // Distribution Selector
        JLabel distLbl = new JLabel("Pattern:");
        distLbl.setFont(Theme.FONT_BOLD);
        distLbl.setForeground(Theme.TEXT_MUTED);

        distributionComboBox = new ModernComboBox<>(new String[] {"Random", "Reversed", "Nearly Sorted", "Few Unique"});

        // Size Slider
        JLabel sizeLbl = new JLabel("Elements (" + currentSize + "):");
        sizeLbl.setFont(Theme.FONT_BOLD);
        sizeLbl.setForeground(Theme.TEXT_MUTED);

        sizeSlider = new JSlider(10, 120, currentSize);
        sizeSlider.setUI(new ModernSliderUI(sizeSlider));
        sizeSlider.setBackground(Theme.SURFACE_HEADER);
        sizeSlider.setOpaque(false);
        sizeSlider.setPreferredSize(new Dimension(130, 24));
        sizeSlider.addChangeListener(e -> {
            if (!controller.isRunning()) {
                currentSize = sizeSlider.getValue();
                sizeLbl.setText("Elements (" + currentSize + "):");
                generateArray((String) distributionComboBox.getSelectedItem());
            }
        });

        // Generate Array Button
        ModernButton btnGenerate = new ModernButton("Shuffle Array", Theme.SURFACE_CARD, Theme.BORDER_COLOR.brighter());
        btnGenerate.setPreferredSize(new Dimension(120, 30));
        btnGenerate.addActionListener(e -> {
            if (!controller.isRunning()) {
                generateArray((String) distributionComboBox.getSelectedItem());
            }
        });

        // Complexity display
        complexityLabel = new JLabel();
        complexityLabel.setFont(Theme.FONT_MONO);
        complexityLabel.setForeground(Theme.TEXT_ACCENT);
        updateComplexityLabel();

        algoComboBox.addActionListener(e -> updateComplexityLabel());
        distributionComboBox.addActionListener(e -> {
            if (!controller.isRunning()) {
                generateArray((String) distributionComboBox.getSelectedItem());
            }
        });

        bar.add(algoLbl);
        bar.add(algoComboBox);
        bar.add(Box.createHorizontalStrut(8));
        bar.add(distLbl);
        bar.add(distributionComboBox);
        bar.add(Box.createHorizontalStrut(8));
        bar.add(sizeLbl);
        bar.add(sizeSlider);
        bar.add(Box.createHorizontalStrut(6));
        bar.add(btnGenerate);
        bar.add(Box.createHorizontalStrut(14));
        bar.add(complexityLabel);

        return bar;
    }

    private void updateComplexityLabel() {
        SortingAlgorithm algo = (SortingAlgorithm) algoComboBox.getSelectedItem();
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

    public void generateArray(String distribution) {
        int[] arr = new int[currentSize];
        Random rand = new Random();

        switch (distribution) {
            case "Reversed":
                for (int i = 0; i < currentSize; i++) {
                    arr[i] = (int) (((double) (currentSize - i) / currentSize) * 90) + 10;
                }
                break;
            case "Nearly Sorted":
                for (int i = 0; i < currentSize; i++) {
                    arr[i] = (int) (((double) (i + 1) / currentSize) * 85) + 10;
                }
                // Randomly swap a few pairs
                for (int k = 0; k < Math.max(2, currentSize / 8); k++) {
                    int i1 = rand.nextInt(currentSize);
                    int i2 = rand.nextInt(currentSize);
                    int tmp = arr[i1];
                    arr[i1] = arr[i2];
                    arr[i2] = tmp;
                }
                break;
            case "Few Unique":
                int[] uniqueVals = new int[] {15, 35, 60, 85, 100};
                for (int i = 0; i < currentSize; i++) {
                    arr[i] = uniqueVals[rand.nextInt(uniqueVals.length)];
                }
                break;
            case "Random":
            default:
                for (int i = 0; i < currentSize; i++) {
                    arr[i] = rand.nextInt(90) + 10;
                }
                break;
        }

        originalArray = arr.clone();
        canvas.setArray(arr);
        controller.reset();
    }

    @Override
    public String getModuleName() {
        return "Sorting Algorithms";
    }

    @Override
    public void startVisualization() {
        if (controller.isRunning()) return;

        // Reset array to original before sorting
        if (originalArray != null) {
            canvas.setArray(originalArray.clone());
        }

        SortingAlgorithm algo = (SortingAlgorithm) algoComboBox.getSelectedItem();
        if (algo == null) return;

        setControlsEnabled(false);
        controller.start();

        workerThread = new Thread(() -> {
            try {
                int[] workingArray = canvas.getInternalArray();
                algo.sort(workingArray, canvas, controller);
                controller.complete();
            } catch (InterruptedException e) {
                // Visualization interrupted or reset
            } finally {
                SwingUtilities.invokeLater(() -> setControlsEnabled(true));
            }
        });
        workerThread.setName("SortingVisualizer-Thread");
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
        if (originalArray != null) {
            canvas.setArray(originalArray.clone());
        }
        setControlsEnabled(true);
    }

    @Override
    public void stepForward() {
        if (controller.isPaused()) {
            controller.step();
        } else if (!controller.isRunning()) {
            // Start paused and step once
            startVisualization();
            SwingUtilities.invokeLater(() -> {
                controller.pause();
                controller.step();
            });
        }
    }

    @Override
    public void setSpeed(int speedPercent) {
        // Speed slider 1..100 maps to delayMs 150..0
        int delay = (int) (150 * (1.0 - (speedPercent / 100.0)));
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
        distributionComboBox.setEnabled(enabled);
        sizeSlider.setEnabled(enabled);
    }
}

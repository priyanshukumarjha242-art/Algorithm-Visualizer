package com.visualizer.sorting.algorithms;

import com.visualizer.core.AnimationController;
import com.visualizer.sorting.ArrayVisualizer;
import com.visualizer.sorting.BarState;
import com.visualizer.sorting.SortingAlgorithm;

public class BubbleSort implements SortingAlgorithm {
    @Override
    public String getName() {
        return "Bubble Sort";
    }

    @Override
    public String getTimeComplexity() {
        return "O(N²)";
    }

    @Override
    public String getSpaceComplexity() {
        return "O(1)";
    }

    @Override
    public void sort(int[] array, ArrayVisualizer visualizer, AnimationController controller) throws InterruptedException {
        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                // Highlight comparing elements
                visualizer.setBarStates(j, j + 1, BarState.COMPARING);
                visualizer.redraw();
                controller.registerComparison();
                controller.checkpoint();

                if (array[j] > array[j + 1]) {
                    // Highlight swapping elements
                    visualizer.setBarStates(j, j + 1, BarState.SWAPPING);
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    swapped = true;
                    controller.registerOperation();
                    visualizer.redraw();
                    controller.checkpoint();
                }

                visualizer.clearState(j);
                visualizer.clearState(j + 1);
            }
            visualizer.markSorted(n - 1 - i);
            visualizer.redraw();
            if (!swapped) break;
        }
        visualizer.markAllSorted();
        visualizer.redraw();
    }
}

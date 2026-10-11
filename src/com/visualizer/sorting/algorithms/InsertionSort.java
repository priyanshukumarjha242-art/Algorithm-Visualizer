package com.visualizer.sorting.algorithms;

import com.visualizer.core.AnimationController;
import com.visualizer.sorting.ArrayVisualizer;
import com.visualizer.sorting.BarState;
import com.visualizer.sorting.SortingAlgorithm;

public class InsertionSort implements SortingAlgorithm {
    @Override
    public String getName() {
        return "Insertion Sort";
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
        visualizer.markSorted(0);

        for (int i = 1; i < n; i++) {
            int key = array[i];
            int j = i - 1;

            visualizer.setBarState(i, BarState.SELECTED);
            visualizer.redraw();
            controller.checkpoint();

            while (j >= 0) {
                visualizer.setBarState(j, BarState.COMPARING);
                visualizer.redraw();
                controller.registerComparison();
                controller.checkpoint();

                if (array[j] > key) {
                    visualizer.setBarStates(j, j + 1, BarState.SWAPPING);
                    array[j + 1] = array[j];
                    controller.registerOperation();
                    visualizer.redraw();
                    controller.checkpoint();

                    visualizer.clearState(j);
                    visualizer.clearState(j + 1);
                    j--;
                } else {
                    visualizer.clearState(j);
                    break;
                }
            }
            array[j + 1] = key;
            controller.registerOperation();
            visualizer.setBarState(j + 1, BarState.SWAPPING);
            visualizer.redraw();
            controller.checkpoint();

            for (int k = 0; k <= i; k++) {
                visualizer.markSorted(k);
            }
            visualizer.redraw();
            controller.checkpoint();
        }
        visualizer.markAllSorted();
        visualizer.redraw();
    }
}

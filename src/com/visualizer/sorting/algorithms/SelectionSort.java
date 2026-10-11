package com.visualizer.sorting.algorithms;

import com.visualizer.core.AnimationController;
import com.visualizer.sorting.ArrayVisualizer;
import com.visualizer.sorting.BarState;
import com.visualizer.sorting.SortingAlgorithm;

public class SelectionSort implements SortingAlgorithm {
    @Override
    public String getName() {
        return "Selection Sort";
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
            int minIdx = i;
            visualizer.setBarState(minIdx, BarState.SELECTED);
            visualizer.redraw();

            for (int j = i + 1; j < n; j++) {
                visualizer.setBarState(j, BarState.COMPARING);
                visualizer.redraw();
                controller.registerComparison();
                controller.checkpoint();

                if (array[j] < array[minIdx]) {
                    if (minIdx != i) {
                        visualizer.clearState(minIdx);
                    }
                    minIdx = j;
                    visualizer.setBarState(minIdx, BarState.SELECTED);
                    visualizer.redraw();
                } else {
                    visualizer.clearState(j);
                }
            }

            if (minIdx != i) {
                visualizer.setBarStates(i, minIdx, BarState.SWAPPING);
                int temp = array[minIdx];
                array[minIdx] = array[i];
                array[i] = temp;
                controller.registerOperation();
                visualizer.redraw();
                controller.checkpoint();
            }

            visualizer.clearState(minIdx);
            visualizer.markSorted(i);
            visualizer.redraw();
        }
        visualizer.markAllSorted();
        visualizer.redraw();
    }
}

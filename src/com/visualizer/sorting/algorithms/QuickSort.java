package com.visualizer.sorting.algorithms;

import com.visualizer.core.AnimationController;
import com.visualizer.sorting.ArrayVisualizer;
import com.visualizer.sorting.BarState;
import com.visualizer.sorting.SortingAlgorithm;

public class QuickSort implements SortingAlgorithm {
    @Override
    public String getName() {
        return "Quick Sort";
    }

    @Override
    public String getTimeComplexity() {
        return "O(N log N)";
    }

    @Override
    public String getSpaceComplexity() {
        return "O(log N)";
    }

    @Override
    public void sort(int[] array, ArrayVisualizer visualizer, AnimationController controller) throws InterruptedException {
        quickSort(array, 0, array.length - 1, visualizer, controller);
        visualizer.markAllSorted();
        visualizer.redraw();
    }

    private void quickSort(int[] array, int low, int high, ArrayVisualizer visualizer, AnimationController controller) throws InterruptedException {
        if (low < high) {
            int pi = partition(array, low, high, visualizer, controller);
            visualizer.markSorted(pi);
            visualizer.redraw();
            quickSort(array, low, pi - 1, visualizer, controller);
            quickSort(array, pi + 1, high, visualizer, controller);
        } else if (low == high) {
            visualizer.markSorted(low);
            visualizer.redraw();
        }
    }

    private int partition(int[] array, int low, int high, ArrayVisualizer visualizer, AnimationController controller) throws InterruptedException {
        int pivot = array[high];
        visualizer.setBarState(high, BarState.PIVOT);
        visualizer.redraw();

        int i = low - 1;

        for (int j = low; j < high; j++) {
            visualizer.setBarState(j, BarState.COMPARING);
            visualizer.redraw();
            controller.registerComparison();
            controller.checkpoint();

            if (array[j] < pivot) {
                i++;
                if (i != j) {
                    visualizer.setBarStates(i, j, BarState.SWAPPING);
                    int temp = array[i];
                    array[i] = array[j];
                    array[j] = temp;
                    controller.registerOperation();
                    visualizer.redraw();
                    controller.checkpoint();
                    visualizer.clearState(i);
                }
            }
            visualizer.clearState(j);
        }

        // Swap pivot into correct position
        int temp = array[i + 1];
        array[i + 1] = array[high];
        array[high] = temp;
        controller.registerOperation();

        visualizer.clearState(high);
        visualizer.setBarState(i + 1, BarState.SWAPPING);
        visualizer.redraw();
        controller.checkpoint();
        visualizer.clearState(i + 1);

        return i + 1;
    }
}

package com.visualizer.sorting.algorithms;

import com.visualizer.core.AnimationController;
import com.visualizer.sorting.ArrayVisualizer;
import com.visualizer.sorting.BarState;
import com.visualizer.sorting.SortingAlgorithm;

public class HeapSort implements SortingAlgorithm {
    @Override
    public String getName() {
        return "Heap Sort";
    }

    @Override
    public String getTimeComplexity() {
        return "O(N log N)";
    }

    @Override
    public String getSpaceComplexity() {
        return "O(1)";
    }

    @Override
    public void sort(int[] array, ArrayVisualizer visualizer, AnimationController controller) throws InterruptedException {
        int n = array.length;

        // Build max heap
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(array, n, i, visualizer, controller);
        }

        // Extract elements from heap one by one
        for (int i = n - 1; i > 0; i--) {
            visualizer.setBarStates(0, i, BarState.SWAPPING);
            int temp = array[0];
            array[0] = array[i];
            array[i] = temp;
            controller.registerOperation();
            visualizer.redraw();
            controller.checkpoint();

            visualizer.clearState(0);
            visualizer.markSorted(i);
            visualizer.redraw();

            heapify(array, i, 0, visualizer, controller);
        }
        visualizer.markAllSorted();
        visualizer.redraw();
    }

    private void heapify(int[] array, int n, int i, ArrayVisualizer visualizer, AnimationController controller) throws InterruptedException {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < n) {
            visualizer.setBarState(left, BarState.COMPARING);
            visualizer.setBarState(largest, BarState.COMPARING);
            visualizer.redraw();
            controller.registerComparison();
            controller.checkpoint();

            if (array[left] > array[largest]) {
                largest = left;
            }
            visualizer.clearState(left);
            visualizer.clearState(largest);
        }

        if (right < n) {
            visualizer.setBarState(right, BarState.COMPARING);
            visualizer.setBarState(largest, BarState.COMPARING);
            visualizer.redraw();
            controller.registerComparison();
            controller.checkpoint();

            if (array[right] > array[largest]) {
                largest = right;
            }
            visualizer.clearState(right);
            visualizer.clearState(largest);
        }

        if (largest != i) {
            visualizer.setBarStates(i, largest, BarState.SWAPPING);
            int swap = array[i];
            array[i] = array[largest];
            array[largest] = swap;
            controller.registerOperation();
            visualizer.redraw();
            controller.checkpoint();

            visualizer.clearState(i);
            visualizer.clearState(largest);

            heapify(array, n, largest, visualizer, controller);
        }
    }
}

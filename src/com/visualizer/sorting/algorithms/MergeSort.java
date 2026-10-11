package com.visualizer.sorting.algorithms;

import com.visualizer.core.AnimationController;
import com.visualizer.sorting.ArrayVisualizer;
import com.visualizer.sorting.BarState;
import com.visualizer.sorting.SortingAlgorithm;

public class MergeSort implements SortingAlgorithm {
    @Override
    public String getName() {
        return "Merge Sort";
    }

    @Override
    public String getTimeComplexity() {
        return "O(N log N)";
    }

    @Override
    public String getSpaceComplexity() {
        return "O(N)";
    }

    @Override
    public void sort(int[] array, ArrayVisualizer visualizer, AnimationController controller) throws InterruptedException {
        mergeSort(array, 0, array.length - 1, visualizer, controller);
        visualizer.markAllSorted();
        visualizer.redraw();
    }

    private void mergeSort(int[] array, int left, int right, ArrayVisualizer visualizer, AnimationController controller) throws InterruptedException {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(array, left, mid, visualizer, controller);
            mergeSort(array, mid + 1, right, visualizer, controller);
            merge(array, left, mid, right, visualizer, controller);
        }
    }

    private void merge(int[] array, int left, int mid, int right, ArrayVisualizer visualizer, AnimationController controller) throws InterruptedException {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        int[] leftArr = new int[n1];
        int[] rightArr = new int[n2];

        for (int i = 0; i < n1; i++) leftArr[i] = array[left + i];
        for (int j = 0; j < n2; j++) rightArr[j] = array[mid + 1 + j];

        int i = 0, j = 0;
        int k = left;

        while (i < n1 && j < n2) {
            visualizer.setBarState(left + i, BarState.COMPARING);
            visualizer.setBarState(mid + 1 + j, BarState.COMPARING);
            visualizer.redraw();
            controller.registerComparison();
            controller.checkpoint();

            visualizer.clearState(left + i);
            visualizer.clearState(mid + 1 + j);

            if (leftArr[i] <= rightArr[j]) {
                array[k] = leftArr[i];
                i++;
            } else {
                array[k] = rightArr[j];
                j++;
            }
            controller.registerOperation();
            visualizer.setBarState(k, BarState.SWAPPING);
            visualizer.redraw();
            controller.checkpoint();
            visualizer.clearState(k);
            k++;
        }

        while (i < n1) {
            array[k] = leftArr[i];
            controller.registerOperation();
            visualizer.setBarState(k, BarState.SWAPPING);
            visualizer.redraw();
            controller.checkpoint();
            visualizer.clearState(k);
            i++;
            k++;
        }

        while (j < n2) {
            array[k] = rightArr[j];
            controller.registerOperation();
            visualizer.setBarState(k, BarState.SWAPPING);
            visualizer.redraw();
            controller.checkpoint();
            visualizer.clearState(k);
            j++;
            k++;
        }

        for (int idx = left; idx <= right; idx++) {
            visualizer.clearState(idx);
        }
        visualizer.redraw();
    }
}

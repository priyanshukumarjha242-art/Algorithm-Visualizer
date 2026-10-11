package com.visualizer.searching.algorithms;

import com.visualizer.core.AnimationController;
import com.visualizer.searching.SearchAlgorithm;
import com.visualizer.searching.SearchElementState;
import com.visualizer.searching.SearchVisualizer;

public class BinarySearch implements SearchAlgorithm {
    @Override
    public String getName() {
        return "Binary Search";
    }

    @Override
    public String getTimeComplexity() {
        return "O(log N)";
    }

    @Override
    public String getSpaceComplexity() {
        return "O(1)";
    }

    @Override
    public boolean requiresSortedArray() {
        return true;
    }

    @Override
    public int search(int[] array, int target, SearchVisualizer visualizer, AnimationController controller) throws InterruptedException {
        int low = 0;
        int high = array.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;

            visualizer.setPointers(low, mid, high);
            visualizer.setState(mid, SearchElementState.CURRENT_CHECK);
            visualizer.redraw();

            controller.registerComparison();
            controller.registerOperation();
            controller.checkpoint();

            if (array[mid] == target) {
                visualizer.markFound(mid);
                visualizer.redraw();
                return mid;
            }

            if (array[mid] < target) {
                // Eliminate left half [low..mid]
                for (int i = low; i <= mid; i++) {
                    visualizer.setState(i, SearchElementState.EXCLUDED);
                }
                low = mid + 1;
            } else {
                // Eliminate right half [mid..high]
                for (int i = mid; i <= high; i++) {
                    visualizer.setState(i, SearchElementState.EXCLUDED);
                }
                high = mid - 1;
            }
            visualizer.redraw();
            controller.checkpoint();
        }

        visualizer.setPointers(-1, -1, -1);
        visualizer.redraw();
        return -1;
    }
}

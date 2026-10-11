package com.visualizer.searching.algorithms;

import com.visualizer.core.AnimationController;
import com.visualizer.searching.SearchAlgorithm;
import com.visualizer.searching.SearchElementState;
import com.visualizer.searching.SearchVisualizer;

public class LinearSearch implements SearchAlgorithm {
    @Override
    public String getName() {
        return "Linear Search";
    }

    @Override
    public String getTimeComplexity() {
        return "O(N)";
    }

    @Override
    public String getSpaceComplexity() {
        return "O(1)";
    }

    @Override
    public boolean requiresSortedArray() {
        return false;
    }

    @Override
    public int search(int[] array, int target, SearchVisualizer visualizer, AnimationController controller) throws InterruptedException {
        for (int i = 0; i < array.length; i++) {
            visualizer.setPointers(-1, i, -1);
            visualizer.setState(i, SearchElementState.CURRENT_CHECK);
            visualizer.redraw();

            controller.registerComparison();
            controller.registerOperation();
            controller.checkpoint();

            if (array[i] == target) {
                visualizer.markFound(i);
                visualizer.redraw();
                return i;
            }

            visualizer.setState(i, SearchElementState.EXCLUDED);
        }
        visualizer.redraw();
        return -1;
    }
}

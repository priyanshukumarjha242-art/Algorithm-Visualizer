package com.visualizer.sorting;

import com.visualizer.core.AnimationController;

/**
 * Strategy interface for all sorting algorithms.
 */
public interface SortingAlgorithm {
    String getName();
    String getTimeComplexity();
    String getSpaceComplexity();

    void sort(int[] array, ArrayVisualizer visualizer, AnimationController controller) throws InterruptedException;
}

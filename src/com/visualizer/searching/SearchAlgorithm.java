package com.visualizer.searching;

import com.visualizer.core.AnimationController;

/**
 * Strategy interface for searching algorithms.
 */
public interface SearchAlgorithm {
    String getName();
    String getTimeComplexity();
    String getSpaceComplexity();
    boolean requiresSortedArray();

    int search(int[] array, int target, SearchVisualizer visualizer, AnimationController controller) throws InterruptedException;
}

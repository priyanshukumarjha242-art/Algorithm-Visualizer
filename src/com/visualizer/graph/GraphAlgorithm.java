package com.visualizer.graph;

import com.visualizer.core.AnimationController;

/**
 * Strategy interface for network graph algorithms (BFS, DFS, Dijkstra).
 */
public interface GraphAlgorithm {
    String getName();
    String getTimeComplexity();
    String getSpaceComplexity();
    boolean requiresTarget();

    void execute(Graph graph, int startId, int targetId,
                 GraphVisualizer visualizer, AnimationController controller) throws InterruptedException;
}

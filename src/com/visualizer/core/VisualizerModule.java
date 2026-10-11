package com.visualizer.core;

/**
 * Interface that all visualizer tabs (Sorting, Searching, Graph/Pathfinding)
 * implement to hook into the main control bar.
 */
public interface VisualizerModule {
    String getModuleName();

    void startVisualization();
    void pauseVisualization();
    void resumeVisualization();
    void resetVisualization();
    void stepForward();

    void setSpeed(int speedPercent);

    boolean isRunning();
    boolean isPaused();

    void addListener(VisualizerListener listener);
}

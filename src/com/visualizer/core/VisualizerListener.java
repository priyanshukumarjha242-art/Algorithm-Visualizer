package com.visualizer.core;

/**
 * Observer listener interface for real-time telemetry updates.
 */
public interface VisualizerListener {
    void onStatusChanged(String status);
    void onMetricsUpdated(long comparisons, long operations, long elapsedTimeMs);
    void onCompleted();
}

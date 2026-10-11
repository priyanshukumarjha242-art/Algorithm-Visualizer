package com.visualizer.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Thread-safe animation controller governing execution playback,
 * speed regulation, stepping, pause/resume, and real-time telemetry dispatch.
 */
public class AnimationController {
    private final Object lock = new Object();
    private volatile boolean running = false;
    private volatile boolean paused = false;
    private volatile boolean stepRequested = false;
    private volatile int delayMs = 25; // Default delay

    private long comparisons = 0;
    private long operations = 0;
    private long startTime = 0;
    private long accumulatedTime = 0;
    private long pauseStartTime = 0;

    private final List<VisualizerListener> listeners = new ArrayList<>();

    public void addListener(VisualizerListener listener) {
        listeners.add(listener);
    }

    public synchronized void start() {
        running = true;
        paused = false;
        stepRequested = false;
        comparisons = 0;
        operations = 0;
        accumulatedTime = 0;
        startTime = System.currentTimeMillis();
        notifyStatus("RUNNING");
        notifyMetrics();
    }

    public void pause() {
        synchronized (lock) {
            if (running && !paused) {
                paused = true;
                pauseStartTime = System.currentTimeMillis();
                accumulatedTime += (pauseStartTime - startTime);
                notifyStatus("PAUSED");
            }
        }
    }

    public void resume() {
        synchronized (lock) {
            if (running && paused) {
                paused = false;
                stepRequested = false;
                startTime = System.currentTimeMillis();
                lock.notifyAll();
                notifyStatus("RUNNING");
            }
        }
    }

    public void step() {
        synchronized (lock) {
            if (running && paused) {
                stepRequested = true;
                lock.notifyAll();
            }
        }
    }

    public void stop() {
        synchronized (lock) {
            running = false;
            paused = false;
            stepRequested = false;
            lock.notifyAll();
        }
    }

    public void reset() {
        stop();
        comparisons = 0;
        operations = 0;
        accumulatedTime = 0;
        notifyStatus("READY");
        notifyMetrics();
    }

    public void complete() {
        synchronized (lock) {
            running = false;
            paused = false;
            notifyStatus("COMPLETED");
            for (VisualizerListener l : listeners) {
                l.onCompleted();
            }
        }
    }

    /**
     * Checkpoint executed between algorithm steps.
     * Handles pausing, stepping, and visual pacing delays.
     */
    public void checkpoint() throws InterruptedException {
        if (!running) {
            throw new InterruptedException("Algorithm execution cancelled.");
        }

        synchronized (lock) {
            while (paused && running) {
                if (stepRequested) {
                    stepRequested = false;
                    break; // allow single step execution
                }
                lock.wait();
            }
        }

        if (!running) {
            throw new InterruptedException("Algorithm execution cancelled.");
        }

        if (delayMs > 0) {
            Thread.sleep(delayMs);
        }

        notifyMetrics();
    }

    public void registerComparison() {
        comparisons++;
    }

    public void registerOperation() {
        operations++;
    }

    public void setDelayMs(int delayMs) {
        this.delayMs = Math.max(0, delayMs);
    }

    public int getDelayMs() {
        return delayMs;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isPaused() {
        return paused;
    }

    private void notifyStatus(String status) {
        for (VisualizerListener l : listeners) {
            l.onStatusChanged(status);
        }
    }

    public void notifyMetrics() {
        long currentElapsed = accumulatedTime;
        if (running && !paused && startTime > 0) {
            currentElapsed += (System.currentTimeMillis() - startTime);
        }
        for (VisualizerListener l : listeners) {
            l.onMetricsUpdated(comparisons, operations, currentElapsed);
        }
    }
}

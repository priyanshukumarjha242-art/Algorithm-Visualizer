package com.visualizer.sorting;

public interface ArrayVisualizer {
    void setBarState(int index, BarState state);
    void setBarStates(int index1, int index2, BarState state);
    void clearState(int index);
    void clearAllStates();
    void markSorted(int index);
    void markAllSorted();
    void redraw();
}

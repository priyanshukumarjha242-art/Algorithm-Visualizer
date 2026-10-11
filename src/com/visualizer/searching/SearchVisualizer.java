package com.visualizer.searching;

import com.visualizer.core.AnimationController;

public interface SearchVisualizer {
    void setState(int index, SearchElementState state);
    void setPointers(int low, int mid, int high);
    void markFound(int index);
    void clearAllStates();
    void redraw();
}

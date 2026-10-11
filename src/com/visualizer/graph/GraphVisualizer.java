package com.visualizer.graph;

/**
 * Interface through which graph algorithms trigger visual updates.
 */
public interface GraphVisualizer {
    void setNodeState(int nodeId, NodeState state);
    void setEdgeState(int uId, int vId, EdgeState state);
    void clearNodeStates();
    void clearEdgeStates();
    void setPathResult(String resultText);
    void redraw();
}

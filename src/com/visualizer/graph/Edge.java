package com.visualizer.graph;

/**
 * Represents a straight-line connection between two nodes.
 */
public class Edge {
    public final Node u;
    public final Node v;
    public final int weight;
    public final boolean directed;
    public EdgeState state = EdgeState.DEFAULT;

    public Edge(Node u, Node v, int weight, boolean directed) {
        this.u = u;
        this.v = v;
        this.weight = weight;
        this.directed = directed;
    }

    public boolean connects(int uId, int vId) {
        if (directed) {
            return u.id == uId && v.id == vId;
        } else {
            return (u.id == uId && v.id == vId) || (u.id == vId && v.id == uId);
        }
    }

    public void reset() {
        this.state = EdgeState.DEFAULT;
    }
}

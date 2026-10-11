package com.visualizer.graph;

/**
 * Represents a circular vertex in a network graph.
 */
public class Node implements Comparable<Node> {
    public final int id;
    public int x;
    public int y;
    public static final int RADIUS = 22;

    public NodeState state = NodeState.DEFAULT;
    public double distance = Double.POSITIVE_INFINITY;
    public Node parent = null;

    public Node(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public void reset() {
        state = NodeState.DEFAULT;
        distance = Double.POSITIVE_INFINITY;
        parent = null;
    }

    public boolean contains(int px, int py) {
        int dx = px - x;
        int dy = py - y;
        return (dx * dx + dy * dy) <= (RADIUS * RADIUS);
    }

    @Override
    public int compareTo(Node other) {
        return Double.compare(this.distance, other.distance);
    }

    @Override
    public String toString() {
        return "Node " + id;
    }
}

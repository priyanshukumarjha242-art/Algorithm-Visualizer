package com.visualizer.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Manages the collection of Nodes and Edges in a network graph.
 */
public class Graph {
    private final List<Node> nodes = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();

    public synchronized void addNode(Node node) {
        nodes.add(node);
    }

    public synchronized void addEdge(Node u, Node v, int weight, boolean directed) {
        // Prevent duplicate edges
        for (Edge e : edges) {
            if (e.u == u && e.v == v && e.directed == directed) return;
            if (!directed && !e.directed && ((e.u == u && e.v == v) || (e.u == v && e.v == u))) return;
        }
        edges.add(new Edge(u, v, weight, directed));
    }

    public synchronized List<Node> getNodes() {
        return new ArrayList<>(nodes);
    }

    public synchronized List<Edge> getEdges() {
        return new ArrayList<>(edges);
    }

    public synchronized Node getNode(int id) {
        for (Node n : nodes) {
            if (n.id == id) return n;
        }
        return null;
    }

    public synchronized List<Edge> getOutgoingEdges(Node u) {
        List<Edge> result = new ArrayList<>();
        for (Edge e : edges) {
            if (e.directed) {
                if (e.u.id == u.id) result.add(e);
            } else {
                if (e.u.id == u.id || e.v.id == u.id) result.add(e);
            }
        }
        return result;
    }

    public synchronized void resetStates() {
        for (Node n : nodes) n.reset();
        for (Edge e : edges) e.reset();
    }

    public synchronized void clear() {
        nodes.clear();
        edges.clear();
    }

    /**
     * Generates a well-spaced random graph with guaranteed connectivity.
     */
    public synchronized void generateRandom(int count, boolean directed, boolean weighted, int width, int height) {
        clear();
        if (count < 2) count = 6;
        Random rand = new Random();

        int marginX = 80;
        int marginY = 80;
        int usableW = Math.max(300, width - 2 * marginX);
        int usableH = Math.max(200, height - 2 * marginY);

        // Position nodes in an elliptical / circle layout with slight jitter for clean aesthetics
        double centerX = marginX + usableW / 2.0;
        double centerY = marginY + usableH / 2.0;
        double radiusX = usableW * 0.40;
        double radiusY = usableH * 0.38;

        for (int i = 0; i < count; i++) {
            double angle = (2 * Math.PI * i) / count;
            int x = (int) (centerX + radiusX * Math.cos(angle) + (rand.nextInt(30) - 15));
            int y = (int) (centerY + radiusY * Math.sin(angle) + (rand.nextInt(30) - 15));
            addNode(new Node(i, x, y));
        }

        // Connect nodes to guarantee connectivity
        for (int i = 0; i < count - 1; i++) {
            Node u = nodes.get(i);
            Node v = nodes.get(i + 1);
            int w = weighted ? (rand.nextInt(12) + 2) : 1;
            addEdge(u, v, w, directed);
        }

        // Add additional random cross edges
        int extraEdges = Math.max(2, count / 2);
        for (int k = 0; k < extraEdges; k++) {
            int i = rand.nextInt(count);
            int j = rand.nextInt(count);
            if (i != j) {
                Node u = nodes.get(i);
                Node v = nodes.get(j);
                int w = weighted ? (rand.nextInt(14) + 1) : 1;
                addEdge(u, v, w, directed);
            }
        }
    }
}

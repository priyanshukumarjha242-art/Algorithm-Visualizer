package com.visualizer.graph.algorithms;

import com.visualizer.core.AnimationController;
import com.visualizer.graph.*;

import java.util.*;

public class Dijkstra implements GraphAlgorithm {
    @Override
    public String getName() {
        return "Dijkstra's Algorithm";
    }

    @Override
    public String getTimeComplexity() {
        return "O((V + E) log V)";
    }

    @Override
    public String getSpaceComplexity() {
        return "O(V)";
    }

    @Override
    public boolean requiresTarget() {
        return true;
    }

    @Override
    public void execute(Graph graph, int startId, int targetId,
                        GraphVisualizer visualizer, AnimationController controller) throws InterruptedException {
        Node startNode = graph.getNode(startId);
        Node targetNode = graph.getNode(targetId);
        if (startNode == null || targetNode == null) return;

        // Reset all nodes
        for (Node n : graph.getNodes()) {
            n.distance = Double.POSITIVE_INFINITY;
            n.parent = null;
        }

        startNode.distance = 0;
        PriorityQueue<Node> pq = new PriorityQueue<>();
        pq.add(startNode);

        Set<Integer> visited = new HashSet<>();
        boolean targetFound = false;

        visualizer.setNodeState(startNode.id, NodeState.START);
        if (startId != targetId) {
            visualizer.setNodeState(targetNode.id, NodeState.TARGET);
        }
        visualizer.redraw();
        controller.checkpoint();

        while (!pq.isEmpty()) {
            Node current = pq.poll();

            if (visited.contains(current.id)) continue;
            visited.add(current.id);

            controller.registerOperation();

            if (current.id != startId && current.id != targetId) {
                visualizer.setNodeState(current.id, NodeState.CURRENT);
                visualizer.redraw();
                controller.checkpoint();
            }

            if (current.id == targetId) {
                targetFound = true;
                break;
            }

            List<Edge> edges = graph.getOutgoingEdges(current);
            for (Edge edge : edges) {
                Node neighbor = (edge.u.id == current.id) ? edge.v : edge.u;

                // Animate edge traversal
                visualizer.setEdgeState(current.id, neighbor.id, EdgeState.TRAVERSING);
                visualizer.redraw();
                controller.registerComparison();
                controller.checkpoint();

                if (!visited.contains(neighbor.id)) {
                    double newDist = current.distance + edge.weight;
                    if (newDist < neighbor.distance) {
                        neighbor.distance = newDist;
                        neighbor.parent = current;

                        if (neighbor.id != startId && neighbor.id != targetId) {
                            visualizer.setNodeState(neighbor.id, NodeState.VISITING);
                        }
                        pq.add(neighbor);
                    }
                }

                // Reset edge state
                visualizer.setEdgeState(current.id, neighbor.id, EdgeState.DEFAULT);
                visualizer.redraw();
            }

            if (current.id != startId && current.id != targetId) {
                visualizer.setNodeState(current.id, NodeState.VISITED);
                visualizer.redraw();
                controller.checkpoint();
            }
        }

        // Reconstruct shortest path & compute path sum
        if (targetFound && targetNode.distance < Double.POSITIVE_INFINITY) {
            List<Node> path = new ArrayList<>();
            Node curr = targetNode;
            while (curr != null) {
                path.add(0, curr);
                curr = curr.parent;
            }

            // Animate shortest path nodes and edges in glowing green/amber
            for (int i = 0; i < path.size(); i++) {
                Node n = path.get(i);
                visualizer.setNodeState(n.id, NodeState.IN_PATH);
                if (i > 0) {
                    Node prev = path.get(i - 1);
                    visualizer.setEdgeState(prev.id, n.id, EdgeState.IN_PATH);
                }
                visualizer.redraw();
                controller.checkpoint();
            }

            int totalSum = (int) targetNode.distance;
            StringBuilder sb = new StringBuilder("🏆 Shortest Path: ");
            for (int i = 0; i < path.size(); i++) {
                sb.append("Node ").append(path.get(i).id);
                if (i < path.size() - 1) sb.append(" ➔ ");
            }
            sb.append(String.format(" | Total Path Sum (Weight): %d", totalSum));
            visualizer.setPathResult(sb.toString());
        } else {
            visualizer.setPathResult(String.format("⚠️ No Path exists from Node %d to Node %d", startId, targetId));
        }
    }
}

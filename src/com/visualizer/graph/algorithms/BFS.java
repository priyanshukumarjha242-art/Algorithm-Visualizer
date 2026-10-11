package com.visualizer.graph.algorithms;

import com.visualizer.core.AnimationController;
import com.visualizer.graph.*;

import java.util.*;

public class BFS implements GraphAlgorithm {
    @Override
    public String getName() {
        return "Breadth-First Search (BFS)";
    }

    @Override
    public String getTimeComplexity() {
        return "O(V + E)";
    }

    @Override
    public String getSpaceComplexity() {
        return "O(V)";
    }

    @Override
    public boolean requiresTarget() {
        return false;
    }

    @Override
    public void execute(Graph graph, int startId, int targetId,
                        GraphVisualizer visualizer, AnimationController controller) throws InterruptedException {
        Node startNode = graph.getNode(startId);
        if (startNode == null) return;

        Set<Integer> visited = new HashSet<>();
        Queue<Node> queue = new LinkedList<>();
        List<Integer> traversalOrder = new ArrayList<>();

        queue.add(startNode);
        visited.add(startNode.id);
        visualizer.setNodeState(startNode.id, NodeState.START);
        visualizer.redraw();
        controller.checkpoint();

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            traversalOrder.add(current.id);

            visualizer.setNodeState(current.id, NodeState.CURRENT);
            visualizer.redraw();
            controller.registerOperation();
            controller.checkpoint();

            List<Edge> edges = graph.getOutgoingEdges(current);
            for (Edge edge : edges) {
                Node neighbor = (edge.u.id == current.id) ? edge.v : edge.u;

                // Animate edge traversal
                visualizer.setEdgeState(current.id, neighbor.id, EdgeState.TRAVERSING);
                visualizer.redraw();
                controller.registerComparison();
                controller.checkpoint();

                if (!visited.contains(neighbor.id)) {
                    visited.add(neighbor.id);
                    visualizer.setNodeState(neighbor.id, NodeState.VISITING);
                    visualizer.redraw();
                    controller.checkpoint();

                    queue.add(neighbor);
                }

                // Restore edge state
                visualizer.setEdgeState(current.id, neighbor.id, EdgeState.DEFAULT);
                visualizer.redraw();
            }

            visualizer.setNodeState(current.id, NodeState.VISITED);
            visualizer.redraw();
            controller.checkpoint();
        }

        // Display traversal result
        StringBuilder sb = new StringBuilder("BFS Traversal: ");
        for (int i = 0; i < traversalOrder.size(); i++) {
            sb.append(traversalOrder.get(i));
            if (i < traversalOrder.size() - 1) sb.append(" ➔ ");
        }
        sb.append(String.format(" (Visited %d/%d nodes)", visited.size(), graph.getNodes().size()));
        visualizer.setPathResult(sb.toString());
    }
}

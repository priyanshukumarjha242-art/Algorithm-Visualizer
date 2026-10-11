package com.visualizer.graph;

import com.visualizer.ui.Theme;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

/**
 * Custom Graphics2D canvas rendering a network graph of circular vertices,
 * straight edges, arrowheads, weights, and real-time traversal animations.
 */
public class GraphCanvas extends JPanel implements GraphVisualizer {
    private Graph graph;
    private Node draggedNode = null;
    private String pathResultText = "";
    private final Object renderLock = new Object();

    public GraphCanvas() {
        setBackground(Theme.BG_DARK);
        setOpaque(true);
        this.graph = new Graph();

        setupMouseInteraction();
    }

    public void setGraph(Graph graph) {
        synchronized (renderLock) {
            this.graph = graph;
            this.pathResultText = "";
        }
        repaint();
    }

    public Graph getGraph() {
        return graph;
    }

    private void setupMouseInteraction() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                synchronized (renderLock) {
                    for (Node n : graph.getNodes()) {
                        if (n.contains(e.getX(), e.getY())) {
                            draggedNode = n;
                            break;
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                draggedNode = null;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggedNode != null) {
                    synchronized (renderLock) {
                        draggedNode.x = Math.max(Node.RADIUS + 10, Math.min(getWidth() - Node.RADIUS - 10, e.getX()));
                        draggedNode.y = Math.max(Node.RADIUS + 10, Math.min(getHeight() - Node.RADIUS - 10, e.getY()));
                    }
                    repaint();
                }
            }
        });
    }

    @Override
    public void setNodeState(int nodeId, NodeState state) {
        synchronized (renderLock) {
            Node n = graph.getNode(nodeId);
            if (n != null) n.state = state;
        }
    }

    @Override
    public void setEdgeState(int uId, int vId, EdgeState state) {
        synchronized (renderLock) {
            for (Edge e : graph.getEdges()) {
                if (e.connects(uId, vId)) {
                    e.state = state;
                }
            }
        }
    }

    @Override
    public void clearNodeStates() {
        synchronized (renderLock) {
            for (Node n : graph.getNodes()) n.state = NodeState.DEFAULT;
        }
    }

    @Override
    public void clearEdgeStates() {
        synchronized (renderLock) {
            for (Edge e : graph.getEdges()) e.state = EdgeState.DEFAULT;
        }
    }

    @Override
    public void setPathResult(String resultText) {
        synchronized (renderLock) {
            this.pathResultText = resultText;
        }
        repaint();
    }

    @Override
    public void redraw() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int panelW = getWidth();
        int panelH = getHeight();

        // Background
        g2.setColor(Theme.BG_DARK);
        g2.fillRect(0, 0, panelW, panelH);

        synchronized (renderLock) {
            if (graph == null || graph.getNodes().isEmpty()) {
                g2.setColor(Theme.TEXT_MUTED);
                g2.setFont(Theme.FONT_TITLE);
                String msg = "Generate a graph to begin visualization";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(msg, (panelW - fm.stringWidth(msg)) / 2, panelH / 2);
                g2.dispose();
                return;
            }

            // 1. Draw Edges first (so nodes sit on top of edge lines)
            for (Edge edge : graph.getEdges()) {
                drawEdge(g2, edge);
            }

            // 2. Draw Nodes (circles with numbers)
            for (Node node : graph.getNodes()) {
                drawNode(g2, node);
            }

            // 3. Draw Path Result Banner if available
            if (pathResultText != null && !pathResultText.isEmpty()) {
                drawResultBanner(g2, panelW);
            }
        }

        g2.dispose();
    }

    private void drawEdge(Graphics2D g2, Edge edge) {
        Node u = edge.u;
        Node v = edge.v;

        double dx = v.x - u.x;
        double dy = v.y - u.y;
        double dist = Math.hypot(dx, dy);
        if (dist <= 2 * Node.RADIUS) return;

        double unitX = dx / dist;
        double unitY = dy / dist;

        // Truncate line endpoints to node circumference
        double startX = u.x + unitX * Node.RADIUS;
        double startY = u.y + unitY * Node.RADIUS;
        double endX = v.x - unitX * Node.RADIUS;
        double endY = v.y - unitY * Node.RADIUS;

        // Determine edge color & stroke
        Color edgeColor;
        float strokeWidth;

        switch (edge.state) {
            case TRAVERSING:
                edgeColor = Theme.COLOR_COMPARING; // Amber glow
                strokeWidth = 3.8f;
                break;
            case IN_PATH:
                edgeColor = Theme.COLOR_SORTED;     // Emerald green in path
                strokeWidth = 4.2f;
                break;
            case DEFAULT:
            default:
                edgeColor = new Color(0x47, 0x55, 0x69); // Slate line
                strokeWidth = 2.0f;
                break;
        }

        g2.setColor(edgeColor);
        g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(new Line2D.Double(startX, startY, endX, endY));

        // Draw Arrowhead for directed edges
        if (edge.directed) {
            drawArrowHead(g2, endX, endY, unitX, unitY, edgeColor);
        }

        // Draw Weight Badge if edge has a weight > 1 or graph is weighted
        if (edge.weight > 1 || edge.directed) {
            drawWeightBadge(g2, (startX + endX) / 2.0, (startY + endY) / 2.0, edge.weight);
        }
    }

    private void drawArrowHead(Graphics2D g2, double tipX, double tipY, double unitX, double unitY, Color color) {
        int arrowSize = 10;
        double normalX = -unitY;
        double normalY = unitX;

        double baseX = tipX - unitX * arrowSize;
        double baseY = tipY - unitY * arrowSize;

        int[] xPoints = {
            (int) tipX,
            (int) (baseX + normalX * (arrowSize * 0.6)),
            (int) (baseX - normalX * (arrowSize * 0.6))
        };
        int[] yPoints = {
            (int) tipY,
            (int) (baseY + normalY * (arrowSize * 0.6)),
            (int) (baseY - normalY * (arrowSize * 0.6))
        };

        g2.setColor(color);
        g2.fillPolygon(xPoints, yPoints, 3);
    }

    private void drawWeightBadge(Graphics2D g2, double midX, double midY, int weight) {
        String text = String.valueOf(weight);
        g2.setFont(Theme.FONT_MONO);
        FontMetrics fm = g2.getFontMetrics();
        int textW = fm.stringWidth(text);
        int textH = fm.getAscent();

        int badgeW = Math.max(22, textW + 10);
        int badgeH = 18;
        int bx = (int) (midX - badgeW / 2.0);
        int by = (int) (midY - badgeH / 2.0);

        // Dark pill background
        g2.setColor(Theme.SURFACE_CARD);
        g2.fillRoundRect(bx, by, badgeW, badgeH, 6, 6);
        g2.setColor(Theme.BORDER_COLOR.brighter());
        g2.drawRoundRect(bx, by, badgeW, badgeH, 6, 6);

        // White weight text
        g2.setColor(Theme.TEXT_PRIMARY);
        g2.drawString(text, (int) (midX - textW / 2.0), (int) (midY + textH / 2.0 - 2));
    }

    private void drawNode(Graphics2D g2, Node node) {
        int r = Node.RADIUS;
        int d = r * 2;
        int x = node.x - r;
        int y = node.y - r;

        Color topColor;
        Color bottomColor;
        Color borderColor;

        switch (node.state) {
            case START:
                topColor = Theme.COLOR_SORTED.brighter();
                bottomColor = Theme.COLOR_SORTED.darker();
                borderColor = Color.WHITE;
                break;
            case TARGET:
                topColor = Theme.COLOR_SWAPPING.brighter();
                bottomColor = Theme.COLOR_SWAPPING.darker();
                borderColor = Color.WHITE;
                break;
            case CURRENT:
                topColor = Theme.COLOR_PIVOT.brighter();
                bottomColor = Theme.COLOR_PIVOT.darker();
                borderColor = Color.WHITE;
                break;
            case VISITING:
                topColor = Theme.COLOR_COMPARING.brighter();
                bottomColor = Theme.COLOR_COMPARING.darker();
                borderColor = Color.WHITE;
                break;
            case VISITED:
                topColor = Theme.COLOR_BAR_DEFAULT.brighter();
                bottomColor = Theme.COLOR_BAR_DEFAULT.darker();
                borderColor = Theme.ACCENT_CYAN;
                break;
            case IN_PATH:
                topColor = Theme.COLOR_SORTED.brighter();
                bottomColor = Theme.COLOR_SORTED.darker();
                borderColor = new Color(0xF5, 0x9E, 0x0B);
                break;
            case DEFAULT:
            default:
                topColor = new Color(0x28, 0x2D, 0x40);
                bottomColor = new Color(0x18, 0x1A, 0x27);
                borderColor = new Color(0x64, 0x74, 0x8B);
                break;
        }

        // Fill circle with gradient
        GradientPaint grad = new GradientPaint(x, y, topColor, x, y + d, bottomColor);
        g2.setPaint(grad);
        g2.fillOval(x, y, d, d);

        // Border
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawOval(x, y, d, d);

        // Number centered inside the circle
        g2.setFont(Theme.FONT_TITLE);
        g2.setColor(Theme.TEXT_PRIMARY);
        String label = String.valueOf(node.id);
        FontMetrics fm = g2.getFontMetrics();
        int strW = fm.stringWidth(label);
        int strH = fm.getAscent();
        g2.drawString(label, node.x - strW / 2, node.y + strH / 2 - 3);
    }

    private void drawResultBanner(Graphics2D g2, int panelW) {
        g2.setFont(Theme.FONT_BOLD);
        FontMetrics fm = g2.getFontMetrics();

        boolean isShortestPath = pathResultText.contains("Shortest Path");
        boolean isTraversal = pathResultText.contains("Traversal");
        boolean isNoPath = pathResultText.contains("No Path");

        // Split text into main details if sum exists
        String badge = isShortestPath ? "SHORTEST PATH" : (isTraversal ? (pathResultText.contains("BFS") ? "BFS" : "DFS") : "RESULT");
        Color badgeColor = isShortestPath ? Theme.COLOR_SORTED : (isNoPath ? Theme.COLOR_SWAPPING : Theme.ACCENT_CYAN);

        int textW = fm.stringWidth(pathResultText);
        int bannerW = Math.max(480, textW + 48);
        int bannerH = 44;
        int bx = (panelW - bannerW) / 2;
        int by = 16;

        // Shadow
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(bx + 2, by + 2, bannerW, bannerH, 12, 12);

        // Dark card background
        g2.setColor(new Color(0x10, 0x10, 0x10, 245));
        g2.fillRoundRect(bx, by, bannerW, bannerH, 12, 12);
        g2.setColor(new Color(0x33, 0x33, 0x33));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(bx, by, bannerW, bannerH, 12, 12);

        // Left Category Tag
        g2.setColor(badgeColor);
        g2.fillRoundRect(bx + 12, by + 10, 8, bannerH - 20, 4, 4);

        // Content Text
        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g2.setColor(Color.WHITE);
        g2.drawString(pathResultText, bx + 28, by + 27);
    }
}

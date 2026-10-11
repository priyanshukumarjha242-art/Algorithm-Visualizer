package com.visualizer.ui.components;

import com.visualizer.ui.Theme;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Minimalist dark metric card displaying telemetry status and numbers.
 */
public class StatusCard extends JPanel {
    private final JLabel titleLabel;
    private final JLabel valueLabel;
    private final int cornerRadius = 8;
    private Color accentColor = Color.WHITE;

    public StatusCard(String title, String initialValue) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setBorder(new EmptyBorder(8, 12, 8, 12));
        setPreferredSize(new Dimension(135, 52));

        titleLabel = new JLabel(title.toUpperCase());
        titleLabel.setFont(Theme.FONT_REGULAR);
        titleLabel.setForeground(Theme.TEXT_MUTED);

        valueLabel = new JLabel(initialValue);
        valueLabel.setFont(Theme.FONT_STAT_VALUE);
        valueLabel.setForeground(Color.WHITE);

        add(titleLabel);
        add(valueLabel);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }

    public void setAccentColor(Color color) {
        this.accentColor = color;
        valueLabel.setForeground(color);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill background
        g2.setColor(Theme.SURFACE_CARD);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

        // Border
        g2.setColor(Theme.BORDER_COLOR);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);

        // Left accent indicator line
        g2.setColor(accentColor);
        g2.fillRoundRect(0, 8, 3, getHeight() - 16, 2, 2);

        g2.dispose();
        super.paintComponent(g);
    }
}

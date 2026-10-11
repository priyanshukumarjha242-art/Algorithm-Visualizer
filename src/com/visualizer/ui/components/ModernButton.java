package com.visualizer.ui.components;

import com.visualizer.ui.Theme;

import javax.swing.JButton;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Premium minimalist button with subtle borders, smooth hover animations,
 * and high-contrast typography.
 */
public class ModernButton extends JButton {
    private Color normalBg;
    private Color hoverBg;
    private Color normalBorder;
    private Color hoverBorder;
    private boolean isHovered = false;
    private boolean isPressed = false;
    private int cornerRadius = 8;

    public ModernButton(String text) {
        this(text, new Color(0x18, 0x18, 0x18), new Color(0x28, 0x28, 0x28), Color.WHITE);
    }

    public ModernButton(String text, Color bg, Color hover) {
        this(text, bg, hover, Color.WHITE);
    }

    public ModernButton(String text, Color bg, Color hover, Color textCol) {
        super(text);
        this.normalBg = bg;
        this.hoverBg = hover;
        this.normalBorder = new Color(0x2E, 0x2E, 0x2E);
        this.hoverBorder = new Color(0x55, 0x55, 0x55);

        setFont(Theme.FONT_BOLD);
        setForeground(textCol);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    isHovered = true;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    isPressed = true;
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    public void setCustomColors(Color normal, Color hover, Color border, Color text) {
        this.normalBg = normal;
        this.hoverBg = hover;
        this.normalBorder = border;
        setForeground(text);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color bg;
        Color border;

        if (!isEnabled()) {
            bg = new Color(0x12, 0x12, 0x12);
            border = new Color(0x20, 0x20, 0x20);
        } else if (isPressed) {
            bg = normalBg.darker();
            border = hoverBorder;
        } else if (isHovered) {
            bg = hoverBg;
            border = hoverBorder;
        } else {
            bg = normalBg;
            border = normalBorder;
        }

        // Fill background
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

        // Draw refined border
        g2.setColor(border);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);

        g2.dispose();
        super.paintComponent(g);
    }
}

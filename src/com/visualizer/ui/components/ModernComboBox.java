package com.visualizer.ui.components;

import com.visualizer.ui.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;

/**
 * Premium custom combo box with dark background, crisp white typography,
 * custom vector chevron arrow, and high-contrast popup renderer.
 */
public class ModernComboBox<E> extends JComboBox<E> {

    public ModernComboBox(E[] items) {
        super(items);
        initStyle();
    }

    public ModernComboBox() {
        super();
        initStyle();
    }

    private void initStyle() {
        setFont(Theme.FONT_REGULAR);
        setBackground(new Color(0x14, 0x14, 0x14));
        setForeground(Color.WHITE);
        setFocusable(false);
        setOpaque(true);

        setUI(new ModernComboBoxUI());
        setRenderer(new ModernComboRenderer<>());
    }

    private static class ModernComboBoxUI extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton btn = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    g2.setColor(new Color(0x14, 0x14, 0x14));
                    g2.fillRect(0, 0, getWidth(), getHeight());

                    // Draw minimalist chevron
                    int cx = getWidth() / 2 - 2;
                    int cy = getHeight() / 2 - 1;
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(cx - 4, cy - 2, cx, cy + 2);
                    g2.drawLine(cx, cy + 2, cx + 4, cy - 2);

                    g2.dispose();
                }
            };
            btn.setBorder(BorderFactory.createEmptyBorder());
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setOpaque(true);
            btn.setBackground(new Color(0x14, 0x14, 0x14));
            return btn;
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(0x14, 0x14, 0x14));
            g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            g2.dispose();
        }

        @Override
        protected ComboPopup createPopup() {
            BasicComboPopup popup = new BasicComboPopup(comboBox) {
                @Override
                protected JScrollPane createScroller() {
                    JScrollPane scroller = super.createScroller();
                    scroller.setBorder(BorderFactory.createLineBorder(new Color(0x33, 0x33, 0x33), 1));
                    scroller.setBackground(new Color(0x14, 0x14, 0x14));
                    scroller.getViewport().setBackground(new Color(0x14, 0x14, 0x14));
                    return scroller;
                }
            };
            popup.setBorder(BorderFactory.createLineBorder(new Color(0x33, 0x33, 0x33), 1));
            return popup;
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            super.paint(g, c);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(0x2E, 0x2E, 0x2E));
            g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 6, 6);
            g2.dispose();
        }
    }

    private static class ModernComboRenderer<T> extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            setFont(Theme.FONT_REGULAR);
            setBorder(new EmptyBorder(6, 10, 6, 10));

            if (isSelected) {
                setBackground(new Color(0x28, 0x28, 0x28));
                setForeground(Color.WHITE);
            } else {
                setBackground(new Color(0x14, 0x14, 0x14));
                setForeground(Color.WHITE);
            }

            return this;
        }
    }
}

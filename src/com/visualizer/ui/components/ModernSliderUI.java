package com.visualizer.ui.components;

import com.visualizer.ui.Theme;

import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Premium custom slider UI with a slim progress track and tactile circular thumb.
 */
public class ModernSliderUI extends BasicSliderUI {
    private final Color trackBg = new Color(0x26, 0x26, 0x26);
    private final Color trackProgress = Theme.ACCENT_CYAN;
    private final Color thumbColor = Color.WHITE;

    public ModernSliderUI(JSlider slider) {
        super(slider);
    }

    @Override
    public void paintFocus(Graphics g) {
        // No dotted focus rectangle
    }

    @Override
    public void paintTrack(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Rectangle trackBounds = trackRect;
        int trackHeight = 4;
        int cy = trackBounds.y + (trackBounds.height - trackHeight) / 2;

        // Background track
        g2.setColor(trackBg);
        g2.fill(new RoundRectangle2D.Float(trackBounds.x, cy, trackBounds.width, trackHeight, 4, 4));

        // Filled progress track
        int thumbMidX = thumbRect.x + thumbRect.width / 2;
        int progressWidth = Math.max(0, thumbMidX - trackBounds.x);
        g2.setColor(trackProgress);
        g2.fill(new RoundRectangle2D.Float(trackBounds.x, cy, progressWidth, trackHeight, 4, 4));

        g2.dispose();
    }

    @Override
    public void paintThumb(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int thumbSize = 14;
        int x = thumbRect.x + (thumbRect.width - thumbSize) / 2;
        int y = thumbRect.y + (thumbRect.height - thumbSize) / 2;

        // Outer glow/shadow
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillOval(x, y + 1, thumbSize, thumbSize);

        // Thumb circle
        g2.setColor(thumbColor);
        g2.fillOval(x, y, thumbSize, thumbSize);

        // Subtle dark border
        g2.setColor(new Color(0x22, 0x22, 0x22));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(x, y, thumbSize, thumbSize);

        g2.dispose();
    }
}

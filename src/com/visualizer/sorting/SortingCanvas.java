package com.visualizer.sorting;

import com.visualizer.ui.Theme;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Arrays;

/**
 * High-performance, anti-aliased custom Graphics2D canvas for rendering array bars,
 * state highlights, gradients, and numerical labels.
 */
public class SortingCanvas extends JPanel implements ArrayVisualizer {
    private int[] array;
    private BarState[] states;
    private final Object renderLock = new Object();

    public SortingCanvas() {
        setBackground(Theme.BG_DARK);
        setOpaque(true);
    }

    public void setArray(int[] newArray) {
        synchronized (renderLock) {
            this.array = Arrays.copyOf(newArray, newArray.length);
            this.states = new BarState[newArray.length];
            Arrays.fill(this.states, BarState.DEFAULT);
        }
        repaint();
    }

    public int[] getArrayCopy() {
        synchronized (renderLock) {
            return array != null ? Arrays.copyOf(array, array.length) : new int[0];
        }
    }

    public int[] getInternalArray() {
        synchronized (renderLock) {
            return this.array;
        }
    }

    @Override
    public void setBarState(int index, BarState state) {
        synchronized (renderLock) {
            if (states != null && index >= 0 && index < states.length) {
                states[index] = state;
            }
        }
    }

    @Override
    public void setBarStates(int index1, int index2, BarState state) {
        synchronized (renderLock) {
            if (states != null) {
                if (index1 >= 0 && index1 < states.length) states[index1] = state;
                if (index2 >= 0 && index2 < states.length) states[index2] = state;
            }
        }
    }

    @Override
    public void clearState(int index) {
        synchronized (renderLock) {
            if (states != null && index >= 0 && index < states.length) {
                if (states[index] != BarState.SORTED) {
                    states[index] = BarState.DEFAULT;
                }
            }
        }
    }

    @Override
    public void clearAllStates() {
        synchronized (renderLock) {
            if (states != null) {
                Arrays.fill(states, BarState.DEFAULT);
            }
        }
    }

    @Override
    public void markSorted(int index) {
        synchronized (renderLock) {
            if (states != null && index >= 0 && index < states.length) {
                states[index] = BarState.SORTED;
            }
        }
    }

    @Override
    public void markAllSorted() {
        synchronized (renderLock) {
            if (states != null) {
                Arrays.fill(states, BarState.SORTED);
            }
        }
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

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // Draw background
        g2.setColor(Theme.BG_DARK);
        g2.fillRect(0, 0, panelWidth, panelHeight);

        synchronized (renderLock) {
            if (array == null || array.length == 0) {
                g2.setColor(Theme.TEXT_MUTED);
                g2.setFont(Theme.FONT_TITLE);
                String msg = "Generate an array to begin visualization";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(msg, (panelWidth - fm.stringWidth(msg)) / 2, panelHeight / 2);
                g2.dispose();
                return;
            }

            int n = array.length;
            int maxVal = 1;
            for (int val : array) {
                if (val > maxVal) maxVal = val;
            }

            int paddingX = 24;
            int paddingTop = 40;
            int paddingBottom = 40;
            int availableWidth = panelWidth - (2 * paddingX);
            int availableHeight = panelHeight - paddingTop - paddingBottom;

            double barWidthWithGap = (double) availableWidth / n;
            double gap = Math.max(1, barWidthWithGap * 0.15);
            if (barWidthWithGap <= 3) gap = 0;
            double barWidth = Math.max(1, barWidthWithGap - gap);

            for (int i = 0; i < n; i++) {
                int value = array[i];
                double barHeight = ((double) value / maxVal) * availableHeight;
                double x = paddingX + (i * barWidthWithGap);
                double y = panelHeight - paddingBottom - barHeight;

                BarState state = (states != null && i < states.length) ? states[i] : BarState.DEFAULT;
                Color baseColor = getColorForState(state);

                // Draw gradient bar
                Color topColor = lightenColor(baseColor, 1.25f);
                Color bottomColor = darkenColor(baseColor, 0.75f);
                GradientPaint gradient = new GradientPaint((float) x, (float) y, topColor, (float) x, (float) (y + barHeight), bottomColor);

                g2.setPaint(gradient);
                int cornerArc = Math.min(6, (int) barWidth);
                g2.fillRoundRect((int) x, (int) y, (int) barWidth, (int) barHeight, cornerArc, cornerArc);

                // Bar outline
                g2.setColor(baseColor.brighter());
                g2.drawRoundRect((int) x, (int) y, (int) barWidth, (int) barHeight, cornerArc, cornerArc);

                // If bar is wide enough, display numerical label
                if (barWidth >= 22) {
                    g2.setFont(Theme.FONT_MONO);
                    g2.setColor(Theme.TEXT_PRIMARY);
                    String valStr = String.valueOf(value);
                    FontMetrics fm = g2.getFontMetrics();
                    int textX = (int) (x + (barWidth - fm.stringWidth(valStr)) / 2);
                    int textY = (int) y - 6;
                    if (textY < 18) textY = (int) (y + 16);
                    g2.drawString(valStr, textX, textY);
                }
            }
        }

        g2.dispose();
    }

    private Color getColorForState(BarState state) {
        if (state == null) return Theme.COLOR_BAR_DEFAULT;
        switch (state) {
            case COMPARING: return Theme.COLOR_COMPARING;
            case SWAPPING: return Theme.COLOR_SWAPPING;
            case SORTED: return Theme.COLOR_SORTED;
            case PIVOT: return Theme.COLOR_PIVOT;
            case SELECTED: return Theme.ACCENT_CYAN;
            case DEFAULT:
            default:
                return Theme.COLOR_BAR_DEFAULT;
        }
    }

    private Color lightenColor(Color c, float factor) {
        int r = Math.min(255, (int) (c.getRed() * factor));
        int g = Math.min(255, (int) (c.getGreen() * factor));
        int b = Math.min(255, (int) (c.getBlue() * factor));
        return new Color(r, g, b);
    }

    private Color darkenColor(Color c, float factor) {
        int r = Math.max(0, (int) (c.getRed() * factor));
        int g = Math.max(0, (int) (c.getGreen() * factor));
        int b = Math.max(0, (int) (c.getBlue() * factor));
        return new Color(r, g, b);
    }
}

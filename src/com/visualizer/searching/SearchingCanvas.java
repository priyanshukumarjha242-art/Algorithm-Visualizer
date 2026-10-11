package com.visualizer.searching;

import com.visualizer.ui.Theme;

import javax.swing.JPanel;
import java.awt.*;
import java.util.Arrays;

/**
 * Custom Graphics2D canvas for rendering searching elements,
 * index badges, Low/Mid/High pointers, and found status animations.
 */
public class SearchingCanvas extends JPanel implements SearchVisualizer {
    private int[] array;
    private SearchElementState[] states;
    private int lowPtr = -1;
    private int midPtr = -1;
    private int highPtr = -1;
    private int foundIndex = -1;
    private final Object renderLock = new Object();

    public SearchingCanvas() {
        setBackground(Theme.BG_DARK);
        setOpaque(true);
    }

    public void setArray(int[] newArray) {
        synchronized (renderLock) {
            this.array = Arrays.copyOf(newArray, newArray.length);
            this.states = new SearchElementState[newArray.length];
            Arrays.fill(this.states, SearchElementState.DEFAULT);
            this.lowPtr = -1;
            this.midPtr = -1;
            this.highPtr = -1;
            this.foundIndex = -1;
        }
        repaint();
    }

    public int[] getArrayCopy() {
        synchronized (renderLock) {
            return array != null ? Arrays.copyOf(array, array.length) : new int[0];
        }
    }

    @Override
    public void setState(int index, SearchElementState state) {
        synchronized (renderLock) {
            if (states != null && index >= 0 && index < states.length) {
                states[index] = state;
            }
        }
    }

    @Override
    public void setPointers(int low, int mid, int high) {
        synchronized (renderLock) {
            this.lowPtr = low;
            this.midPtr = mid;
            this.highPtr = high;
        }
    }

    @Override
    public void markFound(int index) {
        synchronized (renderLock) {
            this.foundIndex = index;
            if (states != null && index >= 0 && index < states.length) {
                states[index] = SearchElementState.FOUND;
            }
        }
    }

    @Override
    public void clearAllStates() {
        synchronized (renderLock) {
            if (states != null) {
                Arrays.fill(states, SearchElementState.DEFAULT);
            }
            this.lowPtr = -1;
            this.midPtr = -1;
            this.highPtr = -1;
            this.foundIndex = -1;
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

        g2.setColor(Theme.BG_DARK);
        g2.fillRect(0, 0, panelWidth, panelHeight);

        synchronized (renderLock) {
            if (array == null || array.length == 0) {
                g2.setColor(Theme.TEXT_MUTED);
                g2.setFont(Theme.FONT_TITLE);
                String msg = "Generate array & enter target to search";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(msg, (panelWidth - fm.stringWidth(msg)) / 2, panelHeight / 2);
                g2.dispose();
                return;
            }

            int n = array.length;
            int paddingX = 30;
            int availableWidth = panelWidth - (2 * paddingX);
            double itemWidth = (double) availableWidth / n;
            double cardWidth = Math.min(60, Math.max(16, itemWidth * 0.85));
            double cardHeight = Math.min(80, cardWidth * 1.4);
            double totalUsedWidth = n * itemWidth;
            double startX = paddingX + (availableWidth - totalUsedWidth) / 2.0;
            double centerY = panelHeight / 2.0;

            for (int i = 0; i < n; i++) {
                double x = startX + (i * itemWidth) + (itemWidth - cardWidth) / 2.0;
                double y = centerY - cardHeight / 2.0;

                SearchElementState state = (states != null && i < states.length) ? states[i] : SearchElementState.DEFAULT;
                Color cardBg;
                Color cardBorder;
                Color textColor = Theme.TEXT_PRIMARY;

                switch (state) {
                    case CURRENT_CHECK:
                        cardBg = Theme.COLOR_COMPARING;
                        cardBorder = Color.WHITE;
                        textColor = Color.BLACK;
                        break;
                    case FOUND:
                        cardBg = Theme.COLOR_SORTED;
                        cardBorder = Color.WHITE;
                        textColor = Color.WHITE;
                        break;
                    case EXCLUDED:
                        cardBg = new Color(0x1F, 0x22, 0x30, 100);
                        cardBorder = new Color(0x33, 0x38, 0x4B);
                        textColor = Theme.TEXT_MUTED;
                        break;
                    case DEFAULT:
                    default:
                        cardBg = Theme.SURFACE_CARD;
                        cardBorder = Theme.BORDER_COLOR;
                        break;
                }

                // Render card
                g2.setColor(cardBg);
                g2.fillRoundRect((int) x, (int) y, (int) cardWidth, (int) cardHeight, 10, 10);
                g2.setColor(cardBorder);
                g2.drawRoundRect((int) x, (int) y, (int) cardWidth, (int) cardHeight, 10, 10);

                // Value Text inside Card
                g2.setColor(textColor);
                g2.setFont(Theme.FONT_TITLE);
                FontMetrics fm = g2.getFontMetrics();
                String valStr = String.valueOf(array[i]);
                int strW = fm.stringWidth(valStr);
                int strH = fm.getAscent();
                g2.drawString(valStr, (int) (x + (cardWidth - strW) / 2.0), (int) (y + (cardHeight + strH) / 2.0 - 4));

                // Index label below Card
                g2.setColor(Theme.TEXT_MUTED);
                g2.setFont(Theme.FONT_MONO);
                FontMetrics fmMono = g2.getFontMetrics();
                String idxStr = "[" + i + "]";
                g2.drawString(idxStr, (int) (x + (cardWidth - fmMono.stringWidth(idxStr)) / 2.0), (int) (y + cardHeight + 20));

                // Pointer badges above card
                drawPointerBadge(g2, i, x, y, cardWidth);
            }
        }

        g2.dispose();
    }

    private void drawPointerBadge(Graphics2D g2, int i, double x, double y, double cardWidth) {
        StringBuilder badgeText = new StringBuilder();
        Color badgeColor = Theme.ACCENT_CYAN;

        if (i == foundIndex) {
            badgeText.append("MATCH!");
            badgeColor = Theme.COLOR_SORTED;
        } else {
            boolean hasPtr = false;
            if (i == lowPtr) {
                badgeText.append("LOW");
                hasPtr = true;
            }
            if (i == midPtr) {
                if (hasPtr) badgeText.append("/");
                badgeText.append("MID");
                badgeColor = Theme.COLOR_COMPARING;
                hasPtr = true;
            }
            if (i == highPtr) {
                if (hasPtr) badgeText.append("/");
                badgeText.append("HIGH");
                hasPtr = true;
            }
        }

        if (badgeText.length() > 0) {
            g2.setFont(Theme.FONT_BOLD);
            FontMetrics fm = g2.getFontMetrics();
            String label = badgeText.toString();
            int w = fm.stringWidth(label) + 10;
            int h = 18;
            int bx = (int) (x + (cardWidth - w) / 2.0);
            int by = (int) (y - 28);

            g2.setColor(badgeColor);
            g2.fillRoundRect(bx, by, w, h, 6, 6);
            g2.setColor(Color.BLACK);
            g2.drawString(label, bx + 5, by + 13);

            // Small triangle pointer
            int tx = (int) (x + cardWidth / 2.0);
            g2.setColor(badgeColor);
            g2.fillPolygon(new int[]{tx - 4, tx + 4, tx}, new int[]{by + h, by + h, by + h + 5}, 3);
        }
    }
}

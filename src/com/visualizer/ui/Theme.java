package com.visualizer.ui;

import java.awt.Color;
import java.awt.Font;

/**
 * High-contrast monochrome black & white UI theme with vibrant visualization accents.
 */
public final class Theme {
    private Theme() {}

    // Pure Black & Dark Surfaces
    public static final Color BG_DARK = new Color(0x00, 0x00, 0x00);         // Pure pitch black
    public static final Color SURFACE_CARD = new Color(0x12, 0x12, 0x12);     // Deep charcoal card
    public static final Color SURFACE_HEADER = new Color(0x0A, 0x0A, 0x0A);   // Toolbar / Header surface
    public static final Color SURFACE_HOVER = new Color(0x1E, 0x1E, 0x1E);    // Subtle hover surface
    public static final Color BORDER_COLOR = new Color(0x26, 0x26, 0x26);     // Refined 1px border
    public static final Color BORDER_LIGHT = new Color(0x40, 0x40, 0x40);     // Active / Hover border

    // Text & Monochromes
    public static final Color TEXT_PRIMARY = new Color(0xFF, 0xFF, 0xFF);     // Pure crisp white
    public static final Color TEXT_MUTED = new Color(0x9E, 0x9E, 0x9E);       // Neutral gray
    public static final Color TEXT_ACCENT = new Color(0x38, 0xBD, 0xF8);      // Electric cyan

    // Accent & Control Colors
    public static final Color ACCENT_PRIMARY = new Color(0xFF, 0xFF, 0xFF);   // Pure white for active tab
    public static final Color ACCENT_DARK = new Color(0x18, 0x18, 0x18);
    public static final Color ACCENT_CYAN = new Color(0x06, 0xB6, 0xD4);

    // Visualization Colors (High-contrast against black)
    public static final Color COLOR_BAR_DEFAULT = new Color(0x3B, 0x82, 0xF6); // Electric Blue
    public static final Color COLOR_COMPARING = new Color(0xFB, 0xBF, 0x24);   // Amber Gold
    public static final Color COLOR_SWAPPING = new Color(0xEF, 0x44, 0x44);    // Vivid Crimson
    public static final Color COLOR_SORTED = new Color(0x10, 0xB9, 0x81);      // Emerald Green
    public static final Color COLOR_PIVOT = new Color(0xA8, 0x55, 0xF7);       // Neon Purple

    // Typography
    public static final Font FONT_APP_TITLE = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_MONO = new Font("Consolas", Font.BOLD, 12);
    public static final Font FONT_STAT_VALUE = new Font("Consolas", Font.BOLD, 15);
}

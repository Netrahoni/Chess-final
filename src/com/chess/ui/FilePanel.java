package com.chess.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

public class FilePanel extends JPanel {
    private boolean isFlipped = false;

    public FilePanel() {
        setPreferredSize(new Dimension(640, 20));
        setOpaque(false);
    }

    public void flip() {
        isFlipped = !isFlipped;
        repaint();
    }

    public void reset() {
        isFlipped = false;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.setColor(new Color(200, 200, 200));
        for (int i = 0; i < 8; i++) {
            String file = isFlipped ? String.valueOf((char) ('h' - i)) : String.valueOf((char) ('a' + i));
            int x = i * 80 + (80 / 2) - 3;
            g.drawString(file, x, 15);
        }
    }
}

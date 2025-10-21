package com.chess.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

public class RankPanel extends JPanel {
    private boolean isFlipped = false;

    public RankPanel() {
        setPreferredSize(new Dimension(20, 640));
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
            String rank = isFlipped ? String.valueOf(i + 1) : String.valueOf(8 - i);
            int y = i * 80 + (80 / 2) + 5;
            g.drawString(rank, 5, y);
        }
    }
}

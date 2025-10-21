package com.chess.ui;

import com.chess.model.PieceColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PlayerProfilePanel extends JPanel {
    private final TimerPanel timerPanel;

    public PlayerProfilePanel(String playerName, PieceColor playerColor) {
        setLayout(new BorderLayout(10, 0));
        setBackground(new Color(38, 36, 33));
        setBorder(new EmptyBorder(5, 25, 5, 10));

        JLabel avatarLabel = new JLabel(playerColor == PieceColor.WHITE ? "⚪" : "⚫");
        avatarLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));

        JLabel nameLabel = new JLabel(playerName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        nameLabel.setForeground(Color.WHITE);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setOpaque(false);
        infoPanel.add(avatarLabel);
        infoPanel.add(nameLabel);

        timerPanel = new TimerPanel();

        add(infoPanel, BorderLayout.WEST);
        add(timerPanel, BorderLayout.EAST);
    }

    public TimerPanel getTimerPanel() {
        return timerPanel;
    }
}

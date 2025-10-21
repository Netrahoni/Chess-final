package com.chess.ui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class TimerPanel extends JPanel {
    private final JLabel timerLabel;

    public TimerPanel() {
        setBackground(new Color(38, 36, 33));
        setBorder(new EmptyBorder(5, 5, 5, 5));
        timerLabel = new JLabel("15:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        timerLabel.setForeground(Color.WHITE);
        add(timerLabel);
    }

    public void setTime(int totalSeconds) {
        if (totalSeconds < 0) {
            timerLabel.setText("∞");
            return;
        }
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    public void setActive(boolean active) {
        if (active) {
            setBackground(new Color(80, 78, 74));
            timerLabel.setForeground(new Color(255, 200, 0));
        } else {
            setBackground(new Color(38, 36, 33));
            timerLabel.setForeground(Color.WHITE);
        }
    }
}

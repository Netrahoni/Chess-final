package com.chess.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class HistoryPanel extends JPanel {
    private final JTextArea moveTextArea;

    public HistoryPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(38, 36, 33));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                "Move History",
                0, 0,
                new Font("Arial", Font.BOLD, 16),
                Color.WHITE
        ));
        setPreferredSize(new Dimension(200, 640));

        moveTextArea = new JTextArea("No moves yet.");
        moveTextArea.setEditable(false);
        moveTextArea.setBackground(new Color(60, 58, 54));
        moveTextArea.setForeground(Color.WHITE);
        moveTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        moveTextArea.setLineWrap(true);
        moveTextArea.setWrapStyleWord(true);
        moveTextArea.setBorder(new EmptyBorder(5, 5, 5, 5));

        JScrollPane scrollPane = new JScrollPane(moveTextArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
        add(scrollPane, BorderLayout.CENTER);
    }

    public void addMove(String pgn) {
        if (moveTextArea.getText().equals("No moves yet.")) {
            moveTextArea.setText("");
        }
        moveTextArea.append(pgn + " ");
    }

    public void removeLastMove(String pgnToRemove) {
        String currentText = moveTextArea.getText();
        int index = currentText.lastIndexOf(pgnToRemove);
        if (index != -1) {
            moveTextArea.setText(currentText.substring(0, index));
        }
        if (moveTextArea.getText().trim().isEmpty()) {
            moveTextArea.setText("No moves yet.");
        }
    }


    public void reset() {
        moveTextArea.setText("No moves yet.");
    }
}

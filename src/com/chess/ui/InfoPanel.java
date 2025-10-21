package com.chess.ui;

import com.chess.model.Piece;
import com.chess.model.PieceColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class InfoPanel extends JPanel {
    private final JLabel turnLabel;
    private final JLabel modeLabel;
    private final JPanel capturedByWhitePanel;
    private final JPanel capturedByBlackPanel;
    private final JButton pauseButton, resignButton, drawButton, undoButton;
    private final GameContainerPanel gameContainerPanel;

    public InfoPanel(GameContainerPanel gameContainerPanel) {
        this.gameContainerPanel = gameContainerPanel;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(38, 36, 33));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(220, 700));

        add(createSectionTitle("Game Status"));
        JPanel statusDetailsPanel = createStyledPanel();
        turnLabel = new JLabel("Turn: White");
        modeLabel = new JLabel("Mode: -");
        styleInfoLabel(turnLabel);
        styleInfoLabel(modeLabel);
        statusDetailsPanel.add(turnLabel);
        statusDetailsPanel.add(modeLabel);
        add(statusDetailsPanel);
        add(Box.createRigidArea(new Dimension(0, 20)));

        add(createSectionTitle("Display"));
        JPanel displayPanel = createStyledPanel();
        displayPanel.setLayout(new GridLayout(0, 1, 0, 5));
        JComboBox<String> themeDropdown = new JComboBox<>(new String[]{"Green (Default)", "Brown", "Blue"});
        themeDropdown.addActionListener(e -> {
            String selectedTheme = (String) themeDropdown.getSelectedItem();
            gameContainerPanel.getChessboardPanel().setTheme(selectedTheme);
        });
        displayPanel.add(new JLabel("Board Theme:"));
        displayPanel.add(themeDropdown);
        displayPanel.add(new StyledButton("Flip Board", e -> gameContainerPanel.flipBoardLayout()));
        displayPanel.add(new StyledButton("Full Screen", e -> gameContainerPanel.chessGame.toggleFullScreen()));
        add(displayPanel);
        add(Box.createRigidArea(new Dimension(0, 20)));

        add(createSectionTitle("Game Actions"));
        JPanel actionsPanel = createStyledPanel();
        actionsPanel.setLayout(new GridLayout(0, 1, 0, 5));
        pauseButton = new StyledButton("Pause Game", e -> gameContainerPanel.handlePauseResume());
        resignButton = new StyledButton("Resign", e -> gameContainerPanel.handleResignation());
        drawButton = new StyledButton("Offer Draw", e -> gameContainerPanel.handleDrawOffer());
        undoButton = new StyledButton("Undo Move", e -> gameContainerPanel.handleUndoRequest());
        actionsPanel.add(pauseButton);
        actionsPanel.add(resignButton);
        actionsPanel.add(drawButton);
        actionsPanel.add(undoButton);
        add(actionsPanel);
        add(Box.createRigidArea(new Dimension(0, 20)));


        add(createSectionTitle("Captured by White"));
        capturedByWhitePanel = createStyledPanel();
        add(capturedByWhitePanel);
        add(Box.createRigidArea(new Dimension(0, 10)));

        add(createSectionTitle("Captured by Black"));
        capturedByBlackPanel = createStyledPanel();
        add(capturedByBlackPanel);

        add(Box.createVerticalGlue());
    }

    public void togglePauseButtonText(boolean isPaused) {
        pauseButton.setText(isPaused ? "Resume Game" : "Pause Game");
    }

    public void updateActionButtonsState(boolean enabled) {
        resignButton.setEnabled(enabled);
        drawButton.setEnabled(enabled);
        undoButton.setEnabled(enabled && !gameContainerPanel.gameLogic.moveHistory.isEmpty());
    }

    public void updateGameInfo(String mode, String level) {
        modeLabel.setText("Mode: " + mode + (mode.equals("AI") ? " (" + level + ")" : ""));
        turnLabel.setText("Turn: White");
    }

    public void updateTurn(String color) {
        turnLabel.setText("Turn: " + color);
    }

    public void addCapturedPiece(Piece piece, PieceColor capturingPlayer) {
        JLabel pieceLabel = new JLabel(piece.getUnicodeChar());
        pieceLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
        pieceLabel.setForeground(piece.color() == PieceColor.WHITE ? Color.WHITE : Color.GRAY);
        if (capturingPlayer == PieceColor.WHITE) {
            capturedByWhitePanel.add(pieceLabel);
        } else {
            capturedByBlackPanel.add(pieceLabel);
        }
        revalidate();
        repaint();
    }

    public void removeLastCapturedPiece(PieceColor capturingPlayer) {
        JPanel targetPanel = (capturingPlayer == PieceColor.WHITE) ? capturedByWhitePanel : capturedByBlackPanel;
        int count = targetPanel.getComponentCount();
        if (count > 0) {
            targetPanel.remove(count - 1);
            targetPanel.revalidate();
            targetPanel.repaint();
        }
    }


    public void reset() {
        turnLabel.setText("Turn: -");
        modeLabel.setText("Mode: -");
        capturedByWhitePanel.removeAll();
        capturedByBlackPanel.removeAll();
        capturedByWhitePanel.revalidate();
        capturedByWhitePanel.repaint();
        capturedByBlackPanel.revalidate();
        capturedByBlackPanel.repaint();
        pauseButton.setText("Pause Game");
        updateActionButtonsState(false);
    }

    private void styleInfoLabel(JLabel label) {
        label.setForeground(Color.LIGHT_GRAY);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
    }

    private JLabel createSectionTitle(String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(Color.WHITE);
        label.setBorder(new EmptyBorder(0, 5, 5, 0));
        return label;
    }

    private JPanel createStyledPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(60, 58, 54));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    private class StyledButton extends JButton {
        private final Color normalColor = new Color(80, 78, 74);
        private final Color hoverColor = new Color(100, 98, 94);

        public StyledButton(String text, ActionListener listener) {
            super(text);
            addActionListener(listener);
            setFont(new Font("Arial", Font.BOLD, 14));
            setBackground(normalColor);
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorder(new EmptyBorder(8, 15, 8, 15));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (isEnabled()) setBackground(hoverColor);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setBackground(normalColor);
                }
            });
        }
    }
}

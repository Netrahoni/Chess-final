package com.chess;

import com.chess.logic.GameLogic;
import com.chess.ui.GameContainerPanel;
import com.chess.ui.GameSetupPanel;
import java.awt.*;
import javax.swing.*;

public class ChessGame extends JFrame {

    private GameLogic gameLogic;
    private final GameSetupPanel setupPanel;
    private final JPanel mainPanel;
    private final CardLayout cardLayout;
    private final GameContainerPanel gameContainerPanel;
    private boolean isFullScreen = false;
    private final GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();


    public ChessGame() {
        setTitle("Java Chess");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setBackground(new Color(49, 46, 43));

        JPanel contentPane = new JPanel(new GridBagLayout());
        contentPane.setBackground(new Color(30, 30, 30));
        setContentPane(contentPane);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setOpaque(false);

        setupPanel = new GameSetupPanel(this);
        gameContainerPanel = new GameContainerPanel(this);

        mainPanel.add(setupPanel, "SETUP");
        mainPanel.add(gameContainerPanel, "GAME");

        contentPane.add(mainPanel, new GridBagConstraints());

        cardLayout.show(mainPanel, "SETUP");

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void startGame(String timeControl, String gameType, String gameMode, String aiLevel) {
        gameLogic = new GameLogic(this);
        gameContainerPanel.initializeGame(gameLogic, timeControl, gameType, gameMode, aiLevel);

        cardLayout.show(mainPanel, "GAME");
        pack();
        setLocationRelativeTo(null);
    }

    public void resetGame() {
        gameLogic = null;
        gameContainerPanel.reset();
        cardLayout.show(mainPanel, "SETUP");
        pack();
        setLocationRelativeTo(null);
    }

    public void handleGameOver(String message) {
        gameContainerPanel.stopTimers();
        Object[] options = {"New Game", "Exit"};
        int choice = JOptionPane.showOptionDialog(
                this,
                message,
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == JOptionPane.YES_OPTION) {
            resetGame();
        } else {
            System.exit(0);
        }
    }

    public void toggleFullScreen() {
        dispose();
        if (isFullScreen) {
            setUndecorated(false);
            device.setFullScreenWindow(null);
            pack();
            setLocationRelativeTo(null);
        } else {
            setUndecorated(true);
            device.setFullScreenWindow(this);
        }
        setVisible(true);
        isFullScreen = !isFullScreen;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChessGame::new);
    }
    public GameContainerPanel getGameContainerPanel() {
        return gameContainerPanel;
    }
}
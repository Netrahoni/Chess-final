package com.chess.ui;

import com.chess.ChessGame;
import com.chess.logic.GameLogic;
import com.chess.model.PieceColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GameContainerPanel extends JPanel {
    private final ChessboardPanel chessboardPanel;
    private final InfoPanel infoPanel;
    private final HistoryPanel historyPanel;
    private final PlayerProfilePanel whitePlayerProfile, blackPlayerProfile;
    private final JPanel boardContainer, boardWrapper;
    private final RankPanel rankPanel;
    private final FilePanel filePanel;
    private final JLabel statusLabel;
    private Timer whiteTimer, blackTimer;
    private int whiteTimeSeconds, blackTimeSeconds;
    private int alertThreshold;
    private boolean whiteLowTimeAlerted, blackLowTimeAlerted;
    private GameLogic gameLogic;
    private final ChessGame chessGame;


    public GameContainerPanel(ChessGame chessGame) {
        this.chessGame = chessGame;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(49, 46, 43));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        infoPanel = new InfoPanel(this);
        historyPanel = new HistoryPanel();

        whitePlayerProfile = new PlayerProfilePanel("White", PieceColor.WHITE);
        blackPlayerProfile = new PlayerProfilePanel("Black", PieceColor.BLACK);

        boardWrapper = new JPanel(new BorderLayout());
        boardWrapper.setOpaque(false);
        chessboardPanel = new ChessboardPanel();
        rankPanel = new RankPanel();
        filePanel = new FilePanel();
        boardWrapper.add(chessboardPanel, BorderLayout.CENTER);
        boardWrapper.add(rankPanel, BorderLayout.WEST);
        boardWrapper.add(filePanel, BorderLayout.SOUTH);

        boardContainer = new JPanel(new BorderLayout());
        boardContainer.setOpaque(false);
        boardContainer.add(blackPlayerProfile, BorderLayout.NORTH);
        boardContainer.add(boardWrapper, BorderLayout.CENTER);
        boardContainer.add(whitePlayerProfile, BorderLayout.SOUTH);

        statusLabel = new JLabel("Welcome to Chess Game", SwingConstants.CENTER);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setOpaque(false);
        statusPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        statusPanel.add(statusLabel, BorderLayout.CENTER);

        add(infoPanel, BorderLayout.WEST);
        add(boardContainer, BorderLayout.CENTER);
        add(historyPanel, BorderLayout.EAST);
        add(statusPanel, BorderLayout.SOUTH);
    }

    public void flipBoardLayout() {
        Component north = ((BorderLayout) boardContainer.getLayout()).getLayoutComponent(BorderLayout.NORTH);
        Component south = ((BorderLayout) boardContainer.getLayout()).getLayoutComponent(BorderLayout.SOUTH);
        boardContainer.remove(north);
        boardContainer.remove(south);
        boardContainer.add(south, BorderLayout.NORTH);
        boardContainer.add(north, BorderLayout.SOUTH);

        chessboardPanel.flipBoard();
        rankPanel.flip();
        filePanel.flip();

        boardContainer.revalidate();
        boardContainer.repaint();
    }

    public void initializeGame(GameLogic gameLogic, String timeControl, String gameType, String gameMode, String aiLevel) {
        this.gameLogic = gameLogic;
        chessboardPanel.setGameLogic(gameLogic);
        infoPanel.updateGameInfo(gameMode, aiLevel);
        updateStatus("White's Turn");
        whiteLowTimeAlerted = false;
        blackLowTimeAlerted = false;
        infoPanel.updateActionButtonsState(true);


        switch (gameType) {
            case "Bullet" -> alertThreshold = 10;
            case "Blitz" -> alertThreshold = 30;
            case "Rapid" -> alertThreshold = 60;
            default -> alertThreshold = -1;
        }


        if ("Unlimited".equals(timeControl)) {
            whiteTimeSeconds = -1;
            blackTimeSeconds = -1;
        } else {
            int minutes = Integer.parseInt(timeControl.split(" ")[0]);
            whiteTimeSeconds = blackTimeSeconds = minutes * 60;
            setupTimers();
        }
        updateTimerLabel(whitePlayerProfile.getTimerPanel(), whiteTimeSeconds);
        updateTimerLabel(blackPlayerProfile.getTimerPanel(), blackTimeSeconds);
        startWhiteTimer();
    }

    public void reset() {
        stopTimers();
        this.gameLogic = null;
        whiteTimeSeconds = 0;
        blackTimeSeconds = 0;
        updateTimerLabel(whitePlayerProfile.getTimerPanel(), 0);
        updateTimerLabel(blackPlayerProfile.getTimerPanel(), 0);
        infoPanel.reset();
        historyPanel.reset();
        chessboardPanel.setGameLogic(null);
        chessboardPanel.resetBoardView();
        rankPanel.reset();
        filePanel.reset();

        boardContainer.remove(whitePlayerProfile);
        boardContainer.remove(blackPlayerProfile);
        boardContainer.add(blackPlayerProfile, BorderLayout.NORTH);
        boardContainer.add(whitePlayerProfile, BorderLayout.SOUTH);
    }

    public void updateStatus(String text) {
        statusLabel.setText(text);
    }

    private void setupTimers() {
        whiteTimer = new Timer(1000, e -> {
            whiteTimeSeconds--;
            updateTimerLabel(whitePlayerProfile.getTimerPanel(), whiteTimeSeconds);
            if (!whiteLowTimeAlerted && alertThreshold > 0 && whiteTimeSeconds == alertThreshold) {
                showLowTimeAlert("White");
                whiteLowTimeAlerted = true;
            }
            if (whiteTimeSeconds <= 0) timeUp("White");
        });
        blackTimer = new Timer(1000, e -> {
            blackTimeSeconds--;
            updateTimerLabel(blackPlayerProfile.getTimerPanel(), blackTimeSeconds);
            if (!blackLowTimeAlerted && alertThreshold > 0 && blackTimeSeconds == alertThreshold) {
                showLowTimeAlert("Black");
                blackLowTimeAlerted = true;
            }
            if (blackTimeSeconds <= 0) timeUp("Black");
        });
    }

    private void showLowTimeAlert(String player) {
        JOptionPane.showMessageDialog(this, player + "'s time is running low!", "Time Alert", JOptionPane.WARNING_MESSAGE);
    }

    public void handlePauseResume() {
        if (gameLogic == null) return;
        gameLogic.isPaused = !gameLogic.isPaused;
        infoPanel.togglePauseButtonText(gameLogic.isPaused);
        infoPanel.updateActionButtonsState(!gameLogic.isPaused);


        if (gameLogic.isPaused) {
            stopTimers();
            updateStatus("Game Paused");
        } else {
            if (gameLogic.currentPlayer == PieceColor.WHITE) {
                startWhiteTimer();
            } else {
                startBlackTimer();
            }
            updateStatus(gameLogic.currentPlayer + "'s Turn");
        }
    }

    public void handleResignation() {
        if (gameLogic == null || gameLogic.isGameOver) return;
        String resigningPlayer = gameLogic.currentPlayer == PieceColor.WHITE ? "White" : "Black";
        int choice = JOptionPane.showConfirmDialog(this,
                resigningPlayer + ", are you sure you want to resign?",
                "Confirm Resignation",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            String winner = resigningPlayer.equals("White") ? "Black" : "White";
            chessGame.handleGameOver(winner + " wins by resignation.");
        }
    }

    public void handleDrawOffer() {
        if (gameLogic == null || gameLogic.isGameOver) return;
        String offeringPlayer = gameLogic.currentPlayer == PieceColor.WHITE ? "White" : "Black";
        String opponent = offeringPlayer.equals("White") ? "Black" : "White";

        int choice = JOptionPane.showConfirmDialog(this,
                opponent + ", " + offeringPlayer + " offers a draw. Do you accept?",
                "Draw Offer",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            chessGame.handleGameOver("Draw by Agreement.");
        } else {
            updateStatus("Draw offer declined. " + opponent + "'s turn.");
        }
    }

    public void handleUndoRequest() {
        if (gameLogic == null || gameLogic.isGameOver || gameLogic.moveHistory.isEmpty()) return;
        String requestingPlayer = gameLogic.currentPlayer == PieceColor.WHITE ? "White" : "Black";
        String opponent = requestingPlayer.equals("White") ? "Black" : "White";

        int choice = JOptionPane.showConfirmDialog(this,
                opponent + ", " + requestingPlayer + " wants to undo the last move. Do you agree?",
                "Undo Request",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            gameLogic.undoLastMove();
        } else {
            updateStatus("Undo request declined. " + requestingPlayer + "'s turn.");
        }
    }


    public void startWhiteTimer() {
        if (whiteTimer != null) whiteTimer.start();
        whitePlayerProfile.getTimerPanel().setActive(true);
        blackPlayerProfile.getTimerPanel().setActive(false);
    }

    public void stopWhiteTimer() {
        if (whiteTimer != null) whiteTimer.stop();
    }

    public void startBlackTimer() {
        if (blackTimer != null) blackTimer.start();
        blackPlayerProfile.getTimerPanel().setActive(true);
        whitePlayerProfile.getTimerPanel().setActive(false);
    }

    public void stopBlackTimer() {
        if (blackTimer != null) blackTimer.stop();
    }

    public void stopTimers() {
        stopWhiteTimer();
        stopBlackTimer();
    }

    private void timeUp(String loser) {
        stopTimers();
        if (gameLogic != null) gameLogic.isGameOver = true;
        String winner = loser.equals("White") ? "Black" : "White";
        chessGame.handleGameOver(winner + " wins on time!");
    }

    private void updateTimerLabel(TimerPanel panel, int totalSeconds) {
        panel.setTime(totalSeconds);
    }

    public InfoPanel getInfoPanel() {
        return infoPanel;
    }

    public HistoryPanel getHistoryPanel() {
        return historyPanel;
    }

    public ChessboardPanel getChessboardPanel() {
        return chessboardPanel;
    }
}

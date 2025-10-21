package com.chess.ui;

import com.chess.logic.GameLogic;
import com.chess.model.Move;
import com.chess.model.Piece;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ChessboardPanel extends JPanel {
    private static final int TILE_SIZE = 80;
    private static final int BOARD_SIZE = 8 * TILE_SIZE;
    private GameLogic gameLogic;
    private final Font pieceFont = new Font("SansSerif", Font.PLAIN, 60);
    private boolean isFlipped = false;
    private Color lightTileColor = new Color(238, 238, 210);
    private Color darkTileColor = new Color(118, 150, 86);


    public ChessboardPanel() {
        this.gameLogic = null;
        setPreferredSize(new Dimension(BOARD_SIZE, BOARD_SIZE));
        addMouseListener(new ChessMouseListener());
    }

    public void setGameLogic(GameLogic newLogic) {
        this.gameLogic = newLogic;
        repaint();
    }

    public void flipBoard() {
        isFlipped = !isFlipped;
        repaint();
    }

    public void resetBoardView() {
        isFlipped = false;
        setTheme("Green (Default)");
    }

    public void setTheme(String themeName) {
        switch (themeName) {
            case "Brown" -> {
                lightTileColor = new Color(240, 217, 181);
                darkTileColor = new Color(181, 136, 99);
            }
            case "Blue" -> {
                lightTileColor = new Color(235, 236, 208);
                darkTileColor = new Color(119, 149, 181);
            }
            default -> {
                lightTileColor = new Color(238, 238, 210);
                darkTileColor = new Color(118, 150, 86);
            }
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int displayRow = isFlipped ? 7 - row : row;
                int displayCol = isFlipped ? 7 - col : col;

                Color tileColor = (row + col) % 2 == 0 ? lightTileColor : darkTileColor;
                g2d.setColor(tileColor);
                g2d.fillRect(displayCol * TILE_SIZE, displayRow * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        if (gameLogic == null) return;

        if (gameLogic.selectedPiece != null) {
            int displayRow = isFlipped ? 7 - gameLogic.selectedRow : gameLogic.selectedRow;
            int displayCol = isFlipped ? 7 - gameLogic.selectedCol : gameLogic.selectedCol;
            g2d.setColor(new Color(80, 150, 255, 150));
            g2d.fillRect(displayCol * TILE_SIZE, displayRow * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        if (gameLogic.validMoves != null) {
            for (Move move : gameLogic.validMoves) {
                int displayRow = isFlipped ? 7 - move.endRow() : move.endRow();
                int displayCol = isFlipped ? 7 - move.endCol() : move.endCol();
                g2d.setColor(new Color(0, 0, 0, 40));
                g2d.fillOval(displayCol * TILE_SIZE + TILE_SIZE / 3, displayRow * TILE_SIZE + TILE_SIZE / 3, TILE_SIZE / 3, TILE_SIZE / 3);
            }
        }

        g2d.setFont(pieceFont);
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = gameLogic.board[row][col];
                if (piece != null) {
                    int displayRow = isFlipped ? 7 - row : row;
                    int displayCol = isFlipped ? 7 - col : col;

                    g2d.setColor(piece.color() == com.chess.model.PieceColor.WHITE ? Color.WHITE : Color.BLACK);
                    String pieceChar = piece.getUnicodeChar();
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = displayCol * TILE_SIZE + (TILE_SIZE - fm.stringWidth(pieceChar)) / 2;
                    int y = displayRow * TILE_SIZE + (TILE_SIZE - fm.getHeight()) / 2 + fm.getAscent();
                    g2d.drawString(pieceChar, x, y);
                }
            }
        }
    }

    private class ChessMouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            if (gameLogic == null || gameLogic.isGameOver || gameLogic.isPaused) {
                return;
            }
            int col = e.getX() / TILE_SIZE;
            int row = e.getY() / TILE_SIZE;

            int actualRow = isFlipped ? 7 - row : row;
            int actualCol = isFlipped ? 7 - col : col;

            gameLogic.handleSquareSelection(actualRow, actualCol);
            repaint();
        }
    }
}

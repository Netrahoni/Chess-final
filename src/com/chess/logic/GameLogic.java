package com.chess.logic;

import com.chess.ChessGame;
import com.chess.model.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class GameLogic {
    public final Piece[][] board;
    public PieceColor currentPlayer;
    public Piece selectedPiece;
    public int selectedRow, selectedCol;
    public List<Move> validMoves;
    public boolean isGameOver;
    public boolean isPaused = false;
    private final ChessGame chessGame;
    private int moveCount = 1;
    public final Stack<MoveRecord> moveHistory = new Stack<>();


    public GameLogic(ChessGame chessGame) {
        this.chessGame = chessGame;
        board = new Piece[8][8];
        setupInitialBoard();
        currentPlayer = PieceColor.WHITE;
        isGameOver = false;
    }

    public MoveRecord undoLastMove() {
        if (moveHistory.isEmpty()) return null;

        MoveRecord lastRecord = moveHistory.pop();
        Move lastMove = lastRecord.move();
        Piece capturedPiece = lastRecord.capturedPiece();

        Piece movedPiece = board[lastMove.endRow()][lastMove.endCol()];
        board[lastMove.startRow()][lastMove.startCol()] = movedPiece;
        board[lastMove.endRow()][lastMove.endCol()] = capturedPiece;

        switchPlayerBack();
        
        return lastRecord;
    }


    private void setupInitialBoard() {
        // ... (Original content unchanged) ...
        board[0][0] = new Piece(PieceType.ROOK, PieceColor.BLACK);
        board[0][1] = new Piece(PieceType.KNIGHT, PieceColor.BLACK);
        board[0][2] = new Piece(PieceType.BISHOP, PieceColor.BLACK);
        board[0][3] = new Piece(PieceType.QUEEN, PieceColor.BLACK);
        board[0][4] = new Piece(PieceType.KING, PieceColor.BLACK);
        board[0][5] = new Piece(PieceType.BISHOP, PieceColor.BLACK);
        board[0][6] = new Piece(PieceType.KNIGHT, PieceColor.BLACK);
        board[0][7] = new Piece(PieceType.ROOK, PieceColor.BLACK);
        for (int i = 0; i < 8; i++) {
            board[1][i] = new Piece(PieceType.PAWN, PieceColor.BLACK);
        }

        board[7][0] = new Piece(PieceType.ROOK, PieceColor.WHITE);
        board[7][1] = new Piece(PieceType.KNIGHT, PieceColor.WHITE);
        board[7][2] = new Piece(PieceType.BISHOP, PieceColor.WHITE);
        board[7][3] = new Piece(PieceType.QUEEN, PieceColor.WHITE);
        board[7][4] = new Piece(PieceType.KING, PieceColor.WHITE);
        board[7][5] = new Piece(PieceType.BISHOP, PieceColor.WHITE);
        board[7][6] = new Piece(PieceType.KNIGHT, PieceColor.WHITE);
        board[7][7] = new Piece(PieceType.ROOK, PieceColor.WHITE);
        for (int i = 0; i < 8; i++) {
            board[6][i] = new Piece(PieceType.PAWN, PieceColor.WHITE);
        }
    }

    public void handleSquareSelection(int row, int col) {
        if (selectedPiece == null) {
            Piece piece = board[row][col];
            if (piece != null && piece.color() == currentPlayer) {
                selectedPiece = piece;
                selectedRow = row;
                selectedCol = col;
                validMoves = calculateValidMoves(row, col);
            }
        } else {
            Move intendedMove = new Move(selectedRow, selectedCol, row, col);
            boolean isValidMove = validMoves.stream().anyMatch(m -> m.equals(intendedMove));

            if (isValidMove) {
                MoveRecord record = movePiece(intendedMove); // Get the record
                checkForPawnPromotion(row, col);
                switchPlayer(record); // Pass the record
            }

            selectedPiece = null;
            validMoves = null;
        }
    }

    public MoveRecord movePiece(Move move) {
        Piece movingPiece = board[move.startRow()][move.startCol()];
        Piece capturedPiece = board[move.endRow()][move.endCol()];

        String pgn = convertMoveToPgn(move, movingPiece, capturedPiece != null);
        MoveRecord record = new MoveRecord(move, capturedPiece, pgn);
        moveHistory.push(record);

        board[move.endRow()][move.endCol()] = movingPiece;
        board[move.startRow()][move.startCol()] = null;

        return record;
    }

    public String convertMoveToPgn(Move move, Piece movedPiece, boolean isCapture) {
        // ... (Original content unchanged) ...
        StringBuilder sb = new StringBuilder();
        if (movedPiece.color() == PieceColor.WHITE) {
            sb.append(moveCount).append(". ");
        }

        sb.append(getPieceChar(movedPiece.type()));
        sb.append(getSquareName(move.startRow(), move.startCol()));
        sb.append(isCapture ? "x" : "-");
        sb.append(getSquareName(move.endRow(), move.endCol()));

        return sb.toString();
    }

    private String getPieceChar(PieceType type) {
        // ... (Original content unchanged) ...
        return switch (type) {
            case KNIGHT -> "N";
            case BISHOP -> "B";
            case ROOK -> "R";
            case QUEEN -> "Q";
            case KING -> "K";
            default -> "";
        };
    }

    private String getSquareName(int row, int col) {
        // ... (Original content unchanged) ...
        char file = (char) ('a' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }

    public void switchPlayerBack() {
         if (currentPlayer == PieceColor.BLACK) {
            moveCount--;
        }

        currentPlayer = (currentPlayer == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
    }

    public void switchPlayer() {
        switchPlayer(null);
    }

    public void switchPlayer(MoveRecord record) {
        if (currentPlayer == PieceColor.BLACK) {
            moveCount++;
        }

        currentPlayer = (currentPlayer == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;

        // === ADDED: UI Update Block ===
        final PieceColor newPlayer = currentPlayer;
        SwingUtilities.invokeLater(() -> {
            // Update history and captured pieces
            if (record != null) {
                chessGame.getGameContainerPanel().getHistoryPanel().addMove(record.pgn());
                if (record.capturedPiece() != null) {
                    // Find the piece that just moved to get its color
                    Piece movingPiece = board[record.move().endRow()][record.move().endCol()];
                    if (movingPiece != null) {
                        chessGame.getGameContainerPanel().getInfoPanel().addCapturedPiece(
                                record.capturedPiece(), movingPiece.color());
                    }
                }
            }

            // Update status label and timers
            chessGame.getGameContainerPanel().updateStatus(newPlayer + "'s Turn");
            if (newPlayer == PieceColor.WHITE) {
                chessGame.getGameContainerPanel().stopBlackTimer();
                chessGame.getGameContainerPanel().startWhiteTimer();
            } else {
                chessGame.getGameContainerPanel().stopWhiteTimer();
                chessGame.getGameContainerPanel().startBlackTimer();
            }
        });
        // === End of Added Block ===

        if (isCheckmate(currentPlayer)) {
            isGameOver = true;
            String winner = (currentPlayer == PieceColor.WHITE ? "Black" : "White");
            SwingUtilities.invokeLater(() -> chessGame.handleGameOver("Checkmate! " + winner + " wins."));
        } else if (isStalemate(currentPlayer)) {
            isGameOver = true;
            SwingUtilities.invokeLater(() -> chessGame.handleGameOver("Stalemate! The game is a draw."));
        }
    }

    private void checkForPawnPromotion(int row, int col) {
        // ... (Original content unchanged) ...
        Piece piece = board[row][col];
        if (piece != null && piece.type() == PieceType.PAWN) {
            if ((piece.color() == PieceColor.WHITE && row == 0) || (piece.color() == PieceColor.BLACK && row == 7)) {
                promotePawn(row, col);
            }
        }
    }

    private void promotePawn(int row, int col) {
        // ... (Original content unchanged) ...
        PieceColor color = board[row][col].color();
        Object[] options = {"Queen", "Rook", "Bishop", "Knight"};
        String choice = (String) JOptionPane.showInputDialog(
                chessGame, "Choose a piece to promote your pawn to:",
                "Pawn Promotion", JOptionPane.PLAIN_MESSAGE, null, options, "Queen");

        PieceType newType = PieceType.QUEEN;
        if (choice != null) {
            newType = switch (choice) {
                case "Rook" -> PieceType.ROOK;
                case "Bishop" -> PieceType.BISHOP;
                case "Knight" -> PieceType.KNIGHT;
                default -> PieceType.QUEEN;
            };
        }
        board[row][col] = new Piece(newType, color);
    }

    public List<Move> calculateValidMoves(int row, int col) {
        // ... (Original content unchanged) ...
        List<Move> moves = new ArrayList<>();
        Piece piece = board[row][col];
        if (piece == null) return moves;

        switch (piece.type()) {
            case PAWN -> addPawnMoves(moves, piece, row, col);
            case ROOK -> addSlidingMoves(moves, piece, row, col, new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}});
            case KNIGHT -> addKnightMoves(moves, piece, row, col);
            case BISHOP -> addSlidingMoves(moves, piece, row, col, new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}});
            case QUEEN -> addSlidingMoves(moves, piece, row, col, new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}});
            case KING -> addKingMoves(moves, piece, row, col);
        }

        return moves.stream()
                .filter(move -> !moveLeavesKingInCheck(move, piece.color()))
                .collect(Collectors.toList());
    }

    private void addPawnMoves(List<Move> moves, Piece piece, int r, int c) {
        // ... (Original content unchanged) ...
        int direction = (piece.color() == PieceColor.WHITE) ? -1 : 1;
        int startRow = (piece.color() == PieceColor.WHITE) ? 6 : 1;

        if (isValid(r + direction, c) && board[r + direction][c] == null) {
            moves.add(new Move(r, c, r + direction, c));
            if (r == startRow && isValid(r + 2 * direction, c) && board[r + 2 * direction][c] == null) {
                moves.add(new Move(r, c, r + 2 * direction, c));
            }
        }
        for (int dc : new int[]{-1, 1}) {
            if (isValid(r + direction, c + dc) && board[r + direction][c + dc] != null &&
                    board[r + direction][c + dc].color() != piece.color()) {
                moves.add(new Move(r, c, r + direction, c + dc));
            }
        }
    }

    private void addSlidingMoves(List<Move> moves, Piece piece, int r, int c, int[][] directions) {
        // ... (Original content unchanged) ...
        for (int[] dir : directions) {
            for (int i = 1; i < 8; i++) {
                int newRow = r + i * dir[0];
                int newCol = c + i * dir[1];
                if (!isValid(newRow, newCol)) break;
                if (board[newRow][newCol] == null) {
                    moves.add(new Move(r, c, newRow, newCol));
                } else {
                    if (board[newRow][newCol].color() != piece.color()) {
                        moves.add(new Move(r, c, newRow, newCol));
                    }
                    break;
                }
            }
        }
    }

    private void addKnightMoves(List<Move> moves, Piece piece, int r, int c) {
        // ... (Original content unchanged) ...
        int[][] knightMoves = {{1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}};
        for (int[] move : knightMoves) {
            int newRow = r + move[0];
            int newCol = c + move[1];
            if (isValid(newRow, newCol) && (board[newRow][newCol] == null || board[newRow][newCol].color() != piece.color())) {
                moves.add(new Move(r, c, newRow, newCol));
            }
        }
    }

    private void addKingMoves(List<Move> moves, Piece piece, int r, int c) {
        // ... (Original content unchanged) ...
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int newRow = r + dr;
                int newCol = c + dc;
                if (isValid(newRow, newCol) && (board[newRow][newCol] == null || board[newRow][newCol].color() != piece.color())) {
                    moves.add(new Move(r, c, newRow, newCol));
                }
            }
        }
    }

    private boolean isValid(int r, int c) {
        // ... (Original content unchanged) ...
        return r >= 0 && r < 8 && c >= 0 && c < 8;
    }

    public boolean isKingInCheck(PieceColor kingColor) {
        // ... (Original content unchanged) ...
        int kingRow = -1, kingCol = -1;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (board[r][c] != null && board[r][c].type() == PieceType.KING && board[r][c].color() == kingColor) {
                    kingRow = r;
                    kingCol = c;
                    break;
                }
            }
        }
        if (kingRow == -1) return false;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = board[r][c];
                if (piece != null && piece.color() != kingColor) {
                    List<Move> opponentMoves = calculateRawMoves(r, c);
                    for (Move move : opponentMoves) {
                        if (move.endRow() == kingRow && move.endCol() == kingCol) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean moveLeavesKingInCheck(Move move, PieceColor playerColor) {
        // ... (Original content unchanged) ...
        Piece temp = board[move.endRow()][move.endCol()];
        board[move.endRow()][move.endCol()] = board[move.startRow()][move.startCol()];
        board[move.startRow()][move.startCol()] = null;

        boolean leavesCheck = isKingInCheck(playerColor);

        board[move.startRow()][move.startCol()] = board[move.endRow()][move.endCol()];
        board[move.endRow()][move.endCol()] = temp;

        return leavesCheck;
    }

    private List<Move> calculateRawMoves(int row, int col) {
        // ... (Original content unchanged) ...
        List<Move> moves = new ArrayList<>();
        Piece piece = board[row][col];
        if (piece == null) return moves;

        switch (piece.type()) {
            case PAWN -> addPawnMoves(moves, piece, row, col);
            case ROOK -> addSlidingMoves(moves, piece, row, col, new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}});
            case KNIGHT -> addKnightMoves(moves, piece, row, col);
            case BISHOP -> addSlidingMoves(moves, piece, row, col, new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}});
            case QUEEN -> addSlidingMoves(moves, piece, row, col, new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}});
            case KING -> addKingMoves(moves, piece, row, col);
        }
        return moves;
    }

    public boolean hasAnyLegalMoves(PieceColor playerColor) {
        // ... (Original content unchanged) ...
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (board[r][c] != null && board[r][c].color() == playerColor) {
                    if (!calculateValidMoves(r, c).isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isCheckmate(PieceColor playerColor) {
        // ... (Original content unchanged) ...
        return isKingInCheck(playerColor) && !hasAnyLegalMoves(playerColor);
    }

    public boolean isStalemate(PieceColor playerColor) {
        // ... (Original content unchanged) ...
        return !isKingInCheck(playerColor) && !hasAnyLegalMoves(playerColor);
    }
}
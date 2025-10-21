package com.chess.model;

public record Piece(PieceType type, PieceColor color) {
    public String getUnicodeChar() {
        return switch (color) {
            case WHITE -> switch (type) {
                case PAWN -> "♙";
                case ROOK -> "♖";
                case KNIGHT -> "♘";
                case BISHOP -> "♗";
                case QUEEN -> "♕";
                case KING -> "♔";
            };
            case BLACK -> switch (type) {
                case PAWN -> "♟";
                case ROOK -> "♜";
                case KNIGHT -> "♞";
                case BISHOP -> "♝";
                case QUEEN -> "♛";
                case KING -> "♚";
            };
        };
    }
}

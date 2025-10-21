package com.chess.model;

public record MoveRecord(Move move, Piece capturedPiece, String pgn) {
}

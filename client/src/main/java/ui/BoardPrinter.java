package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import javax.print.DocFlavor;

public class BoardPrinter {
    public static String printBoard(ChessBoard board, ChessGame.TeamColor orientation) {
        StringBuilder boardString = new StringBuilder();
        int loopStart = 1;
        int loopEnd = 9;
        int direction = 1;
        if (orientation == ChessGame.TeamColor.BLACK) {
            loopStart = 8;
            loopEnd = 0;
            direction = -1;
        }

        for (int i = loopStart; i != loopEnd; i += direction) {
            for (int j = loopStart; j != loopEnd; j += direction) {
                ChessPiece currPiece = board.getPiece(new ChessPosition(1,1));
                String unicode = "";
                if (currPiece == null) {
                    boardString.append(EscapeSequences.EMPTY);
                    continue;
                }
                if (currPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    unicode = switch (currPiece.getPieceType()) {
                        case KING -> EscapeSequences.WHITE_KING;
                        case ROOK -> EscapeSequences.WHITE_ROOK;
                        case BISHOP -> EscapeSequences.WHITE_BISHOP;
                        case PAWN -> EscapeSequences.WHITE_PAWN;
                        case KNIGHT -> EscapeSequences.WHITE_KNIGHT;
                        case QUEEN -> EscapeSequences.WHITE_QUEEN;
                    };
                } else {
                    unicode = switch (currPiece.getPieceType()) {
                        case KING -> EscapeSequences.BLACK_KING;
                        case ROOK -> EscapeSequences.BLACK_ROOK;
                        case BISHOP -> EscapeSequences.BLACK_BISHOP;
                        case PAWN -> EscapeSequences.BLACK_PAWN;
                        case KNIGHT -> EscapeSequences.BLACK_KNIGHT;
                        case QUEEN -> EscapeSequences.BLACK_QUEEN;
                    };
                }
                boardString.append(unicode);
            }
            boardString.append(String.format("%n"));
        }
        return boardString.toString();
    }
}

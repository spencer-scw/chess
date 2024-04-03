package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import javax.print.DocFlavor;

public class BoardPrinter {
    public static String printBoard(ChessBoard board, ChessGame.TeamColor orientation) {
        StringBuilder boardString = new StringBuilder();
        int loopStart = 8;
        int loopEnd = 0;
        int direction = -1;
        if (orientation == ChessGame.TeamColor.WHITE) {
            loopStart = 1;
            loopEnd = 9;
            direction = 1;
        }

        boardString.append(String.format("%s%s%s", EscapeSequences.SET_BG_COLOR_BLUE, EscapeSequences.EMPTY, EscapeSequences.SET_TEXT_COLOR_BLACK));
        boardString.append((direction == 1) ? " a  b  c  d  e  f  g  h " : " h  g  f  e  d  c  b  a ");
        boardString.append(String.format("%s%s%n", EscapeSequences.EMPTY, EscapeSequences.RESET_BG_COLOR));
        for (int i = loopStart; i != loopEnd; i += direction) {
            boardString.append(String.format("%s %d %s", EscapeSequences.SET_BG_COLOR_BLUE, i, EscapeSequences.RESET_BG_COLOR));
            for (int j = loopStart; j != loopEnd; j += direction) {
                ChessPiece currPiece = board.getPiece(new ChessPosition(i, j));

                // Checkerboard pattern
                if ((i + j) % 2 == 0) {
                    boardString.append(EscapeSequences.SET_BG_COLOR_GREY);
                } else {
                    boardString.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                }

                // Blank square
                if (currPiece == null) {
                    boardString.append(EscapeSequences.EMPTY);
                    continue;
                }

                // All other pieces
                if (currPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    boardString.append(EscapeSequences.SET_TEXT_COLOR_WHITE);
                } else {
                    boardString.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
                }
                String unicode = switch (currPiece.getPieceType()) {
                    case KING -> EscapeSequences.BLACK_KING;
                    case ROOK -> EscapeSequences.BLACK_ROOK;
                    case BISHOP -> EscapeSequences.BLACK_BISHOP;
                    case PAWN -> EscapeSequences.BLACK_PAWN;
                    case KNIGHT -> EscapeSequences.BLACK_KNIGHT;
                    case QUEEN -> EscapeSequences.BLACK_QUEEN;
                };
                boardString.append(unicode);
            }
            boardString.append(String.format("%s%s %d %s%n", EscapeSequences.SET_BG_COLOR_BLUE, EscapeSequences.SET_TEXT_COLOR_BLACK, i, EscapeSequences.RESET_BG_COLOR));
        }
        boardString.append(String.format("%s%s%s", EscapeSequences.SET_BG_COLOR_BLUE, EscapeSequences.EMPTY, EscapeSequences.SET_TEXT_COLOR_BLACK));
        boardString.append((direction == 1) ? " a  b  c  d  e  f  g  h " : " h  g  f  e  d  c  b  a ");
        boardString.append(String.format("%s%s%s%n", EscapeSequences.EMPTY, EscapeSequences.RESET_BG_COLOR, EscapeSequences.RESET_TEXT_COLOR));
        return boardString.toString();
    }
}

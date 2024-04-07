package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.HashSet;

public class BoardPrinter {
    public static String printBoard(ChessBoard board, ChessGame.TeamColor orientation, HashSet<ChessPosition> highlightSquares) {
        StringBuilder boardString = new StringBuilder();
        int vLoopStart = 8;
        int vLoopEnd = 0;
        int vDirection = -1;
        if (orientation == ChessGame.TeamColor.BLACK) {
            vLoopStart = 1;
            vLoopEnd = 9;
            vDirection = 1;
        }
        int hLoopStart = 8;
        int hLoopEnd = 0;
        int hDirection = -1;
        if (orientation == ChessGame.TeamColor.WHITE) {
            hLoopStart = 1;
            hLoopEnd = 9;
            hDirection = 1;
        }

        boardString.append(String.format("%s%s%s", EscapeSequences.SET_BG_COLOR_BLUE, EscapeSequences.EMPTY, EscapeSequences.SET_TEXT_COLOR_BLACK));
        boardString.append((orientation == ChessGame.TeamColor.WHITE) ? " a  b  c  d  e  f  g  h " : " h  g  f  e  d  c  b  a ");
        boardString.append(String.format("%s%s%n", EscapeSequences.EMPTY, EscapeSequences.RESET_BG_COLOR));
        for (int i = vLoopStart; i != vLoopEnd; i += vDirection) {
            boardString.append(String.format("%s %d %s", EscapeSequences.SET_BG_COLOR_BLUE, i, EscapeSequences.RESET_BG_COLOR));
            for (int j = hLoopStart; j != hLoopEnd; j += hDirection) {
                ChessPosition currPosition = new ChessPosition(i, j);
                ChessPiece currPiece = board.getPiece(currPosition);



                // Checkerboard pattern
                if ((i + j) % 2 == 0) {
                    boardString.append(EscapeSequences.SET_BG_COLOR_GREY);
                } else {
                    boardString.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                }

                // Highlighting
                if (highlightSquares != null) {
                    if (highlightSquares.contains(currPosition)) {
                        boardString.append(EscapeSequences.SET_BG_COLOR_GREEN);
                    }
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
        boardString.append((orientation == ChessGame.TeamColor.WHITE) ? " a  b  c  d  e  f  g  h " : " h  g  f  e  d  c  b  a ");
        boardString.append(String.format("%s%s%s%n", EscapeSequences.EMPTY, EscapeSequences.RESET_BG_COLOR, EscapeSequences.RESET_TEXT_COLOR));
        return boardString.toString();
    }
}

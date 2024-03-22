package chess;

import java.io.Console;
import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] boardSquares = new ChessPiece[8][8];

    public ChessBoard() {}

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int row = position.getRow();
        int col = position.getColumn();
        boardSquares[8 - row][col - 1] = piece;
    }

    public ChessPiece removePiece(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        ChessPiece piece = getPiece(position);
        boardSquares[8 - row][col - 1] = null;
        return piece;
    }

    public void movePiece(ChessMove move) {
        ChessPiece piece = removePiece(move.getStartPosition());
        if (move.promotionPiece == null)
            addPiece(move.getEndPosition(), piece);
        else
            addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));

    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        return boardSquares[8 - row][col - 1];
    }

    public ChessPosition findPiece(ChessPiece piece) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition currPosition = new ChessPosition(i, j);
                ChessPiece currPiece = getPiece(currPosition);
                if (currPiece != null) {
                    if (currPiece.equals(piece))
                        return currPosition;
                }
            }
        }
        return null;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        boardSquares = new ChessPiece[8][8];
        char[][] defaultBoard = {
                {'r','n','b','q','k','b','n','r'},
                {'p','p','p','p','p','p','p','p'},
                {' ',' ',' ',' ',' ',' ',' ',' '},
                {' ',' ',' ',' ',' ',' ',' ',' '},
                {' ',' ',' ',' ',' ',' ',' ',' '},
                {' ',' ',' ',' ',' ',' ',' ',' '},
                {'P','P','P','P','P','P','P','P'},
                {'R','N','B','Q','K','B','N','R'}
        };

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece currPiece = switch (defaultBoard[i][j]) {
                    case 'P' -> new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
                    case 'R' -> new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
                    case 'N' -> new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
                    case 'B' -> new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
                    case 'Q' -> new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
                    case 'K' -> new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
                    case 'p' -> new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
                    case 'r' -> new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
                    case 'n' -> new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
                    case 'b' -> new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
                    case 'q' -> new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
                    case 'k' -> new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
                    default -> null;
                };
                boardSquares[i][j] = currPiece;
            }
        }
    }

    public void copyBoard (ChessBoard board) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition currPosition = new ChessPosition(i, j);

                ChessPiece oldPiece = board.getPiece(currPosition);

                if (oldPiece == null)
                    addPiece(currPosition, null);
                else {
                    ChessPiece newPiece = new ChessPiece(oldPiece.getTeamColor(), oldPiece.getPieceType());
                    addPiece(currPosition, newPiece);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "boardSquares=" + Arrays.deepToString(boardSquares) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(boardSquares, that.boardSquares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(boardSquares);
    }
}

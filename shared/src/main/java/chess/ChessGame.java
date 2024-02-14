package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board = new ChessBoard();
    private TeamColor turnColor;

    public ChessGame() {
        turnColor = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turnColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turnColor = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece currPiece = board.getPiece(startPosition);

        if (currPiece == null)
            return null;

        Collection<ChessMove> allMoves = currPiece.pieceMoves(board, startPosition);
        HashSet<ChessMove> invalidMoves = new HashSet<>();

        for (ChessMove move : allMoves) {
            ChessGame tempGame = new ChessGame();
            tempGame.setTeamTurn(currPiece.getTeamColor());

            ChessBoard tempBoard = new ChessBoard();
            tempBoard.copyBoard(board);
            tempGame.setBoard(tempBoard);

            tempGame.getBoard().movePiece(move);
            if (tempGame.isInCheck(currPiece.getTeamColor()))
                invalidMoves.add(move);
        }

        allMoves.removeAll(invalidMoves);

        return allMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (validMoves(move.getStartPosition()).contains(move)) {
            if (turnColor == board.getPiece(move.getStartPosition()).getTeamColor()) {
                board.movePiece(move);
                turnColor = switch (turnColor){
                    case BLACK -> TeamColor.WHITE;
                    case WHITE -> TeamColor.BLACK;
                };
            } else {
                throw new InvalidMoveException();
            }
        } else {
            throw new InvalidMoveException();
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = board.findPiece(new ChessPiece(teamColor, ChessPiece.PieceType.KING));
        if (kingPosition == null) {
            return false;
        }
        ChessBoard tempBoard = new ChessBoard();

        for (ChessPiece.PieceType dangerPossibility : ChessPiece.PieceType.values()) {
            tempBoard.copyBoard(board);
            ChessPiece tempPiece = new ChessPiece(teamColor, dangerPossibility);
            tempBoard.removePiece(kingPosition);
            tempBoard.addPiece(kingPosition, tempPiece);

            Collection<ChessMove> lookingAtKing = tempPiece.pieceMoves(tempBoard, kingPosition);
            for (ChessMove currMove : lookingAtKing) {
                ChessPiece currPiece = tempBoard.getPiece(currMove.getEndPosition());
                if (currPiece != null) {
                    if (currPiece.getTeamColor() != teamColor && currPiece.getPieceType() == dangerPossibility) {
                            return true;
                    }
                }
            }

        }
        return false;
    }

    private HashSet<ChessMove> getTeamValidMoves(TeamColor teamColor) {
        HashSet<ChessMove> validMoves = new HashSet<>();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition currPosition = new ChessPosition(i, j);
                ChessPiece currPiece = board.getPiece(currPosition);
                if (currPiece == null)
                    continue;
                if (currPiece.getTeamColor() == teamColor) {
                    validMoves.addAll(validMoves(currPosition));
                }
            }
        }
        return validMoves;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && getTeamValidMoves(teamColor).isEmpty();
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && getTeamValidMoves(teamColor).isEmpty();
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}

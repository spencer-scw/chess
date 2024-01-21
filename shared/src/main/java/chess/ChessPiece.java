package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    ChessGame.TeamColor color;
    PieceType type;
    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.color = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return switch (type){
            case KING -> kingMoves(board, myPosition);
            case QUEEN -> queenMoves(board, myPosition);
            case BISHOP -> bishopMoves(board, myPosition);
            case KNIGHT -> knightMoves(board, myPosition);
            case ROOK -> rookMoves(board, myPosition);
            case PAWN -> pawnMoves(board, myPosition);
        };
    }

    private void checkPosition(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> validMoves, ChessPosition newPos) {
        if (!newPos.isIndexInBounds()) {
            return;
        }

        ChessMove currMove = new ChessMove(myPosition, newPos, null);
        ChessPiece newPiece = board.getPiece(newPos);

        if (newPiece == null ) {
            validMoves.add(currMove);
        }
        else if (newPiece.getTeamColor() != color) {
            validMoves.add(currMove);
        }
    }

    private void pawnCheckPosition(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> validMoves, ChessPosition newPos, boolean capture) {
        if (!newPos.isIndexInBounds()) {
            return;
        }
        ChessMove currMove = new ChessMove(myPosition, newPos, null);
        if ( (color == ChessGame.TeamColor.WHITE && newPos.getRow() == 8) || (color == ChessGame.TeamColor.BLACK && newPos.getRow() == 1)) {
            for (PieceType option : PieceType.values()) {
                if (option != PieceType.PAWN && option != PieceType.KING) {
                    currMove = new ChessMove(myPosition, newPos, option);
                    pawnMoveHelper(board, validMoves, newPos, capture, currMove);
                }
            }
        } else {

            pawnMoveHelper(board, validMoves, newPos, capture, currMove);
        }
    }

    private void pawnMoveHelper(ChessBoard board, HashSet<ChessMove> validMoves, ChessPosition newPos, boolean capture, ChessMove currMove) {
        ChessPiece newPiece = board.getPiece(newPos);
        if (!capture) {
            if (newPiece == null) {
                validMoves.add(currMove);
            }
        } else {
            if (newPiece != null) {
                if (newPiece.getTeamColor() != color) {
                    validMoves.add(currMove);
                }
            }
        }
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        int direction = switch (color) {
            case BLACK -> -1;
            case WHITE -> 1;
        };
        int[] captureDirections = {-1, 1};

        HashSet<ChessMove> validMoves = new HashSet<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        ChessPosition forward = new ChessPosition(row + direction, col);
        pawnCheckPosition(board, myPosition, validMoves, forward, false);

        if (validMoves.size() == 1 && ((row == 2 && color == ChessGame.TeamColor.WHITE ) || (row == 7 && color == ChessGame.TeamColor.BLACK))) {
            ChessPosition doubleForward = new ChessPosition(row + 2 * direction, col);
            pawnCheckPosition(board, myPosition, validMoves, doubleForward, false);
        }

        for (int i : captureDirections) {
            ChessPosition currCapture = new ChessPosition(row + direction, col + i);
            pawnCheckPosition(board, myPosition, validMoves, currCapture, true);
        }

        return  validMoves;
    }

    private void recursiveWalker(int dirX, int dirY, ChessPosition origin, ChessPosition currPosition, ChessBoard board, HashSet<ChessMove> validMoves) {
        int row = currPosition.getRow();
        int col = currPosition.getColumn();
        ChessPosition newPosition = new ChessPosition(row + dirX, col + dirY);

        ChessMove currMove = new ChessMove(origin, currPosition, null);

        if (currPosition.isIndexInBounds()) {
            ChessPiece currPiece = board.getPiece(currPosition);
            if (currPiece == null) {
                validMoves.add(currMove);
                recursiveWalker(dirX, dirY, origin, newPosition, board, validMoves);
            } else if (board.getPiece(currPosition).getTeamColor() != color) {
                validMoves.add(currMove);
            }
        }
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        int[] directionsX = {-1, 0, 1, 0};
        int[] directionsY = {0, 1, 0, -1};
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        HashSet<ChessMove> validMoves = new HashSet<>();
        for (int i = 0; i < directionsX.length; i++) {
            ChessPosition newPosition = new ChessPosition(row + directionsX[i], col + directionsY[i]);
            recursiveWalker(directionsX[i], directionsY[i], myPosition, newPosition, board, validMoves);
        }
        return validMoves;
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        int[] stepsX = {-2, -2, -1, -1, 1, 1, 2, 2};
        int[] stepsY = {1, -1, 2, -2, 2, -2, 1, -1};

        HashSet<ChessMove> validMoves = new HashSet<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        for (int i = 0; i < stepsX.length; i++) {
            ChessPosition newPos = new ChessPosition(row + stepsX[i], col + stepsY[i]);
            checkPosition(board, myPosition, validMoves, newPos);
        }
        return validMoves;
    }



    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        int[] directions = {-1, 1};
        HashSet<ChessMove> validMoves = new HashSet<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        for (int directionX : directions) {
            for (int directionY : directions) {
                ChessPosition newPosition = new ChessPosition(row + directionX, col + directionY);
                recursiveWalker(directionX, directionY, myPosition, newPosition, board, validMoves);
            }
        }
        return validMoves;
    }

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> validMoves = new HashSet<>();
        validMoves.addAll(bishopMoves(board, myPosition));
        validMoves.addAll(rookMoves(board, myPosition));
        return validMoves;
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        int[] steps = {-1, 0, 1};
        HashSet<ChessMove> validMoves = new HashSet<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        for (int stepX : steps) {
            for (int stepY : steps) {

                ChessPosition newPos = new ChessPosition(row + stepX, col + stepY);
                checkPosition(board, myPosition, validMoves, newPos);
            }
        }
        return validMoves;
    }


    @Override
    public String toString() {
        return "ChessPiece{" +
                "color=" + color +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return color == that.color && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
    }
}

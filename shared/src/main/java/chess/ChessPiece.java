package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor color;
    private PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        color = pieceColor;
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
        HashSet<ChessMove> validMoves = new HashSet<>();
        validMoves = switch (type) {
            case PAWN -> pawnMoves(board, myPosition, validMoves);
            case ROOK -> rookMoves(board, myPosition, validMoves);
            case KNIGHT -> knightMoves(board, myPosition, validMoves);
            case BISHOP -> bishopMoves(board, myPosition, validMoves);
            case QUEEN -> queenMoves(board, myPosition, validMoves);
            case KING -> kingMoves(board, myPosition, validMoves);
        };
        return validMoves;
    }

    private boolean isIndexInBounds(ChessPosition pos) {
        int row = pos.getRow();
        int col = pos.getColumn();
        return row > 0 && row < 9 && col > 0 && col < 9;
    }

    private HashSet<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> validMoves) {
        int direction = 0;
        int promotionRow = 0;
        int initalRow = 0;
        switch (color) {
            case BLACK -> {
                direction = -1;
                promotionRow = 2;
                initalRow = 7;
            }
            case WHITE -> {
                direction = 1;
                promotionRow = 7;
                initalRow = 2;
            }
        }

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        // Check forward movement
        ChessPosition newPos = new ChessPosition(row + direction, col);
        if (isIndexInBounds(newPos)) {
            ChessPiece target = board.getPiece(newPos);
            ChessMove currMove = new ChessMove(myPosition, newPos, null);
            if (target == null) {
                if (row != promotionRow) {
                    validMoves.add(currMove);
                } else {
                    validMoves.addAll(pawnPromotionGenerator(currMove));
                }
            }
        }

        // Check double forward
        if (row == initalRow && !validMoves.isEmpty()) {
            newPos = new ChessPosition(row + 2 * direction, col);
            if (isIndexInBounds(newPos)) {
                ChessPiece target = board.getPiece(newPos);
                ChessMove currMove = new ChessMove(myPosition, newPos, null);
                if (target == null) {
                    validMoves.add(currMove);
                }
            }
        }

        // Check Captures
        for (int i : new int[]{-1, 1}){
            newPos = new ChessPosition(row + direction, col + i);
            if (isIndexInBounds(newPos)) {
                ChessPiece target = board.getPiece(newPos);
                ChessMove currMove = new ChessMove(myPosition, newPos, null);
                if (target != null) {
                    if (target.getTeamColor() != color) {
                        if (row != promotionRow) {
                            validMoves.add(currMove);
                        } else {
                            validMoves.addAll(pawnPromotionGenerator(currMove));
                        }
                    }
                }
            }
        }

        return validMoves;
    }

    private HashSet<ChessMove> pawnPromotionGenerator(ChessMove blankMove) {
        HashSet<ChessMove> promotions = new HashSet<>();
        for (PieceType option : PieceType.values()) {
            if (option != PieceType.KING && option != PieceType.PAWN) {
                ChessMove newMove = new ChessMove(blankMove.getStartPosition(), blankMove.getEndPosition(), option);
                promotions.add(newMove);
            }
        }
        return promotions;
    }

    private void recursiveWalker(ChessBoard board, ChessPosition currPosition, int directionX, int directionY, HashSet<ChessPosition> validPositions) {
        int row = currPosition.getRow();
        int col = currPosition.getColumn();

        if (isIndexInBounds(currPosition)) {
            ChessPiece currPiece = board.getPiece(currPosition);
            if (currPiece == null) {
                validPositions.add(currPosition);
                ChessPosition newPos = new ChessPosition(row + directionY, col + directionX);
                recursiveWalker(board, newPos, directionX, directionY, validPositions);
            } else if (currPiece.getTeamColor() != color) {
                validPositions.add(currPosition);
            }
        }
    }

    private HashSet<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> validMoves) {
        int[] directionsX = {-1, 0, 1, 0};
        int[] directionsY = {0, 1, 0, -1};

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        HashSet<ChessPosition> validPositions = new HashSet<>();

        for (int i = 0; i < directionsX.length; i++) {
            ChessPosition newPos = new ChessPosition(row + directionsY[i], col + directionsX[i]);
            recursiveWalker(board, newPos, directionsX[i], directionsY[i], validPositions);
        }

        for (ChessPosition position : validPositions) {
            validMoves.add(new ChessMove(myPosition, position, null));
        }
        return validMoves;
    }

    private HashSet<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> validMoves) {
        int[] directions = {-1, 1};

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        HashSet<ChessPosition> validPositions = new HashSet<>();

        for (int i : directions) {
            for (int j : directions) {
                ChessPosition newPos = new ChessPosition(row + i, col + j);
                recursiveWalker(board, newPos, j, i, validPositions);
            }
        }

        for (ChessPosition position : validPositions) {
            validMoves.add(new ChessMove(myPosition, position, null));
        }
        return validMoves;
    }

    private HashSet<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> validMoves) {
        validMoves.addAll(bishopMoves(board, myPosition, validMoves));
        validMoves.addAll(rookMoves(board, myPosition, validMoves));
        return validMoves;
    }

    private HashSet<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> validMoves) {
        int[] positionsX = {-2, -2, -1, -1, 1,  1, 2,  2};
        int[] positionsY = { 1, -1,  2, -2, 2, -2, 1, -1};

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        for (int i = 0; i < positionsY.length; i++) {
            ChessPosition newPos = new ChessPosition(row + positionsY[i], col + positionsX[i]);
            ChessMove currMove = new ChessMove(myPosition, newPos, null);
            if (isIndexInBounds(newPos)){
                ChessPiece target = board.getPiece(newPos);
                if (target == null) {
                    validMoves.add(currMove);
                } else if (target.getTeamColor() != color) {
                    validMoves.add(currMove);
                }
            }
        }
        return validMoves;
    }

    private HashSet<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> validMoves) {
        int[] positions = {-1, 0, 1};

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        for (int i : positions) {
            for (int j : positions) {
                ChessPosition newPos = new ChessPosition(row + i, col + j);
                ChessMove currMove = new ChessMove(myPosition, newPos, null);
                if (isIndexInBounds(newPos)){
                    ChessPiece target = board.getPiece(newPos);
                    if (target == null) {
                        validMoves.add(currMove);
                    } else if (target.getTeamColor() != color) {
                        validMoves.add(currMove);
                    }
                }
            }
        }
        return validMoves;
    }

    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ChessPiece that = (ChessPiece) object;
        return color == that.getTeamColor() && type == that.getPieceType();
    }

    public int hashCode() {
        return java.util.Objects.hash(super.hashCode(), color, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "color=" + color +
                "\n, type=" + type +
                '}';
    }
}

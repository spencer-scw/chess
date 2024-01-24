package chess.pieceMoves;


import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

public class KingMove extends ChessMove {
    public KingMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece) {
        super(startPosition, endPosition, promotionPiece);
    }
}

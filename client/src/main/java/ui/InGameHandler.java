package ui;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import serverFacade.ServerFacade;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class InGameHandler {
    private final ServerFacade serverFacade;
    private final SessionInfo sessionInfo;

    public InGameHandler(ServerFacade serverFacade, SessionInfo sessionInfo) {
        this.serverFacade = serverFacade;
        this.sessionInfo = sessionInfo;
    }

    protected String redraw() {
        return BoardPrinter.printBoard(sessionInfo.getBoard(), sessionInfo.getTeamColor(), null);
    }

    protected String highlight(String[] params) {
        ChessPosition position = gridIndex(params[0]);
        HashSet<ChessMove> moves = (HashSet<ChessMove>) sessionInfo.getBoard().getPiece(position).pieceMoves(sessionInfo.getBoard(), position);
        HashSet<ChessPosition> endPositions = new HashSet<>();
        for (ChessMove move: moves) {
            endPositions.add(move.getEndPosition());
        }
        return BoardPrinter.printBoard(sessionInfo.getBoard(), sessionInfo.getTeamColor(), endPositions);
    }

    protected String makeMove(String[] params) throws IOException {
        if (params.length < 2) {
            return "Incorrect syntax. Please enter your move in the form of <origin> <destination> [promotion_piece]";
        }
        ChessPosition startPosition = gridIndex(params[0]);
        ChessPosition endPosition = gridIndex(params[1]);
        ChessPiece.PieceType promotionPiece = null;
        if (params.length >= 3) {
            promotionPiece = ChessPiece.PieceType.valueOf(params[2].toUpperCase());
        }
        serverFacade.makeMove(sessionInfo.getAuthToken(), sessionInfo.getGameID(), new ChessMove(startPosition, endPosition, promotionPiece));
        return "";
    }

    protected String leave() throws IOException {
        serverFacade.leave(sessionInfo.getAuthToken(), sessionInfo.getGameID());
        sessionInfo.setClientState(State.SIGNEDIN);

        return "";
    }

    protected String resign() throws IOException {
        serverFacade.resign(sessionInfo.getAuthToken(), sessionInfo.getGameID());
        return "";
    }

    private ChessPosition gridIndex(String index) {
        int col = Character.getNumericValue(index.toUpperCase().charAt(0)) - 9;
        int row = Integer.parseInt(index.substring(1));
        return new ChessPosition(row, col);
    }

}

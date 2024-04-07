package ui;

import chess.ChessMove;
import chess.ChessPosition;
import serverFacade.ServerFacade;

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

    private ChessPosition gridIndex(String index) {
        int col = Character.getNumericValue(index.toUpperCase().charAt(0)) - 9;
        int row = Integer.parseInt(index.substring(1));
        return new ChessPosition(row, col);
    }

}

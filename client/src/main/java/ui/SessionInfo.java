package ui;

import chess.ChessBoard;
import chess.ChessGame;

public class SessionInfo {
    private String authToken;
    private State clientState;
    private ChessBoard board;
    private ChessGame.TeamColor teamColor;

    private Integer gameID;



    public SessionInfo(String authToken, State clientState, ChessBoard board, ChessGame.TeamColor teamColor) {
        this.authToken = authToken;
        this.clientState = clientState;
        this.board = board;
        this.teamColor = teamColor;
        this.gameID = 0;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public State getClientState() {
        return clientState;
    }

    public void setClientState(State clientState) {
        this.clientState = clientState;
    }

    public ChessBoard getBoard() {
        return board;
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    public void setTeamColor(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }

    public Integer getGameID() {
        return gameID;
    }
}

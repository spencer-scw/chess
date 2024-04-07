package ui;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.internal.LinkedTreeMap;
import serverFacade.ServerFacade;

import java.util.ArrayList;
import java.util.Objects;

public class SignedInHandler {
    private final ServerFacade serverFacade;
    private final SessionInfo sessionInfo;

    private ArrayList<Double> lastListOrder;

    public SignedInHandler(ServerFacade serverFacade, SessionInfo sessionInfo) {
        this.serverFacade = serverFacade;
        this.sessionInfo = sessionInfo;
        lastListOrder = new ArrayList<>();
    }

    protected String logOut() {
        try {
            serverFacade.logOut(sessionInfo.getAuthToken());
            sessionInfo.setAuthToken("");
            sessionInfo.setClientState(State.SIGNEDOUT);
            return "Logout successful.";
        } catch (Exception e) {
            return "Logout failed.";
        }
    }

    protected String createGame(String[] params) {
        try {
            return String.format("Created game %s with id %d",
                    params[0],
                    Math.round(
                            (Double) serverFacade.createGame(params, sessionInfo.getAuthToken()).get("gameID")));
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    protected String listGames() {
        StringBuilder result = new StringBuilder();
        result.append(String.format("ID   | gameName        | whitePlayer     | blackPlayer   %n"));
        result.append(String.format("-----+-----------------+-----------------+---------------%n"));

        try {
            ArrayList games = (ArrayList) serverFacade.listGames(sessionInfo.getAuthToken()).get("games");
            lastListOrder = new ArrayList<>();
            for (var game: games) {
                var gameID = ((LinkedTreeMap<?, ?>) game).get("gameID");
                lastListOrder.add((Double) gameID);

                var gameName = ((LinkedTreeMap<?, ?>) game).get("gameName");
                var whiteUsername = ((LinkedTreeMap<?, ?>) game).get("whiteUsername");
                if (whiteUsername == null) {
                    whiteUsername = "-";
                }
                var blackUsername = ((LinkedTreeMap<?, ?>) game).get("blackUsername");
                if (blackUsername == null) {
                    blackUsername = "-";
                }
                result.append(String.format(
                        " %-3d | %-15s | %-15s | %-15s %n",
                        lastListOrder.size() - 1,
                        gameName,
                        whiteUsername,
                        blackUsername
                ));
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return result.toString();
    }

    protected String joinGame(String[] params) {
        try {
            if (params.length >= 2) {
                serverFacade.joinGame(new String[]{params[1], lastListOrder.get(Integer.parseInt(params[0])).toString()}, sessionInfo.getAuthToken());
            } else {
                String assignedColor = "";
                String gameID = "";
                ArrayList games = (ArrayList) serverFacade.listGames(sessionInfo.getAuthToken()).get("games");
                for (var game: games) {
                    gameID = ((Double) ((LinkedTreeMap<?, ?>) game).get("gameID")).toString();
                    var whiteUsername = ((LinkedTreeMap<?, ?>) game).get("whiteUsername");
                    var blackUsername = ((LinkedTreeMap<?, ?>) game).get("blackUsername");
                    if (Objects.equals(gameID, lastListOrder.get(Integer.parseInt(params[0])).toString())) {
                        if (whiteUsername == null && blackUsername == null) {
                            if (Math.random() > .5) {
                                assignedColor = "WHITE";
                            } else {
                                assignedColor = "BLACK";
                            }
                        } else if (blackUsername == null) {
                            assignedColor = "BLACK";
                        } else if (whiteUsername == null) {
                            assignedColor = "WHITE";
                        } else {
                            return "Desired game is full. Please observe this game or join another.";
                        }
                        serverFacade.joinGame(new String[]{assignedColor, gameID}, sessionInfo.getAuthToken());
                    }

                }
            }
            sessionInfo.setClientState(State.INGAME);
        } catch (Exception e) {
            return e.getMessage();
        }

        return BoardPrinter.printBoard(sessionInfo.getBoard(), ChessGame.TeamColor.WHITE, null) +
                String.format("%n") +
                BoardPrinter.printBoard(sessionInfo.getBoard(), ChessGame.TeamColor.BLACK, null);
    }

    protected String observeGame(String[] params) {
        try {
            serverFacade.joinGame(new String[]{lastListOrder.get(Integer.parseInt(params[0])).toString()}, sessionInfo.getAuthToken());
            return BoardPrinter.printBoard(sessionInfo.getBoard(), ChessGame.TeamColor.WHITE, null) +
                    String.format("%n") +
                    BoardPrinter.printBoard(sessionInfo.getBoard(), ChessGame.TeamColor.BLACK, null);
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}

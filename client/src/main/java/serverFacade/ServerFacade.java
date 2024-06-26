package serverFacade;

import chess.ChessGame;
import chess.ChessMove;
import serverFacade.http.HttpCommunicator;
import serverFacade.websocket.ServerMessageObserver;
import serverFacade.websocket.WebsocketCommunicator;
import webSocketMessages.userCommands.*;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class ServerFacade {
    private final HttpCommunicator httpCommunicator;
    private final WebsocketCommunicator websocketCommunicator;

    public ServerFacade(String serverURL, ServerMessageObserver serverMessageObserver) throws DeploymentException, URISyntaxException, IOException {
        httpCommunicator = new HttpCommunicator(serverURL);
        websocketCommunicator = new WebsocketCommunicator(serverURL, serverMessageObserver);
    }

    //HTTP METHODS

    public Map logIn(String[] params) throws Exception {
        return httpCommunicator.handleHTTP(
                "POST",
                "session",
                "",
                Map.of("username", params[0], "password", params[1])
        );

    }

    public Map register(String[] params) throws IOException, URISyntaxException {
        return httpCommunicator.handleHTTP(
                "POST",
                "user",
                "",
                Map.of("username", params[0], "password", params[1], "email", params[2])
        );

    }

    public void logOut(String authToken) throws IOException, URISyntaxException {
        httpCommunicator.handleHTTP(
                "DELETE",
                "session",
                authToken,
                null
        );
    }

    public Map createGame(String[] params, String authToken) throws IOException, URISyntaxException {
        return httpCommunicator.handleHTTP(
                "POST",
                "game",
                authToken,
                Map.of("gameName", params[0])
        );
    }

    public Map listGames(String authToken) throws IOException, URISyntaxException {
        return httpCommunicator.handleHTTP(
                "GET",
                "game",
                authToken,
                null
        );
    }

    public void joinGame(String[] params, String authToken) throws IOException, URISyntaxException {
        if (params.length >= 2) {
            httpCommunicator.handleHTTP(
                    "PUT",
                    "game",
                    authToken,
                    Map.of("playerColor", params[0].toUpperCase(), "gameID", Double.parseDouble(params[1]))
            );
            joinPlayer(authToken, Integer.parseInt(params[1].split("\\.")[0]), ChessGame.TeamColor.valueOf(params[0].toUpperCase()));
        } else {
            httpCommunicator.handleHTTP(
                    "PUT",
                    "game",
                    authToken,
                    Map.of("gameID", Double.parseDouble(params[0]))
            );
            joinObserver(authToken, Integer.parseInt(params[0].split("\\.")[0]));
        }
    }

    // WEBSOCKET METHODS

    public void joinPlayer(String authToken, Integer gameID, ChessGame.TeamColor playerColor) throws IOException {
        JoinPlayer joinCommand = new JoinPlayer(authToken, gameID, playerColor);
        websocketCommunicator.sendUserGameCommand(joinCommand);
    }

    public void joinObserver(String authToken, Integer gameID) throws IOException {
        JoinObserver joinCommand = new JoinObserver(authToken, gameID);
        websocketCommunicator.sendUserGameCommand(joinCommand);
    }

    public void makeMove(String authToken, Integer gameID, ChessMove move) throws IOException {
        MakeMove moveCommand = new MakeMove(authToken, gameID, move);
        websocketCommunicator.sendUserGameCommand(moveCommand);
    }

    public void leave(String authToken, Integer gameID) throws IOException {
        Leave leaveCommand = new Leave(authToken, gameID);
        websocketCommunicator.sendUserGameCommand(leaveCommand);
    }

    public void resign(String authToken, Integer gameID) throws IOException {
        Resign resignCommand = new Resign(authToken, gameID);
        websocketCommunicator.sendUserGameCommand(resignCommand);
    }

}

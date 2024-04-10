package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.interfaces.AuthDAO;
import dataAccess.interfaces.GameDAO;
import dataAccess.interfaces.UserDAO;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.GameService;
import service.UserService;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinPlayer;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Timer;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {

        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer(userGameCommand.getAuthString(), session, userGameCommand.getGameID(), new Gson().fromJson(message, JoinPlayer.class).getPlayerColor());
            case JOIN_OBSERVER -> joinObserver(userGameCommand.getAuthString(), session, userGameCommand.getGameID());
            case MAKE_MOVE -> makeMove();
            case LEAVE -> leave();
            case RESIGN -> resign();
        }
    }
    private void joinPlayer(String authString, Session session, Integer gameID, ChessGame.TeamColor teamColor) throws IOException, DataAccessException {
        connections.add(authString, session);
        String username;
        try {
            username = authDAO.getAuth(authString).username();
        } catch (DataAccessException e) {
            connections.send(authString, new ErrorMessage("Unauthorized"));
            return;
        }
        LoadGame loadGame;
        try {
            loadGame = new LoadGame(gameDAO.getGame(gameID));
        } catch (DataAccessException e) {
            connections.send(authString, new ErrorMessage("Game does not exist."));
            return;
        }
        if (teamColor == ChessGame.TeamColor.BLACK && !Objects.equals(loadGame.getGame().blackUsername(), username) ||
            teamColor == ChessGame.TeamColor.WHITE && !Objects.equals(loadGame.getGame().whiteUsername(), username)) {
            connections.send(authString, new ErrorMessage("Attempted to join wrong team."));
            return;
        }
        connections.send(authString, loadGame);

        var joinMessage = String.format("%s joined the game.", username);
        var notification = new Notification(joinMessage);
        connections.broadcast(authString, notification);
    }

    private void joinObserver(String authString, Session session, Integer gameID) throws DataAccessException, IOException {
        connections.add(authString, session);

        LoadGame loadGame = new LoadGame(gameDAO.getGame(gameID));
        connections.send(authString, loadGame);

        var joinMessage = String.format("%s is now observing this game. Play well!", authString);
        var notification = new Notification(joinMessage);
        connections.broadcast(authString, notification);
    }

    private void makeMove() {
    }

    private void leave() {
    }

    private void resign() {
    }
}
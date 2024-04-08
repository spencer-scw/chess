package server.websocket;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.interfaces.AuthDAO;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;
import java.util.Timer;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final AuthDAO authDAO;

    public WebSocketHandler(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer(authDAO.getAuth(userGameCommand.getAuthString()).username(), session);
            case JOIN_OBSERVER -> joinObserver();
            case MAKE_MOVE -> makeMove();
            case LEAVE -> leave();
            case RESIGN -> resign();
        }
    }

    private void joinPlayer(String username, Session session) throws IOException {
        connections.add(username, session);
        var message = String.format("%s joined the game.", username);
        var notification = new Notification(message);
        connections.broadcast(username, notification);
    }

    private void joinObserver() {
    }

    private void makeMove() {
    }

    private void leave() {
    }

    private void resign() {
    }
}
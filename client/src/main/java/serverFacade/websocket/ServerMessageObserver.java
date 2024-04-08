package serverFacade.websocket;


import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;

public interface ServerMessageObserver {
    void handleLoadGame(ServerMessage loadGame);
    void handleError(ServerMessage error);
    void handleNotification(ServerMessage notification);
}

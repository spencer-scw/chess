package serverFacade.websocket;


import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;

public interface ServerMessageObserver {
    void handleLoadGame(LoadGame loadGame);
    void handleError(ErrorMessage error);
    void handleNotification(Notification notification);
}

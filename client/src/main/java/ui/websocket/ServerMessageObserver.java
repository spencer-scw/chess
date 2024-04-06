package ui.websocket;


import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;

public interface ServerMessageObserver {
    void handleLoadGame(LoadGame loadGame);
    void handleError(Error error);
    void handleNotification(Notification notification);
}

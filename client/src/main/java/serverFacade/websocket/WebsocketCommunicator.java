package serverFacade.websocket;

import com.google.gson.Gson;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketCommunicator extends Endpoint {
    Session session;
    ServerMessageObserver serverMessageObserver;
    public WebsocketCommunicator(String serverURL, ServerMessageObserver serverMessageObserver) throws URISyntaxException, DeploymentException, IOException {
        URI uri = new URI(String.format("ws://%s/connect", serverURL));
        this.serverMessageObserver = serverMessageObserver;

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler( new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                switch (serverMessage.getServerMessageType()) {
                    case LOAD_GAME -> serverMessageObserver.handleLoadGame(new Gson().fromJson(message, LoadGame.class));
                    case ERROR -> serverMessageObserver.handleError(new Gson().fromJson(message, ErrorMessage.class));
                    case NOTIFICATION -> serverMessageObserver.handleNotification(new Gson().fromJson(message, Notification.class));
                }
            }
        });

    }

    public void sendUserGameCommand(UserGameCommand command) throws IOException {
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}

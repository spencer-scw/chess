package ui.websocket;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketCommunicator extends Endpoint {
    Session session;
    public WebsocketCommunicator(String serverURL) throws URISyntaxException, DeploymentException, IOException {
        URI uri = new URI(String.format("ws://%s/connect", serverURL));

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}

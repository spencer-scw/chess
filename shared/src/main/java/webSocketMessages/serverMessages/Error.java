package webSocketMessages.serverMessages;

public class Error extends ServerMessage{
    public Error(String errorMessage) {
        super(ServerMessageType.ERROR, errorMessage);
    }
}

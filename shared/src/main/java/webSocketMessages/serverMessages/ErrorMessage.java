package webSocketMessages.serverMessages;

public class ErrorMessage extends ServerMessage{
    String errorMessage;
    public ErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR, errorMessage);
        this.errorMessage = errorMessage;
    }
}

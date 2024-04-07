package webSocketMessages.userCommands;

public class JoinObserver extends UserGameCommand {
    public JoinObserver(String authToken, Integer gameID) {
        super(authToken, gameID);
        this.commandType = CommandType.JOIN_OBSERVER;
    }
}

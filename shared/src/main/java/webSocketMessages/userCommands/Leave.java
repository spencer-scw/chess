package webSocketMessages.userCommands;

public class Leave extends UserGameCommand{
    public Leave(String authToken, Integer gameID) {
        super(authToken, gameID);
        this.commandType = CommandType.MAKE_MOVE;
    }
}

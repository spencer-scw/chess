package webSocketMessages.userCommands;

import chess.ChessMove;

public class MakeMove extends UserGameCommand{
    ChessMove move;
    public MakeMove(String authToken, Integer gameID, ChessMove move) {
        super(authToken, gameID);
        this.commandType = CommandType.MAKE_MOVE;
        this.move = move;
    }

    public ChessMove getMove() {
        return move;
    }
}

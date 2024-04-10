package webSocketMessages.serverMessages;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

public class LoadGame extends ServerMessage{
    GameData game;
    public LoadGame(GameData game) {
        super(ServerMessageType.LOAD_GAME, "Loading game...");
        this.game = game;
    }

    public GameData getGame() {
        return game;
    }
}

package service;

import model.AuthData;
import model.GameData;
import model.JoinData;

import java.util.ArrayList;

public class GameService {
    public ArrayList<GameData> listGames(AuthData auth){
        throw new RuntimeException("Not implemented");
    }

    public GameData createGame(AuthData auth, String gameName) {
        throw new RuntimeException("Not implemented");
    }

    public String joinGame(AuthData auth, JoinData join) {
        throw new RuntimeException("Not implemented");
    }
}

package service;

import chess.ChessGame;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.AuthData;
import model.GameData;
import model.JoinData;
import model.ListGameData;

import java.util.ArrayList;

public class GameService {
    AuthDAO authDAO;
    GameDAO gameDAO;

    int nextGameID = 0;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public ListGameData listGames(String authToken){
        try {
             authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            return null;
        }
        ListGameData listGameData;
        try {
            listGameData = new ListGameData(gameDAO.listGames());
        } catch (DataAccessException e) {
            return null;
        }
        return  listGameData;
    }

    public GameData createGame(String authToken, String gameName) {
        try {
            authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            return null;
        }
        GameData gameData = new GameData(nextGameID, "", "", gameName, new ChessGame());
        nextGameID++;
        try {
            gameDAO.createGame(gameData);
        } catch (DataAccessException e) {
            return null;
        }
        return gameData;
    }

    public void joinGame(String authToken, JoinData join) {
        throw new RuntimeException("Not implemented");
    }
}

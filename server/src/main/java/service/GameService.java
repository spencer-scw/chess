package service;

import chess.ChessGame;
import dataAccess.interfaces.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.interfaces.GameDAO;
import model.AuthData;
import model.GameData;
import model.JoinData;
import model.ListGameData;

public class GameService {
    AuthDAO authDAO;
    GameDAO gameDAO;

    int nextGameID = 1000;

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
        GameData gameData = new GameData(nextGameID, null, null, gameName, new ChessGame());
        nextGameID++;
        try {
            var dbID = gameDAO.createGame(gameData);
            gameData = new GameData(dbID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), gameData.game());
        } catch (DataAccessException e) {
            return null;
        }
        return gameData;
    }

    public void joinGame(String authToken, JoinData join) throws Exception {
        AuthData auth;
        try {
            auth = authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new Exception("unauthorized");
        }
        GameData game;
        try {
            game = gameDAO.getGame(join.gameID());
        } catch (DataAccessException e) {
            throw new Exception("bad request");
        }
        GameData newGame;
        if (join.desiredColor() == ChessGame.TeamColor.WHITE) {
            if (game.whiteUsername() != null)
                throw new Exception("already taken");
            newGame = new GameData(game.gameID(), auth.username(), game.blackUsername(), game.gameName(), game.game());
        } else if (join.desiredColor() == ChessGame.TeamColor.BLACK) {
            if (game.blackUsername() != null)
                throw new Exception("already taken");
            newGame = new GameData(game.gameID(), game.whiteUsername(), auth.username(), game.gameName(), game.game());
        } else {
            System.out.println("Observing");
            return;
        }
        try {
            gameDAO.updateGame(newGame);
        } catch (DataAccessException e) {
            throw new Exception("bad request");
        }
    }
}

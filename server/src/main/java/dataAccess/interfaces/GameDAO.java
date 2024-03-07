package dataAccess.interfaces;

import dataAccess.DataAccessException;
import model.GameData;
import model.ShortGameData;

import java.util.Collection;

public interface GameDAO {
    int createGame(GameData game) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    Collection<ShortGameData> listGames() throws DataAccessException;

    void updateGame(GameData game) throws DataAccessException;

    void clearGames();
}

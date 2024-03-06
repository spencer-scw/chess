package dataAccess;

import model.GameData;
import model.ShortGameData;

import java.util.Collection;

public class DatabaseGameDAO implements GameDAO{
    @Override
    public void createGame(GameData game) throws DataAccessException {

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<ShortGameData> listGames() throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {

    }

    @Override
    public void clearGames() {

    }
}

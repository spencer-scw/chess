package dataAccess;

import model.GameData;

import java.util.Collection;
import java.util.HashSet;

public class MemoryGameDAO implements GameDAO{

    HashSet<GameData> gameDataHashSet = new HashSet<>();

    @Override
    public void createGame(GameData game) throws DataAccessException {
        for (GameData currGame : gameDataHashSet) {
            if (game.gameID() == currGame.gameID()) {
                throw new DataAccessException("Game ID already exists");
            }
        }
        gameDataHashSet.add(game);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        for (GameData game : gameDataHashSet) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        throw new DataAccessException("Game ID not found");
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return gameDataHashSet;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        for (GameData currGame : gameDataHashSet) {
            if (game.gameID() == currGame.gameID()) {
                currGame = game;
                return;
            }
        }
        throw new DataAccessException("Game doesn't exist");
    }

    @Override
    public void clearGames() {
        this.gameDataHashSet = new HashSet<>();
    }
}

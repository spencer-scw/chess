package dataAccess;

import model.GameData;
import model.ShortGameData;

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
    public Collection<ShortGameData> listGames() throws DataAccessException {
        HashSet<ShortGameData> shortGameDataHashSet = new HashSet<>();
        for (GameData game : gameDataHashSet) {
            ShortGameData shortGameData = new ShortGameData(
                    game.gameID(),
                    game.whiteUsername(),
                    game.blackUsername(),
                    game.gameName()
            );
            shortGameDataHashSet.add(shortGameData);
        }
        return shortGameDataHashSet;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        gameDataHashSet.remove(getGame(game.gameID()));
        gameDataHashSet.add(game);
    }

    @Override
    public void clearGames() {
        this.gameDataHashSet = new HashSet<>();
    }
}

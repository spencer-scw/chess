package dataAccessTests;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.DatabaseGameDAO;
import dataAccess.interfaces.GameDAO;
import model.GameData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameDAOTests {
    static GameDAO gameDAO;
    @BeforeAll
    static public void init() throws DataAccessException {
        gameDAO = new DatabaseGameDAO();
        gameDAO.clearGames();
    }

    @Test
    public void createValidGame() throws DataAccessException {
        var pailOfWater = new GameData(0, "jack", "jill", "pailOfWater", new ChessGame());
        var waterID = gameDAO.createGame(pailOfWater);
        assertEquals(pailOfWater.gameName(), gameDAO.getGame(waterID).gameName());
    }

    @Test
    public void createDuplicateGame() throws DataAccessException {
        var emptyGame = new GameData(1, null, null, null, new ChessGame());
        assertThrows(DataAccessException.class, () -> gameDAO.createGame(emptyGame));
    }

    @Test
    public void getExistingGame() throws DataAccessException {
        var showdown = new GameData(2, "carl", "darby", "game2", new ChessGame());
        int dbID = gameDAO.createGame(showdown);
        assertDoesNotThrow(() -> gameDAO.getGame(dbID));
    }

    @Test
    public void getFakeGame() {
        assertThrows(DataAccessException.class, () -> gameDAO.getGame(-1));
    }

    @Test
    public void listGames() throws DataAccessException {
        gameDAO.clearGames();
        for (int i = 0; i < 10; i++) {
            gameDAO.createGame(new GameData(10 + i, null, null, String.format("game%s",10 + i), new ChessGame()));
        }
        var returnedList = gameDAO.listGames();
        assertEquals(10, returnedList.size());
    }

    @Test
    public void listNoGames() throws DataAccessException {
        gameDAO.clearGames();
        assertEquals(0, gameDAO.listGames().size());
    }

    @Test
    public void updateGameValid() throws DataAccessException {
        int gameID = gameDAO.createGame(new GameData(30, null, null, "joinable", new ChessGame()));
        gameDAO.updateGame(new GameData(gameID, null, "Joshua", "joinable", new ChessGame()));
        var blackName = gameDAO.getGame(gameID).blackUsername();
        assertEquals(blackName, "Joshua");
    }

    @Test
    public void updateGameInvalid() throws DataAccessException {
        gameDAO.updateGame(new GameData(-1, null, null, "Fake_Game", new ChessGame()));
        assertThrows(DataAccessException.class, () -> gameDAO.getGame(-1));
    }

    @Test
    public void clearGames() throws DataAccessException {
        for (int i = 0; i < 10; i++) {
            gameDAO.createGame(new GameData(50 + i, null, null, String.format("game%s",50 + i), new ChessGame()));
        }
        gameDAO.clearGames();
        var returnedList = gameDAO.listGames();
        assertEquals(0, returnedList.size());
    }
}

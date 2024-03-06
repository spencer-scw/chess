package dataAccessTests;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.DatabaseGameDAO;
import dataAccess.GameDAO;
import model.GameData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameDAOTests {
    static GameDAO gameDAO;
    @BeforeAll
    static public void init() {
        gameDAO = new DatabaseGameDAO();
        gameDAO.clearGames();
    }

    @Test
    public void createValidGame() throws DataAccessException {
        var pailOfWater = new GameData(0, "jack", "jill", "pailOfWater", new ChessGame());
        gameDAO.createGame(pailOfWater);
        assertEquals(pailOfWater, gameDAO.getGame(pailOfWater.gameID()));
    }

    @Test
    public void createDuplicateGame() throws DataAccessException {
        var emptyGame = new GameData(1, null, null, "emptyGame", new ChessGame());
        gameDAO.createGame(emptyGame);
        assertThrows(DataAccessException.class, () -> gameDAO.createGame(emptyGame));
    }

    @Test
    public void getExistingGame() throws DataAccessException {
        var showdown = new GameData(2, "carl", "darby", "game2", new ChessGame());
        assertDoesNotThrow(() -> gameDAO.getGame(showdown.gameID()));
    }

    @Test
    public void getFakeGame() {
        assertThrows(DataAccessException.class, () -> gameDAO.getGame(-1));
    }

    @Test
    public void listGames() throws DataAccessException {
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
    public void updateGameValid() {
        
    }

    @Test
    public void updateGameInvalid() {

    }

    @Test
    public void clearGames() {

    }
}

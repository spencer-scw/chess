package serviceTests;

import chess.ChessGame;
import dataAccess.*;
import dataAccess.interfaces.AuthDAO;
import dataAccess.interfaces.GameDAO;
import dataAccess.interfaces.UserDAO;
import model.JoinData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.GameService;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {
    AuthDAO authDAO;
    GameDAO gameDAO;
    UserDAO userDAO;

    GameService gameService;
    UserService userService;

    UserData greg;

    @BeforeEach
    void setup() {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userDAO = new MemoryUserDAO();
        gameService = new GameService(authDAO, gameDAO);
        userService = new UserService(authDAO, userDAO);
        greg = new UserData("Greg", "asphodel", "greg@aloe.com");
    }

    @Test
    void listGamesPositive() {
        var authToken = userService.register(greg).authToken();
        assertNotNull(gameService.listGames(authToken));
    }

    @Test
    void listGamesNegative() {
        assertNull(gameService.listGames("FakeAuth"));
    }

    @Test
    void createGamePositive() {
        var authToken = userService.register(greg).authToken();
        assertNotNull(gameService.createGame(authToken, "Greg's game"));
    }

    @Test
    void createGameNegative() {
        assertNull(gameService.createGame("Fake Token", "Greg's game"));
    }

    @Test
    void joinGamePositive() throws Exception {
        var authToken = userService.register(greg).authToken();
        var game = gameService.createGame(authToken, "Greg's game");

        gameService.joinGame(authToken, new JoinData(ChessGame.TeamColor.WHITE, game.gameID()));
    }

    @Test
    void joinGameNegative() {
        var authToken = userService.register(greg).authToken();

        assertThrows(
                Exception.class,
                () -> {
                    gameService.joinGame(authToken, new JoinData(ChessGame.TeamColor.WHITE, 1234));
                }
        );
    }
}

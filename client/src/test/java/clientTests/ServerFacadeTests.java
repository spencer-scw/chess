package clientTests;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import server.Server;
import serverFacade.ServerFacade;
import serverFacade.websocket.ServerMessageObserver;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ServerFacadeTests {

    private static Server server;
    static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        server.clearTables();
        var port = server.run(8081);
        System.out.println("Started test HTTP server on " + port);
        try {
            serverFacade = new ServerFacade("localhost:8081", new ServerMessageObserver() {
                @Override
                public void handleLoadGame(LoadGame loadGame) {

                }

                @Override
                public void handleError(ErrorMessage error) {

                }

                @Override
                public void handleNotification(Notification notification) {

                }
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    void testInvalidLogin() throws Exception {
        assertThrows(IOException.class, () -> serverFacade.logIn(new String[]{"spencer", "wrong_pass"}));
    }

    @Test
    void testValidLogin() throws Exception {
        serverFacade.register(new String[]{"spencer", "wilson", "spencer.scw@gmail.com"});
        assertEquals("spencer", serverFacade.logIn(new String[]{"spencer", "wilson"}).get("username"));
    }

    @Test
    void testRegister() throws Exception {
        assertEquals("kaladin", serverFacade.register(new String[]{"kaladin", "stormblessed", "kal@spanreed.net"}).get("username"));
    }

    @Test
    void testUsernameTaken() throws Exception {
        serverFacade.register(new String[]{"originalGangster", "thug", "og@mail.com"});
        assertThrows(IOException.class, () -> serverFacade.register(new String[]{"original_gangster", "thug", "og@mail.com"}));
    }

    @Test
    void testLogOut() throws Exception {
        String auth = (String) serverFacade.register(new String[] {"in", "out", "c.c"}).get("authToken");
        assertDoesNotThrow(() -> serverFacade.logOut(auth));
    }

    @Test
    void testLogOutBadAuth() {
        assertThrows(IOException.class, () -> serverFacade.logOut("fake"));
    }

    @Test
    void testCreateGame() throws IOException, URISyntaxException {
        String auth = (String) serverFacade.register(new String[] {"game", "creator", "test@example.com"}).get("authToken");
        assertDoesNotThrow(() -> serverFacade.createGame(new String[] {"newGame"}, auth));
    }

    @Test
    void testInvalidCreateGame() {
        assertThrows(IOException.class, () -> serverFacade.createGame(new String[] {"newGame"}, "fake"));
    }

    @Test
    void testListGames() throws IOException, URISyntaxException {
        String auth = (String) serverFacade.register(new String[] {"lister", "gaming", "test@example.com"}).get("authToken");
        serverFacade.createGame(new String[] {"listableGame"}, auth);
        ArrayList gameList = (ArrayList) serverFacade.listGames(auth).get("games");
        assertFalse(gameList.isEmpty());
    }

    @Test
    void listNoGames() throws IOException, URISyntaxException {
        server.clearTables();
        String auth = (String) serverFacade.register(new String[] {"blank", "lister", "test@example.com"}).get("authToken");
        ArrayList gameList = (ArrayList) serverFacade.listGames(auth).get("games");
        assertTrue(gameList.isEmpty());
    }

    @Test
    void testJoinGameBothColors() throws IOException, URISyntaxException {
        String auth = (String) serverFacade.register(new String[] {"player", "gaming", "test@example.com"}).get("authToken");
        String gameID = serverFacade.createGame(new String[] {"joinGame"}, auth).get("gameID").toString();
        assertDoesNotThrow(() -> serverFacade.joinGame(new String[]{"white", gameID}, auth));
        assertDoesNotThrow(() -> serverFacade.joinGame(new String[]{"black", gameID}, auth));
    }

    @Test
    void testObserveGame() throws IOException, URISyntaxException {
        String auth = (String) serverFacade.register(new String[] {"observer", "gaming", "test@example.com"}).get("authToken");
        String gameID = serverFacade.createGame(new String[] {"joinGame"}, auth).get("gameID").toString();
        assertDoesNotThrow(() -> serverFacade.joinGame(new String[]{gameID}, auth));
    }

    @Test
    void testJoinInvalidGame() throws IOException, URISyntaxException {
        String auth = (String) serverFacade.register(new String[] {"delusional", "gaming", "test@example.com"}).get("authToken");
        String gameID = "0.0";
        assertThrows(IOException.class, () -> serverFacade.joinGame(new String[]{gameID}, auth));
    }

}

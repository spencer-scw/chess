package clientTests;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import server.Server;
import ui.ChessClient;
import ui.ServerFacade;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

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
        serverFacade = new ServerFacade("localhost:8081");
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

}

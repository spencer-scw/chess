package clientTests;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import server.Server;
import ui.ChessClient;
import ui.ServerFacade;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ServerFacadeTests {

    private static Server server;
    static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8081);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("localhost:8081");
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    void testInvalidLogin() {
        assertEquals("Incorrect username or password. Please try again.", serverFacade.logIn(new String[]{"spencer", "wilson"}));
    }

}

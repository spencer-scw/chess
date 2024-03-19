package clientTests;

import org.junit.jupiter.api.*;
import server.Server;
import ui.ChessClient;


public class ServerFacadeTests {

    private static Server server;
    static ChessClient chessClient;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        chessClient = new ChessClient("localhost:0")
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void testLogin() {
        chessClient.
    }

}

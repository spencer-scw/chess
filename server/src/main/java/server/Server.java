package server;

import com.google.gson.Gson;
import dataAccess.*;
import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.util.ajax.JSON;
import service.GameService;
import service.UserService;
import service.UtilService;
import spark.*;

import java.util.Map;

public class Server {
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private UserDAO userDAO;

    private GameService gameService;
    private final UserService userService;
    private final UtilService utilService;

    private final GameHandler gameHandler;

    private final SessionHandler sessionHandler;

    private final UserHandler userHandler;

    public Server() {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userDAO = new MemoryUserDAO();

        gameService = new GameService();
        userService = new UserService(authDAO, userDAO);
        utilService = new UtilService(authDAO, gameDAO, userDAO);

        gameHandler = new GameHandler();
        sessionHandler = new SessionHandler();
        userHandler = new UserHandler(userService);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        Spark.delete("/db", this::clear);
        Spark.post("/user", userHandler::register);

        Spark.exception(Exception.class, userHandler::errorHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object clear(Request req, Response res){
        System.out.println("Clearing!");
        utilService.clear();
        res.status(200);
        res.type("application/json");
        return "null";
    }
}

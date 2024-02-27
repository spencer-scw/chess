package server;

import dataAccess.*;
import service.GameService;
import service.UserService;
import service.UtilService;
import spark.Request;
import spark.Response;
import spark.Spark;

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

        gameService = new GameService(authDAO, gameDAO);
        userService = new UserService(authDAO, userDAO);
        utilService = new UtilService(authDAO, gameDAO, userDAO);

        gameHandler = new GameHandler(gameService);
        sessionHandler = new SessionHandler(userService);
        userHandler = new UserHandler(userService);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        Spark.delete("/db", this::clear);
        Spark.post("/user", userHandler::register);
        Spark.post("/session", sessionHandler::login);
        Spark.delete("/session", sessionHandler::logout);
        Spark.get("/game", gameHandler::list);
        Spark.post("/game", gameHandler::create);
        Spark.put("/game", gameHandler::join);

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

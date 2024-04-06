package server;

import dataAccess.*;
import dataAccess.interfaces.AuthDAO;
import dataAccess.interfaces.GameDAO;
import dataAccess.interfaces.UserDAO;
import service.GameService;
import service.UserService;
import service.UtilService;
import spark.Request;
import spark.Response;
import spark.Spark;

import server.websocket.WebSocketHandler;

public class Server {

    private final UtilService utilService;
    private final GameHandler gameHandler;
    private final SessionHandler sessionHandler;
    private final UserHandler userHandler;
    private final WebSocketHandler webSocketHandler;

    public Server() {
        AuthDAO authDAO = new DatabaseAuthDAO();
        GameDAO gameDAO = new DatabaseGameDAO();
        UserDAO userDAO = new DatabaseUserDAO();

        GameService gameService = new GameService(authDAO, gameDAO);
        UserService userService = new UserService(authDAO, userDAO);
        utilService = new UtilService(authDAO, gameDAO, userDAO);

        gameHandler = new GameHandler(gameService);
        sessionHandler = new SessionHandler(userService);
        userHandler = new UserHandler(userService);

        webSocketHandler = new WebSocketHandler(authDAO);
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

        Spark.webSocket("/connect", webSocketHandler);

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
        return "{}";
    }

    public void clearTables() {
        utilService.clear();
    }
}

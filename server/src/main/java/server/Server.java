package server;

import com.google.gson.Gson;
import dataAccess.*;
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
    private UserService userService;
    private final UtilService utilService;

    public Server() {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userDAO = new MemoryUserDAO();

        gameService = new GameService();
        userService = new UserService();
        utilService = new UtilService(authDAO, gameDAO, userDAO);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        Spark.delete("/db", this::clear);

        Spark.exception(Exception.class, this::errorHandler);
        Spark.notFound((req, res) -> {
            var msg = String.format("[%s] %s not found", req.requestMethod(), req.pathInfo());
            return errorHandler(new Exception(msg), req, res);
        });

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

    public Object errorHandler(Exception e, Request req, Response res) {
        var body = new Gson().toJson(Map.of("message",  String.format("Error: %s", e.getMessage())));
        res.type("application/json");
        res.status(500);
        res.body(body);
        return body;
    }
}

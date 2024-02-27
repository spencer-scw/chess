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
    private UserService userService;
    private final UtilService utilService;

    public Server() {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userDAO = new MemoryUserDAO();

        gameService = new GameService();
        userService = new UserService(authDAO, userDAO);
        utilService = new UtilService(authDAO, gameDAO, userDAO);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);

        Spark.exception(Exception.class, this::errorHandler);

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

    private Object register(Request req, Response res) {
        System.out.println("Registering!");

        var bodyObj = getBody(req, Map.class);

        if (bodyObj.size() != 3 ) {
            return errorHandler(new Exception("bad request"), req, res);
        }

        UserData user = new UserData(
                (String) bodyObj.get("username"),
                (String) bodyObj.get("password"),
                (String) bodyObj.get("email")
        );

        res.status(200);
        res.type("application/json");

        AuthData auth = userService.register(user);

        if (auth == null) {
            return errorHandler(new Exception("already taken"), req, res);
        }

        return new Gson().toJson(auth);
    }

    public Object errorHandler(Exception e, Request req, Response res) {
        var body = new Gson().toJson(Map.of("message",  String.format("Error: %s", e.getMessage())));
        res.type("application/json");

        int code = switch (e.getMessage()) {
            case "bad request" -> 400;
            case "unauthorized" -> 401;
            case "already taken" -> 403;
            default -> 500;
        };

        res.status(code);
        res.body(body);
        return body;
    }

    private static <T> T getBody(Request request, Class<T> clazz) {
        var body = new Gson().fromJson(request.body(), clazz);
        if (body == null) {
            throw new RuntimeException("missing required body");
        }
        return body;
    }
}

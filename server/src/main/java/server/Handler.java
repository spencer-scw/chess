package server;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;

import java.util.Map;

public abstract class Handler {
    protected Object errorHandler(Exception e, Request req, Response res) {
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

    protected static <T> T getBody(Request request, Class<T> clazz) {
        var body = new Gson().fromJson(request.body(), clazz);
        if (body == null) {
            throw new RuntimeException("missing required body");
        }
        return body;
    }
}

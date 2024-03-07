package server;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.Map;

public class SessionHandler extends Handler
{   UserService userService;
    public SessionHandler(UserService userService) {
        this.userService = userService;
    }

    public Object login(Request req, Response res) {
        System.out.println("logging in");

        var bodyObj = getBody(req, Map.class);

        UserData user = new UserData(
                (String) bodyObj.get("username"),
                (String) bodyObj.get("password"),
                null
        );

        AuthData auth = userService.login(user);

        if (auth == null) {
            return errorHandler(new Exception("unauthorized"), req, res);
        }

        res.status(200);
        res.type("application/json");
        return new Gson().toJson(auth);
    }

    public Object logout(Request req, Response res) {
        System.out.println("logging out");
        var authToken = req.headers("authorization");

        if (userService.logout(authToken)) {
            res.status(200);
            res.type("application/json");
            return "{}";
        }
        return errorHandler(new Exception("unauthorized"), req, res);
    }
}

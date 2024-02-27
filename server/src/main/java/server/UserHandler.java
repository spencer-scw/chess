package server;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.Map;

public class UserHandler extends Handler {
    UserService userService;
    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public Object register(Request req, Response res) {
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
}

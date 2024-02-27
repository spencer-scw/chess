package server;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.JoinData;
import model.ListGameData;
import model.UserData;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class GameHandler extends Handler {
    GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object list(Request req, Response res) {
        System.out.println("listing");

        var authToken = req.headers("authorization");

        ListGameData listGameData = gameService.listGames(authToken);

        if (listGameData == null) {
            return errorHandler(new Exception("unauthorized"), req, res);
        }
        res.status(200);
        res.type("application/json");
        return new Gson().toJson(listGameData);
    }

    public Object create(Request req, Response res) {
        System.out.println("creating");

        var authToken = req.headers("authorization");
        String gameName;
        try {
            gameName = (String) getBody(req, Map.class).get("gameName");
        } catch (Exception e) {
            return errorHandler(new Exception("bad request"), req, res);
        }

        GameData gameData = gameService.createGame(authToken, gameName);

        if (gameData == null) {
            return errorHandler(new Exception("unauthorized"), req, res);
        }

        res.status(200);
        res.type("application/json");
        return String.format("{\"gameID\": %s}", gameData.gameID());
    }

    public Object join(Request req, Response res) {
        System.out.println("joining");

        var bodyObj = getBody(req, Map.class);
        JoinData join;

        try {
            join = new JoinData(
                    (bodyObj.containsKey("playerColor")) ? ChessGame.TeamColor.valueOf((String) bodyObj.get("playerColor")) : null,
                    (int) Math.round((Double) bodyObj.get("gameID"))
            );
        } catch (Exception e) {
            return errorHandler(new Exception("bad request"), req, res);
        }

        var authToken = req.headers("authorization");

        try {
            gameService.joinGame(authToken, join);
        } catch (Exception e) {
            return errorHandler(e, req, res);
        }
        res.status(200);
        res.type("application/json");
        return "{}";
    }
}

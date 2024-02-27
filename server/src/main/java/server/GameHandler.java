package server;

import service.GameService;
import spark.Request;
import spark.Response;

public class GameHandler extends Handler {
    GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object list(Request req, Response res) {
        System.out.println("listing");
        return 0;
    }

    public Object create(Request req, Response res) {
        System.out.println("creating");
        return 0;
    }

    public Object join(Request req, Response res) {
        System.out.println("joining");
        return 0;
    }
}

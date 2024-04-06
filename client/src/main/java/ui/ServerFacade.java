package ui;

import ui.http.HttpCommunicator;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class ServerFacade {
    private final HttpCommunicator httpCommunicator;

    public ServerFacade(String serverURL) {
        httpCommunicator = new HttpCommunicator(serverURL);
    }

    public Map logIn(String[] params) throws Exception {
        return httpCommunicator.handleHTTP(
                "POST",
                "session",
                "",
                Map.of("username", params[0], "password", params[1])
        );

    }

    public Map register(String[] params) throws IOException, URISyntaxException {
        return httpCommunicator.handleHTTP(
                "POST",
                "user",
                "",
                Map.of("username", params[0], "password", params[1], "email", params[2])
        );

    }

    public void logOut(String authToken) throws IOException, URISyntaxException {
        httpCommunicator.handleHTTP(
                "DELETE",
                "session",
                authToken,
                null
        );
    }

    public Map createGame(String[] params, String authToken) throws IOException, URISyntaxException {
        return httpCommunicator.handleHTTP(
                "POST",
                "game",
                authToken,
                Map.of("gameName", params[0])
        );
    }

    public Map listGames(String authToken) throws IOException, URISyntaxException {
        return httpCommunicator.handleHTTP(
                "GET",
                "game",
                authToken,
                null
        );
    }

    public void joinGame(String[] params, String authToken) throws IOException, URISyntaxException {
        if (params.length >= 2) {
            httpCommunicator.handleHTTP(
                    "PUT",
                    "game",
                    authToken,
                    Map.of("playerColor", params[0].toUpperCase(), "gameID", Double.parseDouble(params[1]))
            );
        } else {
            httpCommunicator.handleHTTP(
                    "PUT",
                    "game",
                    authToken,
                    Map.of("gameID", Double.parseDouble(params[0]))
            );
        }
    }


}

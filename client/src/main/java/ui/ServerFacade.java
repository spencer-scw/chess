package ui;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class ServerFacade {
    private final String serverURL;

    public ServerFacade(String serverURL) {
        this.serverURL = serverURL;
    }

    private Map handleHTTP(String method, String endpoint, String authToken, Map body) throws IOException, URISyntaxException {
        URI uri = new URI(String.format("http://%s/%s", serverURL, endpoint));
        var http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod(method);
        http.addRequestProperty("Content-Type", "application/json");
        if (!authToken.isEmpty()) {
            http.addRequestProperty("authorization", authToken);
        }

        if (body != null) {
            http.setDoOutput(true);
            try (var outputStream = http.getOutputStream()) {
                var jsonBody = new Gson().toJson(body);
                outputStream.write(jsonBody.getBytes());
            }
        }

        http.connect();

        try (InputStream responseBody = http.getInputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(responseBody);
            return new Gson().fromJson(inputStreamReader, Map.class);
        }
    }

    public Map logIn(String[] params) throws Exception {
        return this.handleHTTP(
               "POST",
               "session",
               "",
                Map.of("username", params[0], "password", params[1])
        );

    }

    public Map register(String[] params) throws IOException, URISyntaxException {
        return this.handleHTTP(
                "POST",
                "user",
                "",
                Map.of("username", params[0], "password", params[1], "email", params[2])
        );

    }

    public void logOut(String authToken) throws IOException, URISyntaxException {
        this.handleHTTP(
                "DELETE",
                "session",
                 authToken,
                null
        );
    }

    public Map createGame(String[] params, String authToken) throws IOException, URISyntaxException {
        return this.handleHTTP(
                "POST",
                "game",
                 authToken,
                 Map.of("gameName", params[0])
        );
    }

    public Map listGames(String authToken) throws IOException, URISyntaxException {
        return this.handleHTTP(
                "GET",
                "game",
                authToken,
                null
        );
    }

    public void joinGame(String[] params, String authToken) throws IOException, URISyntaxException {
        if (params.length >= 2) {
            this.handleHTTP(
                    "PUT",
                    "game",
                    authToken,
                    Map.of("playerColor", params[0].toUpperCase(), "gameID", Double.parseDouble(params[1]))
            );
        } else {
            this.handleHTTP(
                    "PUT",
                    "game",
                    authToken,
                    Map.of("gameID", Double.parseDouble(params[0]))
            );
        }
    }
}

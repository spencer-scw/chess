package ui;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
        http.setDoOutput(true);
        http.addRequestProperty("Content-Type", "application/json");
        if (!authToken.isEmpty()) {
            http.addRequestProperty("authorization", authToken);
        }
        try (var outputStream = http.getOutputStream()) {
            var jsonBody = new Gson().toJson(body);
            outputStream.write(jsonBody.getBytes());
        }

        http.connect();

        try (InputStream responseBody = http.getInputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(responseBody);
            return new Gson().fromJson(inputStreamReader, Map.class);
        }
    }

    public String logIn(String[] params) {
        HttpURLConnection http;
        try {
            return this.handleHTTP(
                   "POST",
                   "session",
                   "",
                    Map.of("username", params[0], "password", params[1])
            ).toString();
        } catch (Exception e) {
            if (e.getClass() != IOException.class) {
                return "bad URL";
            } else {
                return "Incorrect username or password. Please try again.";
            }
        }

    }
    public String observeGame(String[] params) {
        return "";
    }

    public String joinGame(String[] params) {
        return "";
    }

    public String listGames() {
        return "";
    }

    public String createGame(String[] params) {
        return "";
    }

    public String logOut(String[] params) {
        return "";
    }

    public String register(String[] params) {
        return null;
    }


}

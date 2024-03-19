package ui;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.util.Arrays;
import java.util.Map;

public class ChessClient {
    private final String serverURL;
    private State clientState;

    public ChessClient(String serverURL) {
        this.serverURL = String.format("http://%s", serverURL);

        clientState = State.SIGNEDOUT;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> logIn(params);
                case "register" -> register(params);

                case "logout" -> logOut(params);
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);

                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String help() {
        if (clientState == State.SIGNEDOUT) {
            return String.format("""
                   - %s login %s <username> <password> - to log in
                   - %s register %s <username> <password> <email> - to create an account
                   - %s help %s (or any invalid command) - to see this list
                   - %s quit %s
                    """,EscapeSequences.SET_TEXT_BOLD, EscapeSequences.RESET_TEXT_BOLD_FAINT,
                        EscapeSequences.SET_TEXT_BOLD, EscapeSequences.RESET_TEXT_BOLD_FAINT,
                        EscapeSequences.SET_TEXT_BOLD, EscapeSequences.RESET_TEXT_BOLD_FAINT,
                        EscapeSequences.SET_TEXT_BOLD, EscapeSequences.RESET_TEXT_BOLD_FAINT);
        } else {
            return String.format("""
                    - %s logout %s - to log out
                    - %s list %s - to see available games
                    - %s create %s <game name> - to create a new game
                    - %s join %s <game name> - to join a game
                    - %s observe %s <game name> - to observe a game
                    """,EscapeSequences.SET_TEXT_BOLD, EscapeSequences.RESET_TEXT_BOLD_FAINT,
                        EscapeSequences.SET_TEXT_BOLD, EscapeSequences.RESET_TEXT_BOLD_FAINT,
                        EscapeSequences.SET_TEXT_BOLD, EscapeSequences.RESET_TEXT_BOLD_FAINT,
                        EscapeSequences.SET_TEXT_BOLD, EscapeSequences.RESET_TEXT_BOLD_FAINT,
                        EscapeSequences.SET_TEXT_BOLD, EscapeSequences.RESET_TEXT_BOLD_FAINT);
        }
    }

    private String observeGame(String[] params) {
        return "";
    }

    private String joinGame(String[] params) {
        return "";
    }

    private String listGames() {
        return "";
    }

    private String createGame(String[] params) {
        return "";
    }

    private String logOut(String[] params) {
        return "";
    }

    private String register(String[] params) {
        return null;
    }

    private String logIn(String[] params) {
        HttpURLConnection http;
        try {
            URI uri = new URI(String.format("%s/session", serverURL));
            http = (HttpURLConnection) uri.toURL().openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Content-Type", "application/json");
            var body = Map.of("username", params[0], "password", params[1]);
            try (var outputStream = http.getOutputStream()) {
                var jsonBody = new Gson().toJson(body);
                outputStream.write(jsonBody.getBytes());
            }

            http.connect();

            try (InputStream responseBody = http.getInputStream()) {
                InputStreamReader inputStreamReader = new InputStreamReader(responseBody);
                var output = new Gson().fromJson(inputStreamReader, Map.class);
                return output.toString();
            }

        } catch (Exception e) {
            if (e.getClass() != IOException.class) {
                return "bad URL";
            } else {
                return "Incorrect username or password. Please try again.";
            }
        }

    }
}

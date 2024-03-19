package ui;

import model.AuthData;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class ChessClient {
    private final ServerFacade serverFacade;
    private State clientState;
    private String authToken;

    public ChessClient(String serverURL) {
        this.serverFacade = new ServerFacade(serverURL);

        clientState = State.SIGNEDOUT;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (clientState == State.SIGNEDOUT) {
                return switch (cmd) {
                    case "login" -> logIn(params);
                    case "register" -> register(params);
                    case "quit" -> "quit";
                    default -> help();
                };
            } else {
                return switch (cmd) {

                    case "logout" -> serverFacade.logOut(params);
                    case "create" -> serverFacade.createGame(params);
                    case "list" -> serverFacade.listGames();
                    case "join" -> serverFacade.joinGame(params);
                    case "observe" -> serverFacade.observeGame(params);

                    case "quit" -> "quit";
                    default -> help();
                };
            }
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }


    private String logIn(String[] params) {
        Map result;
        try {
            result = serverFacade.logIn(params);
            authToken = (String) result.get("authToken");
            clientState = State.SIGNEDIN;
            return String.format("Signed in successfully as %s", result.get("username"));
        } catch (Exception e) {
            if (e.getClass() != IOException.class) {
                return e.getMessage();
            } else {
                return "Incorrect username or password. Please try again.";
            }
        }
    }

    private String register(String[] params) {
        Map result;
        try {
            result = serverFacade.register(params);
            authToken = (String) result.get("authToken");
            clientState = State.SIGNEDIN;
            return String.format("Successfully registered %s and signed in automatically.", result.get("username"));
        } catch (Exception e) {
            if (e.getClass() != IOException.class) {
                return "bad URL";
            } else if (e.getMessage().contains("403")) {
                return "Sorry, that username is already taken. Try another.";
            } else {
                return e.getMessage();
            }
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
}

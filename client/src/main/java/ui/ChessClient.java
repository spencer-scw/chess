package ui;

import java.util.Arrays;

public class ChessClient {
    private final String serverURL;
    private State clientState;

    public ChessClient(String serverURL) {
        this.serverURL = serverURL;
        clientState = State.SIGNEDOUT;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> signIn(params);
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
    }

    private String joinGame(String[] params) {
    }

    private String listGames() {
    }

    private String createGame(String[] params) {
    }

    private String logOut(String[] params) {
    }

    private String register(String[] params) {
        return null;
    }

    private String signIn(String[] params) {
        return null;
    }
}

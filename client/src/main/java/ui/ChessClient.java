package ui;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import model.AuthData;
import model.GameData;

import java.io.IOException;
import java.util.*;

public class ChessClient {
    private final ServerFacade serverFacade;
    private State clientState;
    private String authToken;

    private ArrayList<Double> lastListOrder;

    public ChessClient(String serverURL) {
        this.serverFacade = new ServerFacade(serverURL);

        lastListOrder = new ArrayList<>();

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

                    case "logout" -> logOut();
                    case "create" -> createGame(params);
                    case "list" -> listGames();
                    case "join" -> joinGame(params);
                    case "observe" -> observeGame(params);

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

    private String logOut() {
        try {
            serverFacade.logOut(authToken);
            clientState = State.SIGNEDOUT;
            authToken = "";
            return "Logout successful.";
        } catch (Exception e) {
            return "Logout failed.";
        }
    }

    private String createGame(String[] params) {
        try {
            return String.format("Created game %s with id %d", params[0], Math.round((Double) serverFacade.createGame(params, authToken).get("gameID")));
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String listGames() {
        StringBuilder result = new StringBuilder();
        result.append(String.format("ID   | gameName        | whitePlayer     | blackPlayer   %n"));
        result.append(String.format("-----+-----------------+-----------------+---------------%n"));

        try {
            ArrayList games = (ArrayList) serverFacade.listGames(authToken).get("games");
            lastListOrder = new ArrayList<>();
            for (var game: games) {
                var gameID = ((LinkedTreeMap<?, ?>) game).get("gameID");
                lastListOrder.add((Double) gameID);

                var gameName = ((LinkedTreeMap<?, ?>) game).get("gameName");
                var whiteUsername = ((LinkedTreeMap<?, ?>) game).get("whiteUsername");
                if (whiteUsername == null) {
                    whiteUsername = "-";
                }
                var blackUsername = ((LinkedTreeMap<?, ?>) game).get("blackUsername");
                if (blackUsername == null) {
                    blackUsername = "-";
                }
                result.append(String.format(
                        " %-3d | %-15s | %-15s | %-15s %n",
                        lastListOrder.size() - 1,
                        gameName,
                        whiteUsername,
                        blackUsername
                ));
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return result.toString();
    }

    private String joinGame(String[] params) {
        try {
            if (params.length >= 2) {
                serverFacade.joinGame(new String[]{params[1], lastListOrder.get(Integer.parseInt(params[0])).toString()}, authToken);
            } else {
                String assignedColor = "";
                String gameID = "";
                ArrayList games = (ArrayList) serverFacade.listGames(authToken).get("games");
                for (var game: games) {
                    gameID = ((Double) ((LinkedTreeMap<?, ?>) game).get("gameID")).toString();
                    var whiteUsername = ((LinkedTreeMap<?, ?>) game).get("whiteUsername");
                    var blackUsername = ((LinkedTreeMap<?, ?>) game).get("blackUsername");
                    if (Objects.equals(gameID, lastListOrder.get(Integer.parseInt(params[0])).toString())) {
                        if (whiteUsername == null && blackUsername == null) {
                            if (Math.random() > .5) {
                                assignedColor = "WHITE";
                            } else {
                                assignedColor = "BLACK";
                            }
                        } else if (blackUsername == null) {
                            assignedColor = "BLACK";
                        } else if (whiteUsername == null) {
                            assignedColor = "WHITE";
                        } else {
                            return "Desired game is full. Please observe this game or join another.";
                        }
                        serverFacade.joinGame(new String[]{assignedColor, gameID}, authToken);
                    }

                }
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        var blankGame = new ChessBoard();
        blankGame.resetBoard();
        return BoardPrinter.printBoard(blankGame, ChessGame.TeamColor.WHITE) +
                String.format("%n") +
                BoardPrinter.printBoard(blankGame, ChessGame.TeamColor.BLACK);
    }

    private String observeGame(String[] params) {
        try {
            serverFacade.joinGame(new String[]{null, lastListOrder.get(Integer.parseInt(params[0])).toString()}, authToken);
            var blankGame = new ChessBoard();
            blankGame.resetBoard();
            return BoardPrinter.printBoard(blankGame, ChessGame.TeamColor.WHITE) +
                    String.format("%n") +
                    BoardPrinter.printBoard(blankGame, ChessGame.TeamColor.BLACK);
        } catch (Exception e) {
            return e.getMessage();
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
                    - %s join %s <game name> [WHITE | BLACK | <empty> ] - to join a game. If no color is specified, will auto-assign.
                    - %s observe %s <game name> - to observe a game
                    """,EscapeSequences.SET_TEXT_BOLD, EscapeSequences.RESET_TEXT_BOLD_FAINT,
                        EscapeSequences.SET_TEXT_BOLD, EscapeSequences.RESET_TEXT_BOLD_FAINT,
                        EscapeSequences.SET_TEXT_BOLD, EscapeSequences.RESET_TEXT_BOLD_FAINT,
                        EscapeSequences.SET_TEXT_BOLD, EscapeSequences.RESET_TEXT_BOLD_FAINT,
                        EscapeSequences.SET_TEXT_BOLD, EscapeSequences.RESET_TEXT_BOLD_FAINT);
        }
    }
}

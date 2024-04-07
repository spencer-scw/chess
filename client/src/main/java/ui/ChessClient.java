package ui;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.internal.LinkedTreeMap;
import serverFacade.ServerFacade;
import serverFacade.websocket.ServerMessageObserver;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class ChessClient implements ServerMessageObserver {
    private final SignedOutHandler signedOutHandler;
    private final SignedInHandler signedInHandler;
    private final InGameHandler inGameHandler;
    private final SessionInfo sessionInfo;

    public ChessClient(String serverURL) throws DeploymentException, URISyntaxException, IOException {
        ServerFacade serverFacade = new ServerFacade(serverURL, this);
        sessionInfo = new SessionInfo("", State.SIGNEDOUT, new ChessBoard(), ChessGame.TeamColor.WHITE);
        sessionInfo.getBoard().resetBoard();
        signedOutHandler = new SignedOutHandler(serverFacade, sessionInfo);
        signedInHandler = new SignedInHandler(serverFacade, sessionInfo);
        inGameHandler = new InGameHandler(serverFacade, sessionInfo);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (sessionInfo.getClientState() == State.SIGNEDOUT) {
                return switch (cmd) {
                    case "login" -> signedOutHandler.logIn(params);
                    case "register" -> signedOutHandler.register(params);
                    case "quit" -> "quit";
                    default -> help();
                };
            } else if (sessionInfo.getClientState() == State.SIGNEDIN){
                return switch (cmd) {

                    case "logout" -> signedInHandler.logOut();
                    case "create" -> signedInHandler.createGame(params);
                    case "list" -> signedInHandler.listGames();
                    case "join" -> signedInHandler.joinGame(params);
                    case "observe" -> signedInHandler.observeGame(params);

                    case "quit" -> "quit";
                    default -> help();
                };
            } else {
                return switch (cmd) {
                    case "redraw" -> inGameHandler.redraw();
                    case "move" -> null;
                    case "leave" -> null;
                    case "resign" -> null;
                    case "highlight" -> inGameHandler.highlight(params);

                    default -> help();
                };
            }
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String help() {
        if (sessionInfo.getClientState() == State.SIGNEDOUT) {
            return String.format("""
                   - %s login %s <username> <password> - to log in.
                   - %s register %s <username> <password> <email> - to create an account.
                   - %s help %s (or any invalid command) - to see this list.
                   - %s quit %s
                    """,EscapeSequences.SET_TEXT_BOLD, EscapeSequences.RESET_TEXT_BOLD_FAINT,
                        EscapeSequences.SET_TEXT_BOLD, EscapeSequences.RESET_TEXT_BOLD_FAINT,
                        EscapeSequences.SET_TEXT_BOLD, EscapeSequences.RESET_TEXT_BOLD_FAINT,
                        EscapeSequences.SET_TEXT_BOLD, EscapeSequences.RESET_TEXT_BOLD_FAINT);
        } else if (sessionInfo.getClientState() == State.SIGNEDIN) {
            return String.format("""
                    - %s logout %s - to log out.
                    - %s list %s - to see available games.
                    - %s create %s <game name> - to create a new game.
                    - %s join %s <game name> [WHITE | BLACK | <empty> ] - to join a game. If no color is specified, will auto-assign.
                    - %s observe %s <game name> - to observe a game.
                    - %s help %s (or any invalid command) - to see this list.
                    """,EscapeSequences.SET_TEXT_BOLD, EscapeSequences.RESET_TEXT_BOLD_FAINT,
                        EscapeSequences.SET_TEXT_BOLD, EscapeSequences.RESET_TEXT_BOLD_FAINT,
                        EscapeSequences.SET_TEXT_BOLD, EscapeSequences.RESET_TEXT_BOLD_FAINT,
                        EscapeSequences.SET_TEXT_BOLD, EscapeSequences.RESET_TEXT_BOLD_FAINT,
                        EscapeSequences.SET_TEXT_BOLD, EscapeSequences.RESET_TEXT_BOLD_FAINT,
                        EscapeSequences.SET_TEXT_BOLD, EscapeSequences.RESET_TEXT_BOLD_FAINT);
        } else {
            return String.format("""
                    - %s move <origin> <destination> %s - moves piece from <origin> to <destination> if it is your turn and the move is valid.
                    - %s redraw %s - redraws the board.
                    - %s leave %s  - to leave the game. This does not resign the game.
                    - %s resign %s - to resign the game. This does not leave the game session.
                    - %s highlight <position> %s - shows all possible moves for a piece at <position>.
                    - %s help %s (or any invalid command) - to see this list.
                    """,EscapeSequences.SET_TEXT_BOLD, EscapeSequences.RESET_TEXT_BOLD_FAINT,
                        EscapeSequences.SET_TEXT_BOLD, EscapeSequences.RESET_TEXT_BOLD_FAINT,
                        EscapeSequences.SET_TEXT_BOLD, EscapeSequences.RESET_TEXT_BOLD_FAINT,
                        EscapeSequences.SET_TEXT_BOLD, EscapeSequences.RESET_TEXT_BOLD_FAINT,
                        EscapeSequences.SET_TEXT_BOLD, EscapeSequences.RESET_TEXT_BOLD_FAINT,
                        EscapeSequences.SET_TEXT_BOLD, EscapeSequences.RESET_TEXT_BOLD_FAINT);
        }
    }

    // Incoming websocket messages handled below

    @Override
    public void handleLoadGame(LoadGame loadGame) {

    }

    @Override
    public void handleError(ErrorMessage error) {

    }

    @Override
    public void handleNotification(Notification notification) {

    }
}

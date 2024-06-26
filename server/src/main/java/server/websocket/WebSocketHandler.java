package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.interfaces.AuthDAO;
import dataAccess.interfaces.GameDAO;
import dataAccess.interfaces.UserDAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.GameService;
import service.UserService;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinPlayer;
import webSocketMessages.userCommands.MakeMove;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;
import java.util.*;


@WebSocket
public class WebSocketHandler {

    private HashMap<Integer, ConnectionManager>  gameConnectionManagers = new HashMap<>();
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {

        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer(userGameCommand.getAuthString(), session, userGameCommand.getGameID(), new Gson().fromJson(message, JoinPlayer.class).getPlayerColor());
            case JOIN_OBSERVER -> joinObserver(userGameCommand.getAuthString(), session, userGameCommand.getGameID());
            case MAKE_MOVE -> makeMove(userGameCommand.getAuthString(), userGameCommand.getGameID(), new Gson().fromJson(message, MakeMove.class).getMove());
            case LEAVE -> leave(userGameCommand.getAuthString(), userGameCommand.getGameID());
            case RESIGN -> resign(userGameCommand.getAuthString(), userGameCommand.getGameID());
        }
    }
    private void joinPlayer(String authString, Session session, Integer gameID, ChessGame.TeamColor teamColor) throws IOException, DataAccessException {
        if (!gameConnectionManagers.containsKey(gameID)) {
            gameConnectionManagers.put(gameID, new ConnectionManager());
        }
        gameConnectionManagers.get(gameID).add(authString, session);
        String username;
        try {
            username = authDAO.getAuth(authString).username();
        } catch (DataAccessException e) {
            gameConnectionManagers.get(gameID).send(authString, new ErrorMessage("Unauthorized to join."));
            return;
        }
        LoadGame loadGame;
        try {
            loadGame = new LoadGame(gameDAO.getGame(gameID));
        } catch (DataAccessException e) {
            gameConnectionManagers.get(gameID).send(authString, new ErrorMessage("Game does not exist."));
            return;
        }
        if (teamColor == ChessGame.TeamColor.BLACK && !Objects.equals(loadGame.getGame().blackUsername(), username) ||
            teamColor == ChessGame.TeamColor.WHITE && !Objects.equals(loadGame.getGame().whiteUsername(), username)) {
            gameConnectionManagers.get(gameID).send(authString, new ErrorMessage("Attempted to join wrong team."));
            return;
        }
        gameConnectionManagers.get(gameID).send(authString, loadGame);

        var joinMessage = String.format("%s joined the game.", username);
        var notification = new Notification(joinMessage);
        gameConnectionManagers.get(gameID).broadcast(authString, notification);
    }

    private void joinObserver(String authString, Session session, Integer gameID) throws DataAccessException, IOException {
        if (!gameConnectionManagers.containsKey(gameID)) {
            gameConnectionManagers.put(gameID, new ConnectionManager());
        }
        gameConnectionManagers.get(gameID).add(authString, session);
        String username;
        try {
            username = authDAO.getAuth(authString).username();
        } catch (DataAccessException e) {
            gameConnectionManagers.get(gameID).send(authString, new ErrorMessage("Unauthorized to observe."));
            return;
        }

        LoadGame loadGame;
        try {
            loadGame = new LoadGame(gameDAO.getGame(gameID));
        } catch (DataAccessException e) {
            gameConnectionManagers.get(gameID).send(authString, new ErrorMessage("Game does not exist."));
            return;
        }

        gameConnectionManagers.get(gameID).send(authString, loadGame);

        var joinMessage = String.format("%s is now observing this game. Play well!", username);
        var notification = new Notification(joinMessage);
        gameConnectionManagers.get(gameID).broadcast(authString, notification);
    }

    private void makeMove(String authString, Integer gameID, ChessMove chessMove) throws DataAccessException, IOException {
        String username = authDAO.getAuth(authString).username();
        GameData game = gameDAO.getGame(gameID);

        if (game.game().getGameOver()) {
            gameConnectionManagers.get(gameID).send(authString, new ErrorMessage("The game is over!"));
        } else if ((Objects.equals(username, game.blackUsername()) && game.game().getTeamTurn() == ChessGame.TeamColor.WHITE)
            || (Objects.equals(username, game.whiteUsername()) && game.game().getTeamTurn() == ChessGame.TeamColor.BLACK)) {
            gameConnectionManagers.get(gameID).send(authString, new ErrorMessage("It is not your turn!"));
        } else if (!Objects.equals(username, game.whiteUsername()) && !Objects.equals(username, game.blackUsername())) {
            gameConnectionManagers.get(gameID).send(authString, new ErrorMessage("Observers can't make moves!"));
        } else {
            try {
                game.game().makeMove(chessMove);
                gameDAO.updateGame(game);
            } catch (InvalidMoveException e) {
                gameConnectionManagers.get(gameID).send(authString, new ErrorMessage(e.getMessage()));
                return;
            }
            LoadGame loadGame = new LoadGame(game);
            Notification notification = new Notification(String.format("%s moved %s to %s.", username, chessMove.getStartPosition().toString(), chessMove.getEndPosition().toString()));
            gameConnectionManagers.get(gameID).broadcast(authString, notification);
            gameConnectionManagers.get(gameID).send(authString, loadGame);
            gameConnectionManagers.get(gameID).broadcast(authString, loadGame);

            var nextPlayerName = switch ( game.game().getTeamTurn()) {
                case WHITE -> game.whiteUsername();
                case BLACK -> game.blackUsername();
            };
            if (game.game().isInCheckmate(game.game().getTeamTurn())) {
                gameConnectionManagers.get(gameID).broadcast("", new Notification(String.format("%s's team is in checkmate. Game over!", nextPlayerName)));
                game.game().setGameOver();
                gameDAO.updateGame(game);
            } else if (game.game().isInStalemate(game.game().getTeamTurn())) {
                gameConnectionManagers.get(gameID).broadcast("", new Notification("The game is in stalemate. Game over!"));
                game.game().setGameOver();
                gameDAO.updateGame(game);
            }
            else if (game.game().isInCheck(game.game().getTeamTurn())) {
                gameConnectionManagers.get(gameID).broadcast("", new Notification(String.format("%s's king is in check.", nextPlayerName)));
            }
        }


    }

    private void leave(String authString, Integer gameID) throws DataAccessException, IOException {
        String username = authDAO.getAuth(authString).username();
        GameData game = gameDAO.getGame(gameID);
        GameData updatedGame;
        if (Objects.equals(username, game.whiteUsername())) {
            updatedGame = new GameData(gameID, null, game.blackUsername(), game.gameName(), game.game());
        } else if (Objects.equals(username, game.blackUsername())) {
            updatedGame = new GameData(gameID, game.whiteUsername(), null, game.gameName(), game.game());
        } else {
            updatedGame = game;
        }
        gameDAO.updateGame(updatedGame);
        gameConnectionManagers.get(gameID).broadcast(authString, new Notification(String.format("%s has left the game.", username)));
        gameConnectionManagers.get(gameID).remove(authString);
    }

    private void resign(String authString, Integer gameID) throws DataAccessException, IOException {
        String username = authDAO.getAuth(authString).username();
        GameData game = gameDAO.getGame(gameID);

        if (game.game().getGameOver()) {
            gameConnectionManagers.get(gameID).send(authString, new ErrorMessage("The other player has already resigned!"));
        } else if (Objects.equals(username, game.whiteUsername()) || Objects.equals(username, game.blackUsername())) {
            gameConnectionManagers.get(gameID).broadcast("", new Notification(String.format("%s has resigned. Game over!", username)));
            game.game().setGameOver();
            gameDAO.updateGame(game);
        } else {
            gameConnectionManagers.get(gameID).send(authString, new ErrorMessage("Observers can't resign!"));
        }
    }
}
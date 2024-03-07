package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.interfaces.GameDAO;
import model.GameData;
import model.ShortGameData;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

public class DatabaseGameDAO implements GameDAO {

    public DatabaseGameDAO() {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            return;
        }
    }

    @Override
    public int createGame(GameData game) throws DataAccessException {
        String query = "insert into games (whiteUsername, blackUsername, gameName, game) values (?, ?, ?, ?)";
        var conn = DatabaseManager.getConnection();
        int gameID;

        try (var preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, game.whiteUsername());
            preparedStatement.setString(2, game.blackUsername());
            preparedStatement.setString(3, game.gameName());
            preparedStatement.setString(4, new Gson().toJson(game));
            preparedStatement.executeUpdate();

            var resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                gameID = resultSet.getInt(1);
            } else {
                throw new DataAccessException("Could not generate ID");
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return gameID;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        GameData returnedGame;
        String query = String.format("SELECT * FROM games WHERE gameID='%d'", gameID);
        var conn = DatabaseManager.getConnection();
        try (var preparedStatement = conn.prepareStatement(query)) {
            try (var rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    var whiteUsername = rs.getString("whiteUsername");
                    var blackUsername = rs.getString("blackUsername");
                    var gameName = rs.getString("gameName");
                    var game = new Gson().fromJson(rs.getString("game"), ChessGame.class);
                    returnedGame = new GameData(gameID, whiteUsername, blackUsername, gameName, game);
                } else {
                    throw new DataAccessException("No such game");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Bad query");
        }
        return returnedGame;
    }

    @Override
    public Collection<ShortGameData> listGames() throws DataAccessException {
        Collection<ShortGameData> returnedGames = new ArrayList<>();
        String query = "SELECT * FROM games";
        var conn = DatabaseManager.getConnection();
        try (var preparedStatement = conn.prepareStatement(query)) {
            try (var rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    var gameID = rs.getInt("gameID");
                    var whiteUsername = rs.getString("whiteUsername");
                    var blackUsername = rs.getString("blackUsername");
                    var gameName = rs.getString("gameName");
                    returnedGames.add(new ShortGameData(gameID, whiteUsername, blackUsername, gameName));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Bad query");
        }
        return returnedGames;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String query = "UPDATE games SET blackUsername=?, whiteUsername=?, gameName=?, game=? WHERE gameID=?";
        var conn = DatabaseManager.getConnection();
        try (var preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, game.blackUsername());
            preparedStatement.setString(2, game.whiteUsername());
            preparedStatement.setString(3, game.gameName());
            preparedStatement.setString(4, new Gson().toJson(game.game()));
            preparedStatement.setInt(5, game.gameID());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clearGames() {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("delete from games where TRUE")) {
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                return;
            }
        } catch (SQLException | DataAccessException e) {
            return;
        }
    }
}

package dataAccess;

import model.AuthData;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseAuthDAO implements AuthDAO{

    public DatabaseAuthDAO() throws DataAccessException {
        DatabaseManager.createDatabase();
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String username;
        String query = String.format("SELECT * FROM auth WHERE authToken='%s'", authToken);
        var conn = DatabaseManager.getConnection();
        try (var preparedStatement = conn.prepareStatement(query)) {
            try (var rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    username = rs.getString("username");
                } else {
                    throw new DataAccessException("No username");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Bad query");
        }
        return new AuthData(authToken, username);
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        String query = "insert into auth (authToken, username) values (?, ?)";
        var conn = DatabaseManager.getConnection();

        if (auth.username().matches("[a-zA-Z0-9]+")) {
            try (var preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setString(1, auth.authToken());
                preparedStatement.setString(2, auth.username());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        }
    }

    @Override
    public void deleteAuth(AuthData auth) throws DataAccessException {
        String query = "delete from auth where authToken=?";
        var conn = DatabaseManager.getConnection();

        try (var preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, auth.authToken());
            if (preparedStatement.executeUpdate() == 0) {
                throw new DataAccessException("Auth doesn't exist");
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clearAuth() {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("delete from auth where TRUE")) {
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                return;
            }
        } catch (SQLException | DataAccessException e) {
            return;
        }
    }
}

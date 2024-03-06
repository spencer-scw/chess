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

    }

    @Override
    public void deleteAuth(AuthData auth) throws DataAccessException {

    }

    @Override
    public void clearAuth() {

    }
}

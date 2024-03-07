package dataAccess;

import com.google.gson.Gson;
import dataAccess.interfaces.UserDAO;
import model.AuthData;
import model.UserData;

import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUserDAO implements UserDAO {

    public DatabaseUserDAO() {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            return;
        }
    }

    @Override
    public void addUser(UserData user) throws DataAccessException {
        String query = "insert into users (username, passHash, email) values (?, ?, ?)";
        var conn = DatabaseManager.getConnection();

        try (var preparedStatement = conn.prepareStatement(query)) {
            if (user.username().matches("[a-zA-Z0-9]+"))
                preparedStatement.setString(1, user.username());
            if (user.password().matches("[a-zA-Z0-9!@#$%^&*]+"))
                preparedStatement.setString(2, user.password());
            if (user.email().matches("[a-zA-Z0-9+_.-@]+"))
                preparedStatement.setString(3, user.email());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        UserData userData;
        String query = String.format("SELECT * FROM users WHERE username='%s'", username);
        var conn = DatabaseManager.getConnection();
        try (var preparedStatement = conn.prepareStatement(query)) {
            try (var rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    var passHash = rs.getString("passHash");
                    var email = rs.getString("email");
                    userData = new UserData(username, passHash, email);
                } else {
                    throw new DataAccessException("No username");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Bad query");
        }
        return userData;
    }

    @Override
    public void clearUsers() {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("delete from users where TRUE")) {
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                return;
            }
        } catch (SQLException | DataAccessException e) {
            return;
        }
    }
}

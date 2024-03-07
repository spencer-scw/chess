package dataAccess;

import com.google.gson.Gson;
import dataAccess.interfaces.UserDAO;
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
            if (user.email().matches("[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+"))
                preparedStatement.setString(3, user.email());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
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

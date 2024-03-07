package dataAccess;

import dataAccess.interfaces.UserDAO;
import model.UserData;

public class DatabaseUserDAO implements UserDAO {
    @Override
    public void addUser(UserData user) throws DataAccessException {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void clearUsers() {

    }
}

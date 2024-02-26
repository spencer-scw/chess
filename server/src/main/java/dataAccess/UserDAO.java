package dataAccess;

import model.UserData;

public interface UserDAO {
    void addUser(UserData user) throws DataAccessException;

    UserData getUser() throws DataAccessException;

    void clearUsers() throws DataAccessException;
}

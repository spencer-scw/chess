package dataAccess;

import model.AuthData;

public interface AuthDAO {
    boolean validateAuth(AuthData auth) throws DataAccessException;

    void createAuth(AuthData auth) throws DataAccessException;

    void deleteAuth(AuthData auth) throws DataAccessException;

    void clearAuth();
}

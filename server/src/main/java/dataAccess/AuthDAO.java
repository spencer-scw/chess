package dataAccess;

import model.AuthData;

public interface AuthDAO {
    AuthData getAuth(String authToken) throws DataAccessException;

    void createAuth(AuthData auth) throws DataAccessException;

    void deleteAuth(AuthData auth) throws DataAccessException;

    void clearAuth();
}

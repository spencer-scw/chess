package dataAccess;

import model.AuthData;

public interface AuthDAO {
    boolean getAuth(AuthData auth) throws DataAccessException;

    boolean createAuth(AuthData auth) throws DataAccessException;

    boolean deleteAuth(AuthData auth) throws DataAccessException;

    boolean clearAuth() throws DataAccessException;
}

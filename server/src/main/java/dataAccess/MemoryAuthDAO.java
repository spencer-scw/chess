package dataAccess;

import model.AuthData;

import java.util.HashSet;

public class MemoryAuthDAO implements AuthDAO {

    HashSet<AuthData> authDataHashSet = new HashSet<>();

    @Override
    public boolean validateAuth(AuthData auth) throws DataAccessException {
        if (!authDataHashSet.contains(auth)) {
            throw new DataAccessException("Auth not found!");
        }
        else {
            return true;
        }
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        if (authDataHashSet.contains(auth)) {
            throw new DataAccessException("Auth already exists!");
        }
        else {
            authDataHashSet.add(auth);
        }
    }

    @Override
    public void deleteAuth(AuthData auth) throws DataAccessException {
        if (!authDataHashSet.contains(auth)) {
            throw new DataAccessException("Auth not found!");
        }
        else {
            authDataHashSet.remove(auth);
        }
    }

    @Override
    public void clearAuth() {
        this.authDataHashSet = new HashSet<>();
    }
}

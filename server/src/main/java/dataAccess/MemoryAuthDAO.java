package dataAccess;

import model.AuthData;

import java.util.HashSet;

public class MemoryAuthDAO implements AuthDAO {

    HashSet<AuthData> authDataHashSet = new HashSet<>();

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        for (AuthData auth : authDataHashSet) {
            if (auth.authToken().equals(authToken)) {
                return auth;
            }
        }
        throw new DataAccessException("Auth token not found!");
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

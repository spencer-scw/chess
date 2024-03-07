package service;

import dataAccess.interfaces.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.interfaces.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {

    AuthDAO authDAO;
    UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public AuthData register(UserData user) {
        try {
            userDAO.addUser(user);
        } catch (DataAccessException e) {
            return null;
        }
        return newAuth(user.username());
    }

    public AuthData login(UserData user) {
        UserData dbUser;
        try {
            dbUser = userDAO.getUser(user.username());
        } catch (DataAccessException e) {
            return null;
        }

        if (dbUser.password().equals(user.password())) {
            return newAuth(user.username());
        }
        return null;
    }

    public boolean logout(String authToken) {
        AuthData auth;
        try {
            auth = authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            return false;
        }
        try {
            authDAO.deleteAuth(auth);
        } catch (DataAccessException e) {
            return false;
        }
        return true;
    }

    private AuthData newAuth(String username) {
        AuthData auth = new AuthData(UUID.randomUUID().toString(), username);
        try {
            authDAO.createAuth(auth);
        } catch (DataAccessException e) {
            return null;
        }
        return auth;
    }
}

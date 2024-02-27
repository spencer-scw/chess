package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
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
        AuthData auth = new AuthData(UUID.randomUUID().toString(), user.username());
        try {
            authDAO.createAuth(auth);
        } catch (DataAccessException e) {
            return null;
        }
        return auth;
    }

    public AuthData login(UserData user) {
        throw new RuntimeException("Not implemented");
    }

    public void logout(UserData user) {
        throw new RuntimeException("Not implemented");
    }
}

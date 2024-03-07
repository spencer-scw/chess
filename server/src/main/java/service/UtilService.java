package service;

import dataAccess.interfaces.AuthDAO;
import dataAccess.interfaces.GameDAO;
import dataAccess.interfaces.UserDAO;

public class UtilService {
    AuthDAO authDAO;
    GameDAO gameDAO;
    UserDAO userDAO;
    public UtilService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    public void clear() {
        authDAO.clearAuth();
        gameDAO.clearGames();
        userDAO.clearUsers();
    }
}

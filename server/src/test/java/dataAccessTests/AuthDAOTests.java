package dataAccessTests;

import dataAccess.interfaces.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.DatabaseAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AuthDAOTests {
    private static AuthDAO authDAO;

    @BeforeAll
    public static void init() throws DataAccessException {
        authDAO = new DatabaseAuthDAO();
        authDAO.clearAuth();
    }

    @Test
    public void getValidAuth() throws DataAccessException {

        var auth123 = new AuthData("auth123", "greg");
        authDAO.createAuth(auth123);

        var dbAuthData =  authDAO.getAuth("auth123");
        assertEquals(auth123, dbAuthData);
    }

    @Test
    public void getBadAuth() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> authDAO.getAuth("fake_auth"));
    }

    @Test
    public void createValidAuth() throws DataAccessException {
        assertDoesNotThrow(() -> authDAO.createAuth(new AuthData("unused_auth", "timo")));
    }

    @Test
    public void createDuplicateAuth() throws DataAccessException {
        authDAO.createAuth(new AuthData("unused_auth_2", "dan"));
        assertThrows(DataAccessException.class, () -> authDAO.createAuth(new AuthData("unused_auth_2", "dan")));
    }

    @Test
    public void deleteValidAuth() throws DataAccessException {
        var herobrine = new AuthData("delete_me", "herobrine");
        authDAO.createAuth(herobrine);
        authDAO.deleteAuth(herobrine);
        assertThrows(DataAccessException.class, () -> authDAO.getAuth("delete_me"));
    }

    @Test
    public void deleteInvalidAuth() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> authDAO.deleteAuth(new AuthData("fake_auth", "unreal")));
    }

    @Test
    public void clearAll() throws DataAccessException {
        authDAO.createAuth(new AuthData("a", "a"));
        authDAO.createAuth(new AuthData("b", "b"));
        authDAO.createAuth(new AuthData("c", "c"));
        authDAO.clearAuth();
        assertThrows(DataAccessException.class, () -> authDAO.getAuth("a"));
        assertThrows(DataAccessException.class, () -> authDAO.getAuth("b"));
        assertThrows(DataAccessException.class, () -> authDAO.getAuth("c"));
    }
}

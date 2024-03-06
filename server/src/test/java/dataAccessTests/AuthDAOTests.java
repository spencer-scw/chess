package dataAccessTests;

import dataAccess.AuthDAO;
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
}

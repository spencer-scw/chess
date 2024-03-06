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
    }

    @Test
    public void getAuthTest() throws DataAccessException {

        // TODO: Update this test to create the auth instead of relying on the manually inserted one.

        var authData =  authDAO.getAuth("abad");
        assertEquals(new AuthData("abad", "greg"), authData);
    }
}

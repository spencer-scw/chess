package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.DatabaseUserDAO;
import dataAccess.interfaces.UserDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTests {
    static UserDAO userDAO;
    @BeforeAll
    static public void init() throws DataAccessException {
        userDAO = new DatabaseUserDAO();
        userDAO.clearUsers();
    }

    @Test
    public void addValidUser() throws DataAccessException {
        assertDoesNotThrow(() ->
            userDAO.addUser(new UserData("abc", "123", "abc@example.com"))
        );
    }

    @Test
    public void addInvalidUser() throws DataAccessException {
        assertThrows(DataAccessException.class, () ->
            userDAO.addUser(new UserData("!@#$%", "^&*", "abc@example.com"))
        );
    }

    @Test
    public void getValidUser() throws DataAccessException {
        var mat = new UserData("Mat", "dice123", "mat@wot.net");
        userDAO.addUser(mat);
        assertEquals(mat, userDAO.getUser("mat"));
    }

    @Test
    public void getFakeUser() throws DataAccessException {
        assertThrows(DataAccessException.class, () ->
            userDAO.getUser("Unreal")
        );
    }

    @Test
    public void testClear() throws DataAccessException {
        for (int i = 0; i < 10; i++) {
            userDAO.addUser(new UserData(String.valueOf(i), "changeme", "test@example.com"));
        }
        userDAO.clearUsers();
        assertThrows(DataAccessException.class, () ->
            userDAO.getUser("5")
    );
    }
}

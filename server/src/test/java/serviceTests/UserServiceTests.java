package serviceTests;

import dataAccess.*;
import dataAccess.interfaces.AuthDAO;
import dataAccess.interfaces.UserDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
    AuthDAO authDAO;
    UserDAO userDAO;
    UserService service;
    @BeforeEach
    void setup() {
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();
        service = new UserService(authDAO, userDAO);
    }

    @Test
    void registerPositive() {
        var greg = new UserData("Greg", "asphodel", "greg@aloe.com");
        assertNotNull(service.register(greg));
    }

    @Test
    void registerNegative() {
        var greg = new UserData("Greg", "asphodel", "greg@aloe.com");
        service.register(greg);
        assertNull(service.register(greg));
    }

    @Test
    void loginPositive() {
        var greg = new UserData("Greg", "asphodel", "greg@aloe.com");
        service.register(greg);
        assertNotNull(service.login(greg));
    }

    @Test
    void loginNegative() {
        var greg = new UserData("Greg", "asphodel", "greg@aloe.com");
        assertNull(service.login(greg));
    }

    @Test
    void logoutPositive() {
        var greg = new UserData("Greg", "asphodel", "greg@aloe.com");
        var auth = service.register(greg);
        assertTrue(service.logout(auth.authToken()));
    }

    @Test
    void logoutNegative() {
        var greg = new UserData("Greg", "asphodel", "greg@aloe.com");
        var authToken = "fakeAuthToken";
        assertFalse(service.logout(authToken));
    }
}

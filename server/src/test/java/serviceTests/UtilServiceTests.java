package serviceTests;

import dataAccess.*;
import dataAccess.interfaces.AuthDAO;
import dataAccess.interfaces.GameDAO;
import dataAccess.interfaces.UserDAO;
import org.junit.jupiter.api.Test;
import service.UtilService;
import static org.junit.jupiter.api.Assertions.*;

public class UtilServiceTests {
    @Test
    void clear() {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserDAO userDAO = new MemoryUserDAO();


        UtilService service = new UtilService(authDAO, gameDAO, userDAO);
        assertDoesNotThrow(service::clear);
    }
}

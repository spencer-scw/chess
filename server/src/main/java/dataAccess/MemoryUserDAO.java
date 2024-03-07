package dataAccess;

import dataAccess.interfaces.UserDAO;
import model.UserData;

import java.util.HashSet;
import java.util.Objects;

public class MemoryUserDAO implements UserDAO {

    HashSet<UserData> userDataHashSet = new HashSet<>();

    @Override
    public void addUser(UserData user) throws DataAccessException {
        for (UserData currUser : userDataHashSet) {
            if (Objects.equals(user.username(), currUser.username())){
                throw new DataAccessException("already taken");
            }
        }
        userDataHashSet.add(user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        for (UserData user : userDataHashSet) {
            if (Objects.equals(user.username(), username)){
                return user;
            }
        }
        throw new DataAccessException("User not found!");
    }

    @Override
    public void clearUsers() {
        this.userDataHashSet = new HashSet<>();
    }
}

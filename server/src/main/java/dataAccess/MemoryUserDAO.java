package dataAccess;

import model.UserData;

import java.util.HashSet;

public class MemoryUserDAO implements UserDAO{

    HashSet<UserData> userDataHashSet = new HashSet<>();

    @Override
    public void addUser(UserData user) throws DataAccessException {
        for (UserData curr_user : userDataHashSet) {
            if (user.username() == curr_user.username()){
                throw new DataAccessException("Username is taken!");
            }
        }
        userDataHashSet.add(user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        for (UserData user : userDataHashSet) {
            if (user.username() == user.username()){
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

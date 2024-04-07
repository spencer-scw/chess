package ui;

import serverFacade.ServerFacade;

import java.io.IOException;
import java.util.Map;

public class SignedOutHandler {
    private final ServerFacade serverFacade;
    private final SessionInfo sessionInfo;

    public SignedOutHandler(ServerFacade serverFacade, SessionInfo sessionInfo) {
        this.serverFacade = serverFacade;
        this.sessionInfo = sessionInfo;
    }

    protected String logIn(String[] params) {
        Map result;
        try {
            result = serverFacade.logIn(params);
            sessionInfo.setAuthToken((String) result.get("authToken"));
            sessionInfo.setClientState(State.SIGNEDIN);
            return String.format("Signed in successfully as %s", result.get("username"));
        } catch (Exception e) {
            if (e.getClass() != IOException.class) {
                return e.getMessage();
            } else {
                return "Incorrect username or password. Please try again.";
            }
        }
    }

    protected String register(String[] params) {
        Map result;
        try {
            result = serverFacade.register(params);
            sessionInfo.setAuthToken((String) result.get("authToken"));
            sessionInfo.setClientState(State.SIGNEDIN);
            return String.format("Successfully registered %s and signed in automatically.", result.get("username"));
        } catch (Exception e) {
            if (e.getClass() != IOException.class) {
                return "bad URL";
            } else if (e.getMessage().contains("403")) {
                return "Sorry, that username is already taken. Try another.";
            } else {
                return e.getMessage();
            }
        }
    }


}

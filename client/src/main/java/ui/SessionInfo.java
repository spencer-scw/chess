package ui;

public class SessionInfo {
    private String authToken;
    private State clientState;
    public SessionInfo(String authToken, State clientState) {
        this.authToken = authToken;
        this.clientState = clientState;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public State getClientState() {
        return clientState;
    }

    public void setClientState(State clientState) {
        this.clientState = clientState;
    }
}

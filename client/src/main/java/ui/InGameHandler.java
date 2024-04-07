package ui;

import serverFacade.ServerFacade;

import java.util.ArrayList;

public class InGameHandler {
    private final ServerFacade serverFacade;
    private final SessionInfo sessionInfo;

    public InGameHandler(ServerFacade serverFacade, SessionInfo sessionInfo) {
        this.serverFacade = serverFacade;
        this.sessionInfo = sessionInfo;
    }

    protected String redraw() {
        return BoardPrinter.printBoard(sessionInfo.getBoard(), sessionInfo.getTeamColor());
    }

}

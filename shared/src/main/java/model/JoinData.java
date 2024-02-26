package model;

import chess.ChessGame;

public record JoinData(ChessGame.TeamColor desiredColor, int gameID) {
}

package com.games.kalah.domain;

public enum EPlayer {
    Player1("P1"),
    Player2("P2");

    EPlayer(String playerName) {
        this.playerName = playerName;
    }

    private final String playerName;

    public String getPlayerName() {
        return playerName;
    }
}

package org.panther.Models;

public class PlayerVoteCount {
    private final String playerName;
    private final int voteCount;

    public PlayerVoteCount(String playerName, int voteCount)   {
        this.playerName = playerName;
        this.voteCount = voteCount;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public String getPlayerName()   {
        return playerName;
    }


}

package be.woutzah.chatbrawl.leaderboard.users;

import java.util.UUID;

public class WinsUser extends User {

    private int wins;

    public WinsUser(int rank, UUID uuid, int wins) {
        super(rank, uuid);
        this.wins = wins;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }
}

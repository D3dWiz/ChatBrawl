package be.woutzah.chatbrawl.leaderboard.users;

import java.util.UUID;

public class TimeUser extends User {

    private final int seconds;

    public TimeUser(int rank, UUID uuid, int seconds) {
        super(rank, uuid);
        this.seconds = seconds;
    }

    public int getSeconds() {
        return seconds;
    }
}

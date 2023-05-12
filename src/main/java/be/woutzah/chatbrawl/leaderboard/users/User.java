package be.woutzah.chatbrawl.leaderboard.users;

import java.util.UUID;

public abstract class User {

    private final int rank;
    private final UUID uuid;

    public User(int rank, UUID uuid) {
        this.rank = rank;
        this.uuid = uuid;
    }

    public int getRank() {
        return rank;
    }

    public UUID getUuid() {
        return uuid;
    }
}

package be.woutzah.chatbrawl.leaderboard;

import java.util.List;
import java.util.UUID;

public class LeaderboardUser {

    private UUID uuid;
    private List<LeaderboardStatistic> leaderboardStatisticList;

    public LeaderboardUser(UUID uuid, List<LeaderboardStatistic> leaderboardStatisticList) {
        this.uuid = uuid;
        this.leaderboardStatisticList = leaderboardStatisticList;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public List<LeaderboardStatistic> getLeaderboardStatisticList() {
        return leaderboardStatisticList;
    }

    public void setLeaderboardStatisticList(List<LeaderboardStatistic> leaderboardStatisticList) {
        this.leaderboardStatisticList = leaderboardStatisticList;
    }
}

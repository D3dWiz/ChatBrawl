package be.woutzah.chatbrawl.leaderboard;

import be.woutzah.chatbrawl.races.types.RaceType;

import java.util.UUID;

public class LeaderboardStatistic {

    private UUID uuid;
    private RaceType raceType;
    private int seconds;

    public LeaderboardStatistic(UUID uuid, RaceType raceType, int seconds) {
        this.uuid = uuid;
        this.raceType = raceType;
        this.seconds = seconds;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public RaceType getRaceType() {
        return raceType;
    }

    public void setRaceType(RaceType raceType) {
        this.raceType = raceType;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }
}

package be.woutzah.chatbrawl.time;

import be.woutzah.chatbrawl.ChatBrawl;
import be.woutzah.chatbrawl.races.types.RaceType;

import java.time.Duration;
import java.time.Instant;

public class TimeManager {

    private ChatBrawl plugin;
    private Instant startTime;
    private Instant stopTime;

    public TimeManager(ChatBrawl plugin) {
        this.plugin = plugin;
    }

    public void startTimer() {
        startTime = Instant.now();
    }

    public void stopTimer() {
        stopTime = Instant.now();
    }

    public int getRemainingTime(RaceType type) {
        return (int) Duration.between(Instant.now(), startTime.plusSeconds(this.plugin.getRaceManager().getRace(type).getDurationSeconds())).getSeconds();
    }

    public int getTotalSeconds() {
        return (int) Duration.between(startTime, stopTime).getSeconds();
    }

    public String getTimeString() {
        return formatTime(getTotalSeconds());
    }

    public String formatTime(int seconds) {
        return String.format("%dh %02dm %02ds",
                seconds / 3600,
                (seconds % 3600) / 60,
                (seconds % 60));
    }
}

package be.woutzah.chatbrawl.contestants;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ContestantsManager {

    private final Map<UUID, Integer> contestantScores;

    public ContestantsManager() {
        this.contestantScores = new HashMap<>();
    }

    public void fillOnlinePlayers() {
        Bukkit.getOnlinePlayers().forEach(p -> contestantScores.put(p.getUniqueId(), 0));
    }

    public void removeOnlinePlayers() {
        contestantScores.clear();
    }

    public void addContestant(UUID uuid) {
        if (contestantScores.containsKey(uuid)) return;
        contestantScores.put(uuid, 0);
    }

    public void addScore(UUID uuid) {
        addScore(uuid, 1);
    }

    public void addScore(UUID uuid, int amount) {
        contestantScores.put(uuid, contestantScores.get(uuid) + amount);
    }

    public boolean hasWon(UUID uuid, int amount) {
        return contestantScores.get(uuid) >= amount;
    }
}

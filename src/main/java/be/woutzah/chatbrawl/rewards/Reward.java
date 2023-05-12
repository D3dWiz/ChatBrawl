package be.woutzah.chatbrawl.rewards;

import java.util.List;

public class Reward {

    private String id;
    private int chance;
    private String broadcast;
    private String title;
    private String subtitle;
    private List<String> commands;

    public Reward(String id, int chance, String broadcast, String title, String subtitle, List<String> commands) {
        this.id = id;
        this.chance = chance;
        this.broadcast = broadcast;
        this.title = title;
        this.subtitle = subtitle;
        this.commands = commands;
    }

    public String getId() {
        return id;
    }

    public int getChance() {
        return chance;
    }

    public String getBroadcast() {
        return broadcast;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public List<String> getCommands() {
        return commands;
    }
}

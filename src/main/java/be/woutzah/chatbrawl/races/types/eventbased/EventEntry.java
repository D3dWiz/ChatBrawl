package be.woutzah.chatbrawl.races.types.eventbased;

import be.woutzah.chatbrawl.settings.SettingManager;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.List;

public class EventEntry<T> {
    protected static List<Integer> rewardIds;
    private final int amount;
    protected SettingManager settingManager;
    protected T entry;

    public EventEntry(T entry, List<Integer> rewardIds, int amount) {
        EventEntry.rewardIds = rewardIds;
        this.amount = amount;
        this.entry = entry;
    }

    public static List<Integer> getRewardIds() {
        return rewardIds;
    }

    public int getAmount() {
        return amount;
    }

    public Material getMaterial() {
        return (Material) entry;
    }

    public EntityType getEntityType() {
        return (EntityType) entry;
    }
}
package be.woutzah.chatbrawl.races.types.huntrace;

import org.bukkit.entity.EntityType;

import java.util.List;

public class HuntEntry {

    private final EntityType entityType;
    private final int amount;
    private final List<Integer> rewardIds;

    public HuntEntry(EntityType entityType, int amount, List<Integer> rewardIds) {
        this.entityType = entityType;
        this.amount = amount;
        this.rewardIds = rewardIds;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public int getAmount() {
        return amount;
    }

    public List<Integer> getRewardIds() {
        return rewardIds;
    }
}

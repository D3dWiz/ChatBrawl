package be.woutzah.chatbrawl.races.types.huntrace;

import be.woutzah.chatbrawl.races.types.RaceEntry;
import org.bukkit.entity.EntityType;

import java.util.List;

public class HuntEntry extends RaceEntry {

    private final EntityType entityType;
    private final int amount;

    public HuntEntry(EntityType entityType, int amount, List<Integer> rewardIds) {
        super(rewardIds);
        this.entityType = entityType;
        this.amount = amount;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public int getAmount() {
        return amount;
    }

}

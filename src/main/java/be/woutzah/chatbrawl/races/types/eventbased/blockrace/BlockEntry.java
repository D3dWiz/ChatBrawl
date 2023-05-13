package be.woutzah.chatbrawl.races.types.eventbased.blockrace;

import be.woutzah.chatbrawl.races.types.RaceEntry;
import org.bukkit.Material;

import java.util.List;

public class BlockEntry extends RaceEntry {

    private final Material material;
    private final int amount;

    public BlockEntry(Material material, int amount, List<Integer> rewardIds) {
        super(rewardIds);
        this.material = material;
        this.amount = amount;
    }

    public Material getMaterial() {
        return material;
    }

    public int getAmount() {
        return amount;
    }
}

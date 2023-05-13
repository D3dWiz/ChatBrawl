package be.woutzah.chatbrawl.races.types.foodrace;

import be.woutzah.chatbrawl.races.types.RaceEntry;
import org.bukkit.Material;

import java.util.List;

public class FoodEntry extends RaceEntry {
    private final Material material;
    private final int amount;

    public FoodEntry(Material material, int amount, List<Integer> rewardIds) {
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

package be.woutzah.chatbrawl.races.types.foodrace;

import org.bukkit.Material;

import java.util.List;

public class FoodEntry {
    private final Material material;
    private final int amount;
    private final List<Integer> rewardIds;

    public FoodEntry(Material material, int amount, List<Integer> rewardIds) {
        this.material = material;
        this.amount = amount;
        this.rewardIds = rewardIds;
    }

    public Material getMaterial() {
        return material;
    }

    public int getAmount() {
        return amount;
    }

    public List<Integer> getRewardIds() {
        return rewardIds;
    }
}

package be.woutzah.chatbrawl.races.types;

import java.util.List;

public abstract class RaceEntry {

    protected static List<Integer> rewardIds;

    public RaceEntry(List<Integer> rewardIds) {
        RaceEntry.rewardIds = rewardIds;
    }
    public static List<Integer> getRewardIds() {
        return rewardIds;
    }
}

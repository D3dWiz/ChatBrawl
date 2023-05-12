package be.woutzah.chatbrawl.races.types.chatrace;

import java.util.List;

public class WordToGuess {

    private final String word;
    private final List<Integer> rewardIds;

    public WordToGuess(String word, List<Integer> rewardIds) {
        this.word = word;
        this.rewardIds = rewardIds;
    }

    public String getWord() {
        return word;
    }

    public List<Integer> getRewardIds() {
        return rewardIds;
    }
}

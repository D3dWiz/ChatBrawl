package be.woutzah.chatbrawl.races.types.chatbased.typerace;

import be.woutzah.chatbrawl.races.types.RaceEntry;

import java.util.List;

public class WordToType extends RaceEntry {

    private final String word;

    public WordToType(String word, List<Integer> rewardIds) {
        super(rewardIds);
        this.word = word;
    }

    public String getWord() {
        return word;
    }
}

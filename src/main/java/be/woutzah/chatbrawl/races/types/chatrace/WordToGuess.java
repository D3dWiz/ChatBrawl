package be.woutzah.chatbrawl.races.types.chatrace;

import be.woutzah.chatbrawl.races.types.RaceEntry;

import java.util.List;

public class WordToGuess extends RaceEntry {

    private final String word;

    public WordToGuess(String word, List<Integer> rewardIds) {
        super(rewardIds);
        this.word = word;
    }

    public String getWord() {
        return word;
    }
}

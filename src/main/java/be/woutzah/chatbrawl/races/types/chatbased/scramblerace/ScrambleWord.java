package be.woutzah.chatbrawl.races.types.chatbased.scramblerace;

import be.woutzah.chatbrawl.races.types.RaceEntry;

import java.util.List;

public class ScrambleWord extends RaceEntry {
    private final String word;
    private final int difficulty;
    private String scrambledWord;

    public ScrambleWord(String word, int difficulty, List<Integer> rewardIds) {
        super(rewardIds);
        this.word = word;
        this.difficulty = difficulty;
    }

    public String getWord() {
        return word;
    }

    public String getScrambledWord() {
        return scrambledWord;
    }

    public void setScrambledWord(String scrambledWord) {
        this.scrambledWord = scrambledWord;
    }

    public int getDifficulty() {
        return difficulty;
    }
}

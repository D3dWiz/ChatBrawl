package be.woutzah.chatbrawl.races.types.scramblerace;

import be.woutzah.chatbrawl.races.types.RaceEntry;

import java.util.List;

public class ScrambleWord extends RaceEntry {
    private final String word;
    private String scrambledWord;
    private final int difficulty;

    public ScrambleWord(String word, int difficulty, List<Integer> rewardIds) {
        super(rewardIds);
        this.word = word;
        this.difficulty = difficulty;
    }

    public String getWord() {
        return word;
    }

    public void setScrambledWord(String scrambledWord) {
        this.scrambledWord = scrambledWord;
    }

    public String getScrambledWord() {
        return scrambledWord;
    }

    public int getDifficulty() {
        return difficulty;
    }
}

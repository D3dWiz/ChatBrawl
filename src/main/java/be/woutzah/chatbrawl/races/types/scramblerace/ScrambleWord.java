package be.woutzah.chatbrawl.races.types.scramblerace;

import java.util.List;

public class ScrambleWord {
    private final String word;
    private  String scrambledWord;
    private final int difficulty;
    private final List<Integer> rewardIds;

    public ScrambleWord(String word,int difficulty, List<Integer> rewardIds) {
        this.word = word;
        this.difficulty = difficulty;
        this.rewardIds = rewardIds;
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

    public List<Integer> getRewardIds() {
        return rewardIds;
    }
}

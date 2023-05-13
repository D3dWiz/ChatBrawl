package be.woutzah.chatbrawl.races.types.quizrace;

import be.woutzah.chatbrawl.races.types.RaceEntry;

import java.util.List;

public class Question extends RaceEntry {

    private final String question;
    private final List<String> answers;

    public Question(String question, List<String> answers, List<Integer> rewardIds) {
        super(rewardIds);
        this.question = question;
        this.answers = answers;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getAnswers() {
        return answers;
    }
}


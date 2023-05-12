package be.woutzah.chatbrawl.races.types.quizrace;

import java.util.List;

public class Question {

    private final String question;
    private final List<String> answers;
    private final List<Integer> rewardIds;

    public Question(String question, List<String> answers, List<Integer> rewardIds) {
        this.question = question;
        this.answers = answers;
        this.rewardIds = rewardIds;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public List<Integer> getRewardIds() {
        return rewardIds;
    }
}


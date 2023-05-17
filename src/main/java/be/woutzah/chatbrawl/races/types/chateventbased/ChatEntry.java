package be.woutzah.chatbrawl.races.types.chateventbased;

import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.leaderboard.LeaderboardManager;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.races.types.RaceType;
import be.woutzah.chatbrawl.rewards.RewardManager;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.settings.races.QuizRaceSetting;
import be.woutzah.chatbrawl.time.TimeManager;

import java.util.List;

public abstract class ChatEntry {
    protected static List<Integer> rewardIds;
    protected String chatEntry;
    protected List<String> chatEntryAnswerList;
    protected SettingManager settingManager;

    public ChatEntry(String chatEntry, List<String> chatEntryAnswerList, List<Integer> rewardIds) {
        ChatEntry.rewardIds = rewardIds;
        this.chatEntry = chatEntry;
        this.chatEntryAnswerList = chatEntryAnswerList;
    }

    public static List<Integer> getRewardIds() {
        return rewardIds;
    }

    public String getChatEntry() {
        return chatEntry;
    }

    public void setChatEntry(String scrambledWord) {
        this.chatEntry = scrambledWord;
    }

    public String getFirstChatEntryAnswer() {
        return chatEntryAnswerList.get(0);
    }

    public boolean matchesAnswer(String string) {
        return chatEntryAnswerList.stream().anyMatch(s -> s.equals(string));
    }

    public boolean checkAnswer(String answer) {
        return chatEntryAnswerList.stream().anyMatch(s -> s.equals(answer));
    }

    public static class QuizRace extends ChatRace {

        public QuizRace(RaceManager raceManager, SettingManager settingManager,
                        RewardManager rewardManager, TimeManager timeManager,
                        LeaderboardManager leaderboardManager) {
            super(RaceType.QUIZ, raceManager, settingManager, rewardManager, timeManager, leaderboardManager);
            initChatEntryList();
        }

        private void initChatEntryList() {
            settingManager.getConfigSection(QuizRaceSetting.QUESTIONS).getKeys(false).forEach(entry -> {
                String question = settingManager.getString(ConfigType.QUIZRACE, "questions." + entry + ".question");
                List<String> answers = settingManager.getStringList(ConfigType.QUIZRACE, "questions." + entry + ".answer");
                List<Integer> rewardIds = settingManager.getIntegerList(ConfigType.QUIZRACE, "questions." + entry + ".rewards");
                chatEntryList.add(new ChatEntry(question, answers, rewardIds) {
                });
            });
        }

    }
}

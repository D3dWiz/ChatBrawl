package be.woutzah.chatbrawl.races.types.chateventbased.quizrace;

import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.leaderboard.LeaderboardManager;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.races.types.RaceType;
import be.woutzah.chatbrawl.races.types.chateventbased.ChatEntry;
import be.woutzah.chatbrawl.races.types.chateventbased.ChatRace;
import be.woutzah.chatbrawl.rewards.RewardManager;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.settings.races.QuizRaceSetting;
import be.woutzah.chatbrawl.time.TimeManager;

import java.util.List;

public class QuizRace extends ChatRace {

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
            chatEntryList.add(new ChatEntry(question, answers, rewardIds) {});
        });
    }

}

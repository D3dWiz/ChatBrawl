package be.woutzah.chatbrawl.races.types.chatbased;

import be.woutzah.chatbrawl.leaderboard.LeaderboardManager;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.races.types.Race;
import be.woutzah.chatbrawl.races.types.RaceType;
import be.woutzah.chatbrawl.rewards.RewardManager;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.time.TimeManager;

public abstract class ChatRace extends Race {
    public ChatRace(RaceType type, RaceManager raceManager, SettingManager settingManager, RewardManager rewardManager, TimeManager timeManager, LeaderboardManager leaderboardManager) {
        super(type, raceManager, settingManager, rewardManager, timeManager, leaderboardManager);
    }


    public void initRandomWord() {

    }

    @Override
    public void beforeRaceStart() {
        initRandomWord();
        super.beforeRaceStart();
    }

}

package be.woutzah.chatbrawl.races.types.chateventbased.typerace;

import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.leaderboard.LeaderboardManager;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.races.types.RaceType;
import be.woutzah.chatbrawl.races.types.chateventbased.ChatEntry;
import be.woutzah.chatbrawl.races.types.chateventbased.ChatRace;
import be.woutzah.chatbrawl.rewards.RewardManager;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.settings.races.TypeRaceSetting;
import be.woutzah.chatbrawl.time.TimeManager;

import java.util.ArrayList;
import java.util.List;

public class TypeRace extends ChatRace {
    public TypeRace(RaceManager raceManager, SettingManager settingManager,
                    RewardManager rewardManager, TimeManager timeManager,
                    LeaderboardManager leaderboardManager) {
        super(RaceType.TYPE, raceManager, settingManager, rewardManager, timeManager, leaderboardManager);
        this.chatEntryList = new ArrayList<>();
        initChatEntryList();
    }

    private void initChatEntryList() {
        settingManager.getConfigSection(TypeRaceSetting.WORDS).getKeys(false).forEach(entry -> {
            String word = settingManager.getString(ConfigType.TYPERACE, "words." + entry + ".word");
            List<Integer> rewardIds = settingManager.getIntegerList(ConfigType.TYPERACE, "words." + entry + ".rewards");
            chatEntryList.add(new ChatEntry(word, List.of(word), rewardIds) {
            });
        });
    }
}
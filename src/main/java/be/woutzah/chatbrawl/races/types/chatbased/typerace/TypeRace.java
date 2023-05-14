package be.woutzah.chatbrawl.races.types.chatbased.typerace;

import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.leaderboard.LeaderboardManager;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.races.types.RaceType;
import be.woutzah.chatbrawl.races.types.chatbased.ChatRace;
import be.woutzah.chatbrawl.rewards.RewardManager;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.settings.races.TypeRaceSetting;
import be.woutzah.chatbrawl.time.TimeManager;
import be.woutzah.chatbrawl.util.Printer;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class TypeRace extends ChatRace {

    private final List<WordToType> wordToTypeList;
    private WordToType wordToType;

    public TypeRace(RaceManager raceManager, SettingManager settingManager,
                    RewardManager rewardManager, TimeManager timeManager,
                    LeaderboardManager leaderboardManager) {
        super(RaceType.TYPE, raceManager, settingManager, rewardManager, timeManager, leaderboardManager);
        this.wordToTypeList = new ArrayList<>();
        initWordToTypeList();
    }

    private void initWordToTypeList() {
        settingManager.getConfigSection(TypeRaceSetting.WORDS).getKeys(false).forEach(entry -> {
            String word = settingManager.getString(ConfigType.TYPERACE, "words." + entry + ".word");
            List<Integer> rewardIds = settingManager.getIntegerList(ConfigType.TYPERACE, "words." + entry + ".rewards");
            wordToTypeList.add(new WordToType(word, rewardIds));
        });
    }

    @Override
    public void initRandomWord() {
        wordToType = wordToTypeList.get(random.nextInt(wordToTypeList.size()));
        Printer.printConsole("&a[ChatBrawl] &fTypeRace word has been set to: &e" + wordToType.getWord() + "&f.");
    }

    @EventHandler
    public void checkWordInChat(AsyncChatEvent e) {
        if (!isActive()) return;
        Player player = e.getPlayer();
        if (raceChecks(player)) return;
        String message = Printer.stripColors(e.originalMessage().toString());
        if (raceManager.startsWithForbiddenCommand(message)) return;
        if (!message.equals(wordToType.getWord())) return;
        onWinning(player);
    }

    @Override
    public String replacePlaceholders(String message) {
        return message.replace("<word>", wordToType.getWord());
    }
}

package be.woutzah.chatbrawl.races.types.chatbased.guessrace;

import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.leaderboard.LeaderboardManager;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.races.types.RaceType;
import be.woutzah.chatbrawl.races.types.chatbased.ChatRace;
import be.woutzah.chatbrawl.rewards.RewardManager;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.settings.races.GuessRaceSetting;
import be.woutzah.chatbrawl.time.TimeManager;
import be.woutzah.chatbrawl.util.Printer;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class GuessRace extends ChatRace {

    private final List<WordToGuess> wordToGuessList;
    private WordToGuess wordToGuess;

    public GuessRace(RaceManager raceManager, SettingManager settingManager,
                     RewardManager rewardManager, TimeManager timeManager,
                     LeaderboardManager leaderboardManager) {
        super(RaceType.GUESS, raceManager, settingManager, rewardManager, timeManager, leaderboardManager);
        this.wordToGuessList = new ArrayList<>();
        initWordToGuessList();
    }

    private void initWordToGuessList() {
        settingManager.getConfigSection(GuessRaceSetting.WORDS).getKeys(false).forEach(entry -> {
            String word = settingManager.getString(ConfigType.GUESSRACE, "words." + entry + ".word");
            List<Integer> rewardIds = settingManager.getIntegerList(ConfigType.GUESSRACE, "words." + entry + ".rewards");
            wordToGuessList.add(new WordToGuess(word, rewardIds));
        });
    }

    @Override
    public void initRandomWord() {
        wordToGuess = wordToGuessList.get(random.nextInt(wordToGuessList.size()));
    }

    @EventHandler
    public void checkWordInChat(AsyncChatEvent e) {
        if (!isActive()) return;
        Player player = e.getPlayer();
        raceChecks(player);
        String message = Printer.stripColors(e.originalMessage().toString());
        if (raceManager.startsWithForbiddenCommand(message)) return;
        if (!message.equals(wordToGuess.getWord())) return;
        onWinning(player);
    }

    @Override
    public String replacePlaceholders(String message) {
        return message.replace("<word>", wordToGuess.getWord());
    }

}

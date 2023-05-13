package be.woutzah.chatbrawl.races.types.chatrace;

import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.leaderboard.LeaderboardManager;
import be.woutzah.chatbrawl.leaderboard.LeaderboardStatistic;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.races.types.Race;
import be.woutzah.chatbrawl.races.types.RaceEntry;
import be.woutzah.chatbrawl.races.types.RaceType;
import be.woutzah.chatbrawl.rewards.RewardManager;
import be.woutzah.chatbrawl.settings.GeneralSetting;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.settings.races.ChatRaceSetting;
import be.woutzah.chatbrawl.time.TimeManager;
import be.woutzah.chatbrawl.util.FireWorkUtil;
import be.woutzah.chatbrawl.util.Printer;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class ChatRace extends Race {

    private final List<WordToGuess> wordToGuessList;
    private WordToGuess wordToGuess;

    public ChatRace(RaceManager raceManager, SettingManager settingManager,
                    RewardManager rewardManager, TimeManager timeManager,
                    LeaderboardManager leaderboardManager) {
        super(RaceType.CHAT, raceManager, settingManager, rewardManager, timeManager, leaderboardManager);
        this.wordToGuessList = new ArrayList<>();
        initWordToGuessList();
    }

    private void initWordToGuessList() {
        settingManager.getConfigSection(ChatRaceSetting.WORDS).getKeys(false).forEach(entry -> {
            String word = settingManager.getString(ConfigType.CHATRACE, "words." + entry + ".word");
            List<Integer> rewardIds = settingManager.getIntegerList(ConfigType.CHATRACE, "words." + entry + ".rewards");
            wordToGuessList.add(new WordToGuess(word, rewardIds));
        });
    }

    public void initRandomWord() {
        wordToGuess = wordToGuessList.get(random.nextInt(wordToGuessList.size()));
    }

    @Override
    public void beforeRaceStart() {
        initRandomWord();
        if (isAnnounceStartEnabled()) announceStart(isCenterMessages());
        if (isBossBarEnabled()) showBossBar();
        if (isActionBarEnabled()) showActionBar();
    }

    @EventHandler
    public void checkWordInChat(AsyncChatEvent e) {
        if (!isActive()) return;
        Player player = e.getPlayer();
        if (!raceManager.isCreativeAllowed()) {
            if (player.getGameMode() == GameMode.CREATIVE) return;
        }
        World world = player.getWorld();
        if (!raceManager.isWorldAllowed(world.getName())) return;
        String message = Printer.stripColors(e.originalMessage().toString());
        if (raceManager.startsWithForbiddenCommand(message)) return;
        if (!message.equals(wordToGuess.getWord())) return;
        //when correct
        afterRaceEnd();
        if (isAnnounceEndEnabled()) announceWinner(isCenterMessages(), player);
        if (isFireWorkEnabled()) FireWorkUtil.shootFireWorkSync(player);
        this.raceTask.cancel();
        rewardManager.executeRandomRewardSync(RaceEntry.getRewardIds(), player);
        if (settingManager.getBoolean(GeneralSetting.MYSQL_ENABLED)) {
            leaderboardManager.addWin(new LeaderboardStatistic(player.getUniqueId(), type, timeManager.getTotalSeconds()));
        }
        Printer.sendMessage(getWinnerPersonal(), player);
    }

    @Override
    public String replacePlaceholders(String message) {
        return message.replace("<word>", wordToGuess.getWord());
    }

}

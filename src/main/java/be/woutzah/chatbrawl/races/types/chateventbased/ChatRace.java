package be.woutzah.chatbrawl.races.types.chateventbased;

import be.woutzah.chatbrawl.leaderboard.LeaderboardManager;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.races.types.Race;
import be.woutzah.chatbrawl.races.types.RaceType;
import be.woutzah.chatbrawl.rewards.RewardManager;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.settings.races.RaceSetting;
import be.woutzah.chatbrawl.time.TimeManager;
import be.woutzah.chatbrawl.util.Printer;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ChatRace extends Race {
    protected ChatEntry chatEntry;
    protected List<ChatEntry> chatEntryList = new ArrayList<>();

    public ChatRace(RaceType type, RaceManager raceManager, SettingManager settingManager, RewardManager rewardManager, TimeManager timeManager, LeaderboardManager leaderboardManager) {
        super(type, raceManager, settingManager, rewardManager, timeManager, leaderboardManager);
    }

    public void initRandomEntry() {
        chatEntry = chatEntryList.get(random.nextInt(chatEntryList.size()));
    }

    public void beforeRaceStart() {
        initRandomEntry();
        super.beforeRaceStart();
    }

    @EventHandler
    public void checkAnswerInChat(AsyncChatEvent e) {
        Printer.printConsole("Answer is checked");
        if (isInactive()) return;
        Player player = e.getPlayer();
        if (raceChecks(player)) return;
        String message = Printer.stripColors(e.originalMessage());
        if (raceManager.startsWithForbiddenCommand(message)) return;
        if (chatEntry.checkAnswer(message)) {
            onWinning(player);
        }
    }

    @Override
    public void announceEnd() {
        List<String> lines = settingManager.getStringList(RaceType.QUIZ, RaceSetting.LANGUAGE_ENDED)
                .stream()
                .map(line -> line.replace("<answer>", chatEntry.getFirstChatEntryAnswer()))
                .collect(Collectors.toList());
        Printer.broadcast(lines);
    }

    @Override
    public String replacePlaceholders(String message) {
        return message
                .replace("<question>", String.valueOf(chatEntry.getChatEntry()))
                .replace("<word>", String.valueOf(chatEntry.getChatEntry()));
    }
}

package be.woutzah.chatbrawl.races.types.chatbased.scramblerace;

import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.leaderboard.LeaderboardManager;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.races.types.RaceType;
import be.woutzah.chatbrawl.races.types.chatbased.ChatRace;
import be.woutzah.chatbrawl.rewards.RewardManager;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.settings.races.RaceSetting;
import be.woutzah.chatbrawl.settings.races.ScrambleRaceSetting;
import be.woutzah.chatbrawl.time.TimeManager;
import be.woutzah.chatbrawl.util.Printer;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ScrambleRace extends ChatRace {

    private final List<ScrambleWord> scrambleWordList;
    private ScrambleWord scrambleWord;

    public ScrambleRace(RaceManager raceManager, SettingManager settingManager,
                        RewardManager rewardManager, TimeManager timeManager,
                        LeaderboardManager leaderboardManager) {
        super(RaceType.SCRAMBLE, raceManager, settingManager, rewardManager, timeManager, leaderboardManager);
        this.scrambleWordList = new ArrayList<>();
        initScrambleWorldList();
    }

    private void initScrambleWorldList() {
        settingManager.getConfigSection(ScrambleRaceSetting.WORDS).getKeys(false).forEach(entry -> {
            String word = settingManager.getString(ConfigType.SCRAMBLERACE, "words." + entry + ".word");
            int difficulty = settingManager.getInt(ConfigType.SCRAMBLERACE, "words." + entry + ".difficulty");
            List<Integer> rewardIds = settingManager.getIntegerList(ConfigType.SCRAMBLERACE, "words." + entry + ".rewards");
            scrambleWordList.add(new ScrambleWord(word, difficulty, rewardIds));
        });
    }

    @Override
    public void initRandomWord() {
        scrambleWord = scrambleWordList.get(random.nextInt(scrambleWordList.size()));
        scrambleWord.setScrambledWord(scramble(scrambleWord.getWord(), scrambleWord.getDifficulty()));
    }

    public String scramble(String word, int difficulty) {
        List<Character> chars = word.chars()
                .mapToObj(e -> (char) e)
                .collect(Collectors.toList());
        Collections.shuffle(chars);
        String shuffledWord = chars.toString()
                .substring(1, 3 * chars.size() - 1)
                .replaceAll(", ", "");
        return switch (difficulty) {
            case 1 -> shuffledWord;
            case 2 -> randomCase(shuffledWord);
            case 3 -> swapInNumbers(randomCase(shuffledWord));
            default -> null;
        };
    }

    private String randomCase(String word) {
        StringBuilder newWord = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            int number = random.nextInt(2);
            if (number == 0) {
                newWord.append(word.substring(i, i + 1).toLowerCase());
            } else {
                newWord.append(word.substring(i, i + 1).toUpperCase());
            }
        }
        return newWord.toString();
    }

    private String swapInNumbers(String word) {
        return word
                // English
                .replace("O", "0").replace("o", "0")
                .replace("I", "1").replace("i", "1")
                .replace("Z", "2").replace("z", "2")
                .replace("E", "3").replace("e", "3")
                .replace("A", "5").replace("a", "5")
                .replace("T", "7").replace("t", "7")
                .replace("B", "8").replace("b", "8")

                //Cyrillic
                .replace("О", "0").replace("о", "0")
                .replace("Е", "3").replace("е", "3")
                .replace("З", "3").replace("з", "3")
                .replace("Ч", "4").replace("ч", "4")
                .replace("А", "5").replace("а", "5")
                .replace("Ъ", "6").replace("ъ", "6")
                .replace("Т", "7").replace("т", "7")
                .replace("Г", "7").replace("г", "7")
                .replace("В", "8").replace("в", "8");
    }

    @EventHandler
    public void checkWordInChat(AsyncChatEvent e) {
        if (!isActive()) return;
        Player player = e.getPlayer();
        if (raceChecks(player)) return;
        String message = Printer.stripColors(e.message().toString());
        if (raceManager.startsWithForbiddenCommand(message)) return;
        if (!message.equals(scrambleWord.getWord())) return;
        onWinning(player);
    }

    @Override
    public String replacePlaceholders(String message) {
        return message.replace("<word>", scrambleWord.getScrambledWord());
    }

    @Override
    public void announceEnd() {
        List<String> lines = settingManager.getStringList(RaceType.SCRAMBLE, RaceSetting.LANGUAGE_ENDED)
                .stream()
                .map(line -> line.replace("<answer>", scrambleWord.getWord()))
                .collect(Collectors.toList());
        Printer.broadcast(lines);
    }
}

package be.woutzah.chatbrawl.races.types.chateventbased;

import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.leaderboard.LeaderboardManager;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.races.types.RaceType;
import be.woutzah.chatbrawl.races.types.chateventbased.ChatRace;
import be.woutzah.chatbrawl.races.types.chateventbased.ScrambleWord;
import be.woutzah.chatbrawl.rewards.RewardManager;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.settings.races.ScrambleRaceSetting;
import be.woutzah.chatbrawl.time.TimeManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ScrambleRace extends ChatRace {

    public ScrambleRace(RaceManager raceManager, SettingManager settingManager,
                        RewardManager rewardManager, TimeManager timeManager,
                        LeaderboardManager leaderboardManager) {
        super(RaceType.SCRAMBLE, raceManager, settingManager, rewardManager, timeManager, leaderboardManager);
        this.chatEntryList = new ArrayList<>();
        initChatEntryList();
    }

    private void initChatEntryList() {
        settingManager.getConfigSection(ScrambleRaceSetting.WORDS).getKeys(false).forEach(entry -> {
            String word = settingManager.getString(ConfigType.SCRAMBLERACE, "words." + entry + ".word");
            List<Integer> rewardIds = settingManager.getIntegerList(ConfigType.SCRAMBLERACE, "words." + entry + ".rewards");
            int difficulty = settingManager.getInt(ConfigType.SCRAMBLERACE, "words." + entry + ".difficulty");
            chatEntryList.add(new ScrambleWord(word, List.of(word), rewardIds, difficulty));
        });
    }

    @Override
    public void initRandomEntry() {
        chatEntry = chatEntryList.get(random.nextInt(chatEntryList.size()));
        chatEntry.setChatEntry(scramble(chatEntry.getChatEntry(), settingManager.getInt(ConfigType.SCRAMBLERACE, "words." + (chatEntryList.indexOf(chatEntry) + 1) + ".difficulty")));
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
}

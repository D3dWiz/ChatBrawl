package be.woutzah.chatbrawl.races.types.quizrace;

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
import be.woutzah.chatbrawl.settings.races.QuizRaceSetting;
import be.woutzah.chatbrawl.settings.races.RaceSetting;
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
import java.util.stream.Collectors;

public class QuizRace extends Race {
    private final List<Question> questionList;
    private Question question;

    public QuizRace(RaceManager raceManager, SettingManager settingManager,
                    RewardManager rewardManager, TimeManager timeManager,
                    LeaderboardManager leaderboardManager) {
        super(RaceType.QUIZ, raceManager, settingManager, rewardManager, timeManager, leaderboardManager);
        this.questionList = new ArrayList<>();
        initQuestionsList();
    }

    private void initQuestionsList() {
        settingManager.getConfigSection(QuizRaceSetting.QUESTIONS).getKeys(false).forEach(entry -> {
            String question = settingManager.getString(ConfigType.QUIZRACE, "questions." + entry + ".question");
            List<String> answers = settingManager.getStringList(ConfigType.QUIZRACE, "questions." + entry + ".answer");
            List<Integer> rewardIds = settingManager.getIntegerList(ConfigType.QUIZRACE, "questions." + entry + ".rewards");
            questionList.add(new Question(question, answers, rewardIds));
        });
    }

    public void initRandomWord() {
        question = questionList.get(random.nextInt(questionList.size()));
    }


    @EventHandler
    public void checkAnswerInChat(AsyncChatEvent e) {
        //do checks
        if (!isActive()) return;
        Player player = e.getPlayer();
        if (!raceManager.isCreativeAllowed()) {
            if (player.getGameMode() == GameMode.CREATIVE) return;
        }
        World world = player.getWorld();
        if (!raceManager.isWorldAllowed(world.getName())) return;
        String message = Printer.stripColors(e.originalMessage().toString());
        if (raceManager.startsWithForbiddenCommand(message)) return;
        if (question.getAnswers().stream().anyMatch(a -> a.equalsIgnoreCase(message))) {
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
    }

    @Override
    public void announceWinner(boolean center, Player player) {
        List<String> messageList = settingManager.getStringList(RaceType.QUIZ, RaceSetting.LANGUAGE_WINNER)
                .stream()
                .map(this::replacePlaceholders)
                .map(s -> s.replace("<displayname>", player.displayName().toString()))
                .map(s -> s.replace("<player>", player.getName()))
                .map(s -> s.replace("<time>", timeManager.getTimeString()))
                .map(s -> s.replace("<answer>", question.getAnswers().get(0)))
                .collect(Collectors.toList());
        if (center) {
            Printer.broadcast(Printer.centerMessage(messageList));
            return;
        }
        Printer.broadcast(messageList);
    }

    @Override
    public String replacePlaceholders(String message) {
        return message.replace("<question>", question.getQuestion());
    }

    @Override
    public void beforeRaceStart() {
        initRandomWord();
        if (isAnnounceStartEnabled()) announceStart(isCenterMessages());
        if (isBossBarEnabled()) showBossBar();
        if (isActionBarEnabled()) showActionBar();
    }

    @Override
    public void announceEnd() {
        List<String> lines = settingManager.getStringList(RaceType.QUIZ, RaceSetting.LANGUAGE_ENDED)
                .stream()
                .map(line -> line.replace("<answer>", question.getAnswers().get(0)))
                .collect(Collectors.toList());
        Printer.broadcast(lines);
    }
}

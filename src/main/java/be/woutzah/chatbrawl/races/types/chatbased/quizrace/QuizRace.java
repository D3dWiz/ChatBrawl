package be.woutzah.chatbrawl.races.types.chatbased.quizrace;

import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.leaderboard.LeaderboardManager;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.races.types.RaceType;
import be.woutzah.chatbrawl.races.types.chatbased.ChatRace;
import be.woutzah.chatbrawl.rewards.RewardManager;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.settings.races.QuizRaceSetting;
import be.woutzah.chatbrawl.settings.races.RaceSetting;
import be.woutzah.chatbrawl.time.TimeManager;
import be.woutzah.chatbrawl.util.Printer;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class QuizRace extends ChatRace {
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

    @Override
    public void initRandomWord() {
        question = questionList.get(random.nextInt(questionList.size()));
    }


    @EventHandler
    public void checkAnswerInChat(AsyncChatEvent e) {
        if (!isActive()) return;
        Player player = e.getPlayer();
        raceChecks(player);
        String message = Printer.stripColors(e.originalMessage().toString());
        if (raceManager.startsWithForbiddenCommand(message)) return;
        if (question.getAnswers().stream().anyMatch(a -> a.equalsIgnoreCase(message))) {
            onWinning(player);
        }
    }

    @Override
    public String replacePlaceholders(String message) {
        return message.replace("<question>", question.getQuestion());
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

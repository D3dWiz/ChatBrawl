package be.woutzah.chatbrawl.rewards;

import be.woutzah.chatbrawl.ChatBrawl;
import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.settings.RewardSetting;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.util.Printer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RewardManager {

    private final List<Reward> rewardsList;
    private final SettingManager settingManager;
    private final Random random;

    public RewardManager(ChatBrawl plugin) {
        this.random = new Random();
        this.settingManager = plugin.getSettingManager();
        rewardsList = new ArrayList<>();
        initRewards();
    }

    private void initRewards() {
        settingManager.getConfigSection(RewardSetting.REWARDS).getKeys(false).forEach(entry -> {
            int chance = settingManager.getInt(ConfigType.REWARDS, "rewards." + entry + ".chance");
            String broadcast = settingManager.getString(ConfigType.REWARDS, "rewards." + entry + ".broadcast");
            String title = settingManager.getString(ConfigType.REWARDS, "rewards." + entry + ".title");
            String subTitle = settingManager.getString(ConfigType.REWARDS, "rewards." + entry + ".subtitle");
            List<String> commands = settingManager.getStringList(ConfigType.REWARDS, "rewards." + entry + ".commands");
            rewardsList.add(new Reward(entry, chance, broadcast, title, subTitle, commands));
        });
    }

    public void executeRandomReward(List<Integer> selectedRewardIds, Player player) {
        List<Reward> selectedRewards = rewardsList
                .stream()
                .filter(r -> selectedRewardIds
                        .contains(Integer.valueOf(r.getId())))
                .collect(Collectors.toList());
        int totalChance = selectedRewards.stream().mapToInt(Reward::getChance).sum();
        int index = random.nextInt(totalChance);
        int sum = 0;
        int i = 0;
        while (sum < index) {
            sum += selectedRewards.get(i++).getChance();
        }
        Reward selectedReward = selectedRewards.get(Math.max(0, i - 1));
        selectedReward.getCommands().forEach(command -> {
            command = command
                    .replace("<player>", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        });
        Printer.broadcast(selectedReward.getBroadcast()
                .replace("<player>", player.getName()));
        Title title = Title.title(Component.text(Printer.parseColor(selectedReward.getTitle())),
                Component.text(Printer.parseColor(selectedReward.getSubtitle())), Title.Times.times(Duration.ofMillis(10), Duration.ofMillis(70), Duration.ofMillis(20)));
        player.showTitle(title);
    }

    public void executeRandomRewardSync(List<Integer> selectedRewardIds, Player player) {
        Bukkit.getScheduler().runTask(ChatBrawl.getInstance(), () -> {
            executeRandomReward(selectedRewardIds, player);
        });
    }

    private String replacePlaceholders(String input, Player player) {
        return input.replace("<player>", player.getName())
                .replace("<displayname>", player.getName());
    }
}

package be.woutzah.chatbrawl.races.types.craftrace;

import be.woutzah.chatbrawl.ChatBrawl;
import be.woutzah.chatbrawl.contestants.ContestantsManager;
import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.leaderboard.LeaderboardManager;
import be.woutzah.chatbrawl.leaderboard.LeaderboardStatistic;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.races.types.ContestantRace;
import be.woutzah.chatbrawl.races.types.RaceType;
import be.woutzah.chatbrawl.rewards.RewardManager;
import be.woutzah.chatbrawl.settings.GeneralSetting;
import be.woutzah.chatbrawl.settings.LanguageSetting;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.settings.races.CraftRaceSetting;
import be.woutzah.chatbrawl.settings.races.RaceSetting;
import be.woutzah.chatbrawl.time.TimeManager;
import be.woutzah.chatbrawl.util.ErrorHandler;
import be.woutzah.chatbrawl.util.FireWorkUtil;
import be.woutzah.chatbrawl.util.Printer;
import com.meowj.langutils.lang.LanguageHelper;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class CraftRace extends ContestantRace {

    private final List<CraftEntry> craftEntryList;
    private CraftEntry craftEntry;

    public CraftRace(RaceManager raceManager, SettingManager settingManager,
                     RewardManager rewardManager, TimeManager timeManager,
                     ContestantsManager contestantsManager, LeaderboardManager leaderboardManager) {
        super(RaceType.CRAFT, raceManager, settingManager, rewardManager, timeManager, contestantsManager,leaderboardManager);
        this.craftEntryList = new ArrayList<>();
        initCraftEntryList();
    }

    private void initCraftEntryList() {
        settingManager.getConfigSection(CraftRaceSetting.ITEMS).getKeys(false).forEach(entry -> {
            Material material = null;
            try {
                material = Material.valueOf(settingManager.getString(ConfigType.CRAFTRACE, "items." + entry + ".item").toUpperCase());
            } catch (Exception exception) {
                ErrorHandler.error("craftrace: craftentry " + entry + " has an invalid material!");
            }
            int amount = settingManager.getInt(ConfigType.CRAFTRACE, "items." + entry + ".amount");
            List<Integer> rewardIds = settingManager.getIntegerList(ConfigType.CRAFTRACE, "items." + entry + ".rewards");
            craftEntryList.add(new CraftEntry(material, amount, rewardIds));
        });
    }

    public void initRandomCraftEntry() {
        craftEntry = craftEntryList.get(random.nextInt(craftEntryList.size()));
    }

    @Override
    public void announceStart(boolean center) {
        List<String> messageList = settingManager.getStringList(RaceType.CRAFT, RaceSetting.LANGUAGE_START)
                .stream()
                .map(this::replacePlaceholders)
                .collect(Collectors.toList());
        if (center) {
            Printer.broadcast(Printer.centerMessage(messageList));
            return;
        }
        Printer.broadcast(messageList);
    }

    @Override
    public void sendStart(Player player) {
        List<String> messageList = settingManager.getStringList(RaceType.CRAFT, RaceSetting.LANGUAGE_START)
                .stream()
                .map(this::replacePlaceholders)
                .collect(Collectors.toList());
        if (isCenterMessages()) {
            Printer.sendMessage(Printer.centerMessage(messageList),player);
            return;
        }
        Printer.sendMessage(messageList,player);
    }

    @EventHandler(ignoreCancelled = true)
    public void checkCraftedItems(CraftItemEvent e) {
        //do checks
        if (!isActive()) return;
        Player player = (Player) e.getWhoClicked();
        if (!raceManager.isCreativeAllowed()) {
            if (player.getGameMode() == GameMode.CREATIVE) return;
        }
        World world = player.getWorld();
        if (!raceManager.isWorldAllowed(world.getName())) return;
        if (!(e.getWhoClicked().getInventory().firstEmpty() == -1)) {
            if (e.getSlotType() == InventoryType.SlotType.RESULT) {
                ItemStack craftedItemStack;
                if (e.getClick().isShiftClick()) {
                    int amount = Arrays.stream(e.getInventory().getMatrix())
                            .filter(Objects::nonNull)
                            .mapToInt(ItemStack::getAmount)
                            .min()
                            .orElse(0);
                    craftedItemStack = new ItemStack(Objects.requireNonNull(e.getCurrentItem()).getType(),
                            amount * e.getCurrentItem().getAmount());
                } else {
                    craftedItemStack = e.getCurrentItem();
                }
                if (craftedItemStack == null) return;
                if (craftedItemStack.getType().equals(craftEntry.getMaterial())) {
                    UUID uuid = player.getUniqueId();
                    contestantsManager.addScore(uuid,craftedItemStack.getAmount());
                    if (contestantsManager.hasWon(uuid, craftEntry.getAmount())) {
                        //when correct
                        afterRaceEnd();
                        if (isAnnounceEndEnabled()) announceWinner(isCenterMessages(), player);
                        if (isFireWorkEnabled()) FireWorkUtil.shootFireWorkSync(player);
                        this.raceTask.cancel();
                        rewardManager.executeRandomRewardSync(craftEntry.getRewardIds(), player);
                        if (settingManager.getBoolean(GeneralSetting.MYSQL_ENABLED)) {
                            leaderboardManager.addWin(new LeaderboardStatistic(player.getUniqueId(), type, timeManager.getTotalSeconds()));
                        }
                        Printer.sendMessage(getWinnerPersonal(), player);
                        contestantsManager.removeOnlinePlayers();
                    }
                }
            }
        }
    }


    @Override
    public void announceWinner(boolean center, Player player) {
        List<String> messageList = settingManager.getStringList(RaceType.CRAFT, RaceSetting.LANGUAGE_WINNER)
                .stream()
                .map(this::replacePlaceholders)
                .map(s -> s.replace("<displayname>", player.getDisplayName()))
                .map(s -> s.replace("<player>", player.getName()))
                .map(s -> s.replace("<time>", timeManager.getTimeString()))
                .collect(Collectors.toList());
        if (center) {
            Printer.broadcast(Printer.centerMessage(messageList));
            return;
        }
        Printer.broadcast(messageList);
    }

    @Override
    public void showBossBar() {

    }

    @Override
    public void showActionBar() {
        String message = replacePlaceholders(settingManager.getString(RaceType.CRAFT, RaceSetting.LANGUAGE_ACTIONBAR));
        this.actionBarTask = new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(p -> p.spigot()
                        .sendMessage(ChatMessageType.ACTION_BAR,
                                new TextComponent(Printer.parseColor(message))));
            }
        }.runTaskTimer(ChatBrawl.getInstance(), 0, 20);
    }

    @Override
    public String replacePlaceholders(String message) {
        return message.replace("<item>", ChatBrawl.isLangUtilsIsEnabled() ?
                LanguageHelper.getItemName(new ItemStack(craftEntry.getMaterial()), settingManager.getString(LanguageSetting.LANG))
                :craftEntry.getMaterial().toString().toLowerCase().replace("_", " "))
                .replace("<amount>", String.valueOf(craftEntry.getAmount()));
    }

    @Override
    public void beforeRaceStart() {
        super.beforeRaceStart();
        initRandomCraftEntry();
        if (isAnnounceStartEnabled()) announceStart(isCenterMessages());
        if (isActionBarEnabled()) showActionBar();
    }
}

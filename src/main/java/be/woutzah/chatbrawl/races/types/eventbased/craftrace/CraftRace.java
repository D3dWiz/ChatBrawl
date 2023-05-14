package be.woutzah.chatbrawl.races.types.eventbased.craftrace;

import be.woutzah.chatbrawl.contestants.ContestantsManager;
import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.leaderboard.LeaderboardManager;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.races.types.ContestantRace;
import be.woutzah.chatbrawl.races.types.RaceType;
import be.woutzah.chatbrawl.rewards.RewardManager;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.settings.races.CraftRaceSetting;
import be.woutzah.chatbrawl.time.TimeManager;
import be.woutzah.chatbrawl.util.ErrorHandler;
import be.woutzah.chatbrawl.util.Printer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CraftRace extends ContestantRace {

    private final List<CraftEntry> craftEntryList;
    private CraftEntry craftEntry;

    public CraftRace(RaceManager raceManager, SettingManager settingManager,
                     RewardManager rewardManager, TimeManager timeManager,
                     ContestantsManager contestantsManager, LeaderboardManager leaderboardManager) {
        super(RaceType.CRAFT, raceManager, settingManager, rewardManager, timeManager, contestantsManager, leaderboardManager);
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


    @EventHandler(ignoreCancelled = true)
    public void checkCraftedItems(CraftItemEvent e) {
        if (!isActive()) return;
        Player player = (Player) e.getWhoClicked();
        if (raceChecks(player)) return;
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
                    contestantsManager.addScore(uuid, craftedItemStack.getAmount());
                    if (contestantsManager.hasWon(uuid, craftEntry.getAmount())) {
                        onWinning(player);
                        contestantsManager.removeOnlinePlayers();
                    }
                }
            }
        }
    }

    @Override
    public String replacePlaceholders(String message) {
        return message.replace("<item>", Printer.capitalize(craftEntry.getMaterial().toString().toLowerCase().replace("_", " ")))
                .replace("<amount>", String.valueOf(craftEntry.getAmount()));
    }

    @Override
    public void beforeRaceStart() {
        initRandomCraftEntry();
        super.beforeRaceStart();
    }
}

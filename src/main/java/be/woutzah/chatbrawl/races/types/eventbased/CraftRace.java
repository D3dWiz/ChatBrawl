package be.woutzah.chatbrawl.races.types.eventbased;

import be.woutzah.chatbrawl.contestants.ContestantsManager;
import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.leaderboard.LeaderboardManager;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.races.types.RaceType;
import be.woutzah.chatbrawl.rewards.RewardManager;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.settings.races.CraftRaceSetting;
import be.woutzah.chatbrawl.time.TimeManager;
import be.woutzah.chatbrawl.util.ErrorHandler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CraftRace extends EventRace {
    public CraftRace(RaceManager raceManager, SettingManager settingManager,
                     RewardManager rewardManager, TimeManager timeManager,
                     ContestantsManager contestantsManager, LeaderboardManager leaderboardManager) {
        super(RaceType.CRAFT, raceManager, settingManager, rewardManager, timeManager, contestantsManager, leaderboardManager);
        initEventEntryList();
    }

    private void initEventEntryList() {
        settingManager.getConfigSection(CraftRaceSetting.ITEMS).getKeys(false).forEach(entry -> {
            Material material = null;
            try {
                material = Material.valueOf(settingManager.getString(ConfigType.CRAFTRACE, "items." + entry + ".item").toUpperCase());
            } catch (Exception exception) {
                ErrorHandler.error("craftrace: craftentry " + entry + " has an invalid material!");
            }
            int amount = settingManager.getInt(ConfigType.CRAFTRACE, "items." + entry + ".amount");
            List<Integer> rewardIds = settingManager.getIntegerList(ConfigType.CRAFTRACE, "items." + entry + ".rewards");
            eventEntryList.add(new EventEntry<>(material, rewardIds, amount));
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void checkCraftedItems(CraftItemEvent e) {
        if (isInactive()) return;
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
                if (eventEntryList.stream().anyMatch(s -> s.getMaterial().equals(craftedItemStack.getType()))) {
                    UUID uuid = player.getUniqueId();
                    contestantsManager.addScore(uuid, craftedItemStack.getAmount());
                    if (contestantsManager.hasWon(uuid, eventEntry.getAmount())) {
                        onWinning(player);
                        contestantsManager.removeOnlinePlayers();
                    }
                }
            }
        }
    }
}

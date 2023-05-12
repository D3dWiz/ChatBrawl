package be.woutzah.chatbrawl.races.types.huntrace;

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
import be.woutzah.chatbrawl.settings.races.HuntRaceSetting;
import be.woutzah.chatbrawl.settings.races.RaceSetting;
import be.woutzah.chatbrawl.time.TimeManager;
import be.woutzah.chatbrawl.util.ErrorHandler;
import be.woutzah.chatbrawl.util.FireWorkUtil;
import be.woutzah.chatbrawl.util.Printer;
import com.meowj.langutils.lang.LanguageHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class HuntRace extends ContestantRace {


	private final List<HuntEntry> huntEntryList;
	private HuntEntry huntEntry;

	public HuntRace(RaceManager raceManager, SettingManager settingManager,
					RewardManager rewardManager, TimeManager timeManager,
					ContestantsManager contestantsManager, LeaderboardManager leaderboardManager) {
		super(RaceType.HUNT, raceManager, settingManager, rewardManager, timeManager, contestantsManager, leaderboardManager);
		this.huntEntryList = new ArrayList<>();
		initHuntEntryList();
	}

	private void initHuntEntryList() {
		settingManager.getConfigSection(HuntRaceSetting.MOBS).getKeys(false).forEach(entry -> {
			EntityType entityType = null;
			try {
				entityType = EntityType.valueOf(settingManager.getString(ConfigType.HUNTRACE, "mobs." + entry + ".mob").toUpperCase());
			} catch (Exception exception) {
				ErrorHandler.error("huntrace: huntentry " + entry + " has an invalid entitytype!");
			}
			int amount = settingManager.getInt(ConfigType.HUNTRACE, "mobs." + entry + ".amount");
			List<Integer> rewardIds = settingManager.getIntegerList(ConfigType.HUNTRACE, "mobs." + entry + ".rewards");
			huntEntryList.add(new HuntEntry(entityType, amount, rewardIds));
		});
	}

	public void initRandomHuntEntry() {
		huntEntry = huntEntryList.get(random.nextInt(huntEntryList.size()));
	}

	@Override
	public void announceStart(boolean center) {
		List<String> messageList = settingManager.getStringList(RaceType.HUNT, RaceSetting.LANGUAGE_START)
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
		List<String> messageList = settingManager.getStringList(RaceType.HUNT, RaceSetting.LANGUAGE_START)
				.stream()
				.map(this::replacePlaceholders)
				.collect(Collectors.toList());
		if (isCenterMessages()) {
			Printer.sendMessage(Printer.centerMessage(messageList), player);
			return;
		}
		Printer.sendMessage(messageList, player);
	}

	@EventHandler
	public void checkMobsKilled(EntityDeathEvent e) {
		//do checks
		if (!isActive()) return;
		Player player = e.getEntity().getKiller();
		if (player == null) return;
		if (!raceManager.isCreativeAllowed()) {
			if (player.getGameMode() == GameMode.CREATIVE) return;
		}
		World world = player.getWorld();
		if (!raceManager.isWorldAllowed(world.getName())) return;
		EntityType killedEntityType = e.getEntity().getType();
		if (killedEntityType.equals(huntEntry.getEntityType())) {
			UUID uuid = player.getUniqueId();
			contestantsManager.addScore(uuid);
			if (contestantsManager.hasWon(uuid, huntEntry.getAmount())) {
				//when correct
				afterRaceEnd();
				if (isAnnounceEndEnabled()) announceWinner(isCenterMessages(), player);
				if (isFireWorkEnabled()) FireWorkUtil.shootFireWorkSync(player);
				this.raceTask.cancel();
				rewardManager.executeRandomRewardSync(huntEntry.getRewardIds(), player);
				if (settingManager.getBoolean(GeneralSetting.MYSQL_ENABLED)) {
					leaderboardManager.addWin(new LeaderboardStatistic(player.getUniqueId(), type, timeManager.getTotalSeconds()));
				}
				Printer.sendMessage(getWinnerPersonal(), player);
				contestantsManager.removeOnlinePlayers();
			}
		}
	}

	@Override
	public void announceWinner(boolean center, Player player) {
		List<String> messageList = settingManager.getStringList(RaceType.HUNT, RaceSetting.LANGUAGE_WINNER)
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
		Component message = LegacyComponentSerializer.legacyAmpersand().deserialize(replacePlaceholders(settingManager.getString(RaceType.HUNT, RaceSetting.LANGUAGE_ACTIONBAR)));
		this.actionBarTask = new BukkitRunnable() {
			@Override
			public void run() {
				Bukkit.getServer().sendActionBar(message);
			}
		}.runTaskTimer(ChatBrawl.getInstance(), 0, 20);
	}

	@Override
	public String replacePlaceholders(String message) {
		return message.replace("<mob>", ChatBrawl.isLangUtilsIsEnabled() ?
				LanguageHelper.getEntityName(huntEntry.getEntityType(), settingManager.getString(LanguageSetting.LANG))
				: huntEntry.getEntityType().toString().toLowerCase().replace("_", " "))
				.replace("<amount>", String.valueOf(huntEntry.getAmount()));
	}

	@Override
	public void beforeRaceStart() {
		super.beforeRaceStart();
		initRandomHuntEntry();
		if (isAnnounceStartEnabled()) announceStart(isCenterMessages());
		if (isActionBarEnabled()) showActionBar();
	}
}

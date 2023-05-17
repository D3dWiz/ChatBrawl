package be.woutzah.chatbrawl.commands.subcommands;

import be.woutzah.chatbrawl.ChatBrawl;
import be.woutzah.chatbrawl.leaderboard.LeaderboardManager;
import be.woutzah.chatbrawl.races.types.RaceType;
import be.woutzah.chatbrawl.settings.GeneralSetting;
import be.woutzah.chatbrawl.settings.LanguageSetting;
import be.woutzah.chatbrawl.settings.LeaderboardSetting;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.time.TimeManager;
import be.woutzah.chatbrawl.util.Printer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Objects;

public class LeaderboardCommand extends SubCommand {

    private final LeaderboardManager leaderboardManager;
    private final SettingManager settingManager;
    private final TimeManager timeManager;

    public LeaderboardCommand(ChatBrawl plugin) {
        super("leaderboard", "cb.leaderboard", new ArrayList<>(), false);
        this.leaderboardManager = plugin.getLeaderboardManager();
        this.settingManager = plugin.getSettingManager();
        this.timeManager = plugin.getTimeManager();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (leaderboardManager == null) {
            Printer.sendParsedMessage(settingManager.getString(GeneralSetting.PLUGIN_PREFIX) +
                    "<red>MySQL has not been configured!", player);
            return;
        }
        if (args.length <= 0) {
            Printer.sendParsedMessage(settingManager.getString(GeneralSetting.PLUGIN_PREFIX) +
                    settingManager.getString(LanguageSetting.LEADERBOARD_TYPE), player);
            return;
        }
        switch (args[0].toUpperCase()) {
            case "TOTAL":
                leaderboardManager.getMostTotalWins().thenAccept(winsUsers -> {
                    StringBuilder sb = new StringBuilder();
                    settingManager.getStringList(LeaderboardSetting.TOTAL_HEADER).forEach(sb::append);
                    winsUsers.forEach(user -> sb.append(settingManager.getString(LeaderboardSetting.TOTAL_ENTRY)
                            .replace("<rank>", String.valueOf(user.getRank()))
                            .replace("<player>", Objects.requireNonNull(Bukkit.getOfflinePlayer(user.getUuid()).getName()))
                            .replace("<wins>", String.valueOf(user.getWins()))));
                    settingManager.getStringList(LeaderboardSetting.TOTAL_FOOTER).forEach(sb::append);
                    Printer.sendParsedMessage(sb.toString(), player);
                });
                return;
            case "RACE":
                if (args.length < 2) {
                    Printer.sendParsedMessage(settingManager.getString(GeneralSetting.PLUGIN_PREFIX) +
                            settingManager.getString(LanguageSetting.LEADERBOARD_RACE), player);
                    return;
                }
                if (args.length < 3) {
                    Printer.sendParsedMessage(settingManager.getString(GeneralSetting.PLUGIN_PREFIX) +
                            settingManager.getString(LanguageSetting.LEADERBOARD_SUBTYPE), player);
                    return;
                }
                RaceType raceType = null;
                try {
                    raceType = RaceType.valueOf(args[1].toUpperCase());
                } catch (Exception ex) {
                    Printer.sendParsedMessage(settingManager.getString(GeneralSetting.PLUGIN_PREFIX) +
                            settingManager.getString(LanguageSetting.LEADERBOARD_VALID_RACE), player);
                    return;
                }
                RaceType finalRaceType = raceType;
                switch (args[2].toUpperCase()) {
                    case "WINS":
                        leaderboardManager.getMostTotalWinsForRace(raceType).thenAccept(winsUsers -> {
                            StringBuilder sb = new StringBuilder();
                            settingManager.getStringList(LeaderboardSetting.TOTALRACE_HEADER).forEach(s -> sb.append(s
                                    .replace("<race>", Printer.capitalize(finalRaceType.toString().toLowerCase()) + " Race")));
                            winsUsers.forEach(user -> sb.append(settingManager.getString(LeaderboardSetting.TOTALRACE_ENTRY)
                                    .replace("<rank>", String.valueOf(user.getRank()))
                                    .replace("<player>", Objects.requireNonNull(Bukkit.getOfflinePlayer(user.getUuid()).getName()))
                                    .replace("<wins>", String.valueOf(user.getWins()))));
                            settingManager.getStringList(LeaderboardSetting.TOTALRACE_FOOTER).forEach(sb::append);
                            Printer.sendParsedMessage(sb.toString(), player);
                        });
                        return;
                    case "TIME":
                        leaderboardManager.getFastestTimeForRace(raceType).thenAccept(timeUsers -> {
                            StringBuilder sb = new StringBuilder();
                            settingManager.getStringList(LeaderboardSetting.FASTESTRACE_HEADER).forEach(s -> sb.append(s
                                    .replace("<race>", Printer.capitalize(finalRaceType.toString().toLowerCase()) + " Race")));
                            timeUsers.forEach(user -> sb.append(settingManager.getString(LeaderboardSetting.FASTESTRACE_ENTRY)
                                    .replace("<rank>", String.valueOf(user.getRank()))
                                    .replace("<player>", Objects.requireNonNull(Bukkit.getOfflinePlayer(user.getUuid()).getName()))
                                    .replace("<time>", timeManager.formatTime(user.getSeconds()))));
                            settingManager.getStringList(LeaderboardSetting.FASTESTRACE_FOOTER).forEach(sb::append);
                            Printer.sendParsedMessage(sb.toString(), player);
                        });
                        return;
                }
            case "USER":
                if (args.length < 2) {
                    Printer.sendParsedMessage(settingManager.getString(GeneralSetting.PLUGIN_PREFIX) +
                            settingManager.getString(LanguageSetting.LEADERBOARD_USER), player);
                    return;
                }
                if (args.length < 3) {
                    Printer.sendParsedMessage(settingManager.getString(GeneralSetting.PLUGIN_PREFIX) +
                            settingManager.getString(LanguageSetting.LEADERBOARD_SUBTYPE), player);
                    return;
                }
                OfflinePlayer offlinePlayer;
                if (!Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore()) {
                    Printer.sendParsedMessage(settingManager.getString(GeneralSetting.PLUGIN_PREFIX) +
                            settingManager.getString(LanguageSetting.LEADERBOARD_NOT_PLAYED_BEFORE), player);
                    return;
                }
                offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                switch (args[2].toUpperCase()) {
                    case "WINS":
                        leaderboardManager.getMostWinsForUser(offlinePlayer.getUniqueId()).thenAccept(raceWinsMap -> {
                            StringBuilder sb = new StringBuilder();
                            settingManager.getStringList(LeaderboardSetting.TOTALUSER_HEADER).forEach(s -> sb.append(s
                                    .replace("<user>", Objects.requireNonNull(offlinePlayer.getName()))));
                            raceWinsMap.keySet().forEach(race -> sb.append(settingManager.getString(LeaderboardSetting.TOTALUSER_ENTRY)
                                    .replace("<race>", Printer.capitalize(race.toString().toLowerCase()) + " Race")
                                    .replace("<wins>", String.valueOf(raceWinsMap.get(race)))));
                            settingManager.getStringList(LeaderboardSetting.TOTALUSER_FOOTER).forEach(sb::append);
                            Printer.sendParsedMessage(sb.toString(), player);
                        });
                        return;
                    case "TIME":
                        leaderboardManager.getFastestTimeForUser(offlinePlayer.getUniqueId()).thenAccept(fastestTimesMap -> {
                            StringBuilder sb = new StringBuilder();
                            settingManager.getStringList(LeaderboardSetting.FASTESTUSER_HEADER).forEach(s -> sb.append(s
                                    .replace("<user>", Objects.requireNonNull(offlinePlayer.getName()))));
                            fastestTimesMap.keySet().forEach(race -> sb.append(settingManager.getString(LeaderboardSetting.FASTESTUSER_ENTRY)
                                    .replace("<race>", Printer.capitalize(race.toString().toLowerCase()) + " Race")
                                    .replace("<time>", timeManager.formatTime(fastestTimesMap.get(race)))));
                            settingManager.getStringList(LeaderboardSetting.FASTESTUSER_FOOTER).forEach(sb::append);
                            Printer.sendParsedMessage(sb.toString(), player);
                        });
                        return;
                    default:
                        Printer.sendParsedMessage(settingManager.getString(GeneralSetting.PLUGIN_PREFIX) +
                                settingManager.getString(LanguageSetting.LEADERBOARD_VALID_SUBTYPE), player);
                        return;
                }
            default:
                Printer.sendParsedMessage(settingManager.getString(GeneralSetting.PLUGIN_PREFIX) +
                        settingManager.getString(LanguageSetting.LEADERBOARD_VALID_TYPE), player);
        }
    }
}

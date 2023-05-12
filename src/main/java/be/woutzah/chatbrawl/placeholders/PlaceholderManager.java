package be.woutzah.chatbrawl.placeholders;

import be.woutzah.chatbrawl.ChatBrawl;
import be.woutzah.chatbrawl.leaderboard.LeaderboardManager;
import be.woutzah.chatbrawl.leaderboard.users.WinsUser;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.races.types.RaceType;
import be.woutzah.chatbrawl.time.TimeManager;
import be.woutzah.chatbrawl.util.Printer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class PlaceholderManager extends PlaceholderExpansion {

    //current race
    //leaderboard placeholders x5
    //

    private ChatBrawl plugin;
    private RaceManager raceManager;
    private LeaderboardManager leaderboardManager;
    private TimeManager timeManager;

    private List<WinsUser> totalWinsList;

    public PlaceholderManager(ChatBrawl plugin) {
        this.plugin = plugin;
        this.raceManager = plugin.getRaceManager();
        this.leaderboardManager = plugin.getLeaderboardManager();
        this.timeManager = plugin.getTimeManager();
        this.totalWinsList = new ArrayList<>();
        updateCache();
        new BukkitRunnable() {
            @Override
            public void run() {
                updateCache();
            }
        }.runTaskTimer(plugin, 0, 6000);
    }

    public void updateCache() {
        if (leaderboardManager == null) return;
        leaderboardManager.getMostTotalWins().thenAccept(winsUsers -> {
            totalWinsList = winsUsers;
        });
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        AtomicReference<String> atomicString =
                new AtomicReference<String>("loading...");
        if (player == null) {
            return "";
        }

        String[] arguments = identifier.split("_");
        if (arguments.length == 0) return "§cwrong format!";
        if (arguments[0].equalsIgnoreCase("currentrace")) {
            RaceType raceType = raceManager.getCurrentRunningRace();
            return raceType == RaceType.NONE ? "None" : Printer.capitalize(raceType.toString().toLowerCase());
        }
        if (arguments.length != 4) return "§cwrong format!";
        if (arguments[0].equalsIgnoreCase("lb")) {
            if (arguments[1].equalsIgnoreCase("total")) {
                int rank;
                try {
                    rank = Integer.parseInt(arguments[2]);
                } catch (Exception ex) {
                    return "§cnot a number!";
                }
                if (rank < 1 || rank > 10) return "§crank needs to be between 1 - 10";

                switch (arguments[3].toUpperCase()) {
                    case "USER":
                        if (totalWinsList.size() == 0) return "§eFetching";
                        return Bukkit.getOfflinePlayer(totalWinsList.get(rank - 1).getUuid()).getName();
                    case "AMOUNT":
                        if (totalWinsList.size() == 0) return "§eFetching";
                        return String.valueOf(totalWinsList.get(rank - 1).getWins());
                    default:
                        return "§cwrong format";
                }
            }
//            RaceType raceType;
//            try {
//                raceType = RaceType.valueOf(arguments[1].toUpperCase());
//            } catch (Exception ex) {
//                return "§cinvalid racetype";
//            }
//            int rank;
//            try {
//                rank = Integer.parseInt(arguments[3]);
//            } catch (Exception ex) {
//                return "§cnot a number!";
//            }
//            if (rank < 1 || rank > 10) return "§crank needs to be between 1 - 10";
//            switch (arguments[2].toUpperCase()) {
//                case "TIME":
//                        switch (arguments[4].toUpperCase()) {
//                            case "USER":
//                                return Bukkit.getOfflinePlayer(timeUsers.get(rank - 1).getUuid()).getName();
//                            case "AMOUNT":
//                                return timeManager.formatTime(timeUsers.get(rank - 1).getSeconds());
//                            default:
//                                return "§cwrong format";
//                        }
//                case "WINS":
//
//                    leaderboardManager.getMostTotalWinsForRace(raceType).thenApply(winsUsers -> {
//                        switch (arguments[4].toUpperCase()) {
//                            case "USER":
//                                return Bukkit.getOfflinePlayer(winsUsers.get(rank - 1).getUuid()).getName();
//                            case "AMOUNT":
//                                return String.valueOf(winsUsers.get(rank - 1).getWins());
//                            default:
//                                return "§cwrong format";
//                        }
//                    });
//            }
        }
        return null;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "cb";
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }
}

package be.woutzah.chatbrawl.leaderboard;

import be.woutzah.chatbrawl.ChatBrawl;
import be.woutzah.chatbrawl.database.dao.LeaderboardUserDAO;
import be.woutzah.chatbrawl.leaderboard.users.TimeUser;
import be.woutzah.chatbrawl.leaderboard.users.WinsUser;
import be.woutzah.chatbrawl.races.types.RaceType;
import be.woutzah.chatbrawl.settings.SettingManager;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LeaderboardManager {

    private LeaderboardUserDAO leaderboardUserDAO;
    private SettingManager settingManager;

    public LeaderboardManager(ChatBrawl plugin) {
        this.leaderboardUserDAO = plugin.getDatabaseManager().getLeaderboardUserDAO();
        this.settingManager = plugin.getSettingManager();
    }

    public void addWin(LeaderboardStatistic leaderboardStatistic) {
        CompletableFuture.runAsync(() -> {
            leaderboardUserDAO.save(leaderboardStatistic);
        });
    }

//    public CompletableFuture<LeaderboardUser> getLeaderboardUser(UUID uuid) {
//        return CompletableFuture.supplyAsync(() -> {
//            List<LeaderboardStatistic> statisticList = leaderboardUserDAO.getLeaderboardStatisticsForUUID(uuid);
//            return new LeaderboardUser(uuid,statisticList);
//        });
//    }

    public CompletableFuture<List<WinsUser>> getMostTotalWins() {
        return CompletableFuture.supplyAsync(() -> leaderboardUserDAO.getMostTotalWins());
    }

    public CompletableFuture<List<WinsUser>> getMostTotalWinsForRace(RaceType raceType) {
        return CompletableFuture.supplyAsync(() -> leaderboardUserDAO.getMostTotalWinsForRace(raceType));
    }

    public CompletableFuture<List<TimeUser>> getFastestTimeForRace(RaceType raceType){
        return CompletableFuture.supplyAsync(() -> leaderboardUserDAO.getFastestTimeForRace(raceType));
    }

    public CompletableFuture<Map<RaceType,Integer>> getMostWinsForUser(UUID uuid){
        return CompletableFuture.supplyAsync(() -> leaderboardUserDAO.getMostWinsForUser(uuid));
    }

    public CompletableFuture<Map<RaceType,Integer>> getFastestTimeForUser(UUID uuid){
        return CompletableFuture.supplyAsync(() -> leaderboardUserDAO.getFastestTimeForUser(uuid));
    }
}

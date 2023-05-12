package be.woutzah.chatbrawl.database.dao;

import be.woutzah.chatbrawl.database.DatabaseManager;
import be.woutzah.chatbrawl.leaderboard.LeaderboardStatistic;
import be.woutzah.chatbrawl.leaderboard.users.TimeUser;
import be.woutzah.chatbrawl.leaderboard.users.WinsUser;
import be.woutzah.chatbrawl.races.types.RaceType;
import be.woutzah.chatbrawl.util.ErrorHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class LeaderboardUserDAO {

    private final DatabaseManager databaseManager;

    public LeaderboardUserDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        createTable();
    }

    public void createTable() {
        try (Connection con = databaseManager.getConnection();
             PreparedStatement stmt = con.prepareStatement("CREATE TABLE IF NOT EXISTS leaderboard (id int NOT NULL PRIMARY KEY AUTO_INCREMENT, uuid varchar(36)," +
                     "racetype varchar(36) ,seconds int)")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            ErrorHandler.error("&cDatabase could not be created! Check your MySQL Settings!");
            e.printStackTrace();
        }
    }

    public void save(LeaderboardStatistic leaderboardStatistic) {
        try (Connection con = databaseManager.getConnection();
             PreparedStatement stmt = con.prepareStatement("insert into leaderboard (uuid, racetype, seconds)" +
                     " values (?, ?, ?)")) {
            stmt.setString(1, leaderboardStatistic.getUuid().toString());
            stmt.setString(2, leaderboardStatistic.getRaceType().toString());
            stmt.setInt(3, leaderboardStatistic.getSeconds());
            stmt.executeUpdate();
        } catch (SQLException e) {
            ErrorHandler.error("&cCouldn't leaderboard statistic!");
            e.printStackTrace();
        }
    }

    public List<LeaderboardStatistic> getLeaderboardStatisticsForUUID(UUID uuid) {
        List<LeaderboardStatistic> statList = new ArrayList<>();
        try (Connection con = databaseManager.getConnection();
             PreparedStatement stmt = con.prepareStatement("SELECT * FROM leaderboard WHERE uuid=?")) {
            stmt.setString(1, uuid.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RaceType raceType = RaceType.valueOf(rs.getString("racetype").toUpperCase());
                    int seconds = rs.getInt("seconds");
                    LeaderboardStatistic statistic = new LeaderboardStatistic(uuid, raceType, seconds);
                    statList.add(statistic);
                }
            }
        } catch (SQLException e) {
            ErrorHandler.error("&cCouldn't retrieve leaderboard statistics!");
            e.printStackTrace();
        }
        return statList;
    }

    public List<WinsUser> getMostTotalWins() {
        List<WinsUser> userList = new ArrayList<>();
        String TOTALMOSTWINS = "SELECT uuid, count(uuid) as wins FROM leaderboard group by uuid order by wins DESC LIMIT 10";
        int index = 1;
        try (Connection con = databaseManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(TOTALMOSTWINS)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    int wins = rs.getInt("wins");
                    WinsUser winsUser = new WinsUser(index, uuid, wins);
                    userList.add(winsUser);
                    index++;
                }
            }
        } catch (SQLException e) {
            ErrorHandler.error("&cCouldn't retrieve most total user wins!");
            e.printStackTrace();
        }
        return userList;
    }

    public List<WinsUser> getMostTotalWinsForRace(RaceType raceType) {
        List<WinsUser> userList = new ArrayList<>();
        String TOTALMOSTWINSFORRACE = "SELECT uuid, count(uuid) as wins FROM leaderboard WHERE racetype=? group by uuid order by wins DESC LIMIT 10";
        int index = 1;
        try (Connection con = databaseManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(TOTALMOSTWINSFORRACE)) {
            stmt.setString(1, raceType.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    int wins = rs.getInt("wins");
                    WinsUser winsUser = new WinsUser(index, uuid, wins);
                    userList.add(winsUser);
                    index++;
                }
            }
        } catch (SQLException e) {
            ErrorHandler.error("&cCouldn't retrieve most total user wins for race!");
            e.printStackTrace();
        }
        return userList;
    }

    public List<TimeUser> getFastestTimeForRace(RaceType raceType){
        List<TimeUser> userList = new ArrayList<>();
        String FASTESTTIMEFORRACE = "SELECT uuid, min(seconds) as time FROM leaderboard WHERE racetype=? group by uuid order by time ASC LIMIT 10";
        int index = 1;
        try (Connection con = databaseManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(FASTESTTIMEFORRACE)) {
            stmt.setString(1, raceType.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    int seconds = rs.getInt("time");
                    TimeUser timeUser = new TimeUser(index, uuid, seconds);
                    userList.add(timeUser);
                    index++;
                }
            }
        } catch (SQLException e) {
            ErrorHandler.error("&cCouldn't retrieve fastest time users for race!");
            e.printStackTrace();
        }
        return userList;
    }

    public Map<RaceType,Integer> getMostWinsForUser(UUID uuid){
        Map<RaceType,Integer> raceWinsMap = new HashMap<>();
        String MOSTWINSFORUSER = "SELECT racetype, count(uuid) as wins FROM leaderboard WHERE uuid=? group by racetype order by wins ASC LIMIT 10";
        try (Connection con = databaseManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(MOSTWINSFORUSER)) {
            stmt.setString(1, uuid.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RaceType racetype = null;
                    try {
                        racetype = RaceType.valueOf(rs.getString("racetype").toUpperCase());
                    } catch (Exception ex) {
                        ErrorHandler.error("&cCouldn't retrieve wins for user! --> invalid racetype");
                    }
                    int wins = rs.getInt("wins");
                    raceWinsMap.put(racetype,wins);
                }
            }
        } catch (SQLException e) {
            ErrorHandler.error("&cCouldn't retrieve wins for user!");
            e.printStackTrace();
        }
        return raceWinsMap;
    }

    public Map<RaceType,Integer> getFastestTimeForUser(UUID uuid){
        Map<RaceType,Integer> raceTimesMap = new HashMap<>();
        String FASTESTTIMESFORUSER = "SELECT racetype, min(seconds) as time FROM leaderboard WHERE uuid=? group by racetype order by time DESC LIMIT 10";
        try (Connection con = databaseManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(FASTESTTIMESFORUSER)) {
            stmt.setString(1, uuid.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RaceType racetype = null;
                    try {
                        racetype = RaceType.valueOf(rs.getString("racetype").toUpperCase());
                    } catch (Exception ex) {
                        ErrorHandler.error("&cCouldn't retrieve fastest times for user! --> invalid racetype");
                    }
                    int seconds = rs.getInt("time");
                    raceTimesMap.put(racetype,seconds);
                }
            }
        } catch (SQLException e) {
            ErrorHandler.error("&cCouldn't retrieve fastest times for user!");
            e.printStackTrace();
        }
        return raceTimesMap;
    }
}

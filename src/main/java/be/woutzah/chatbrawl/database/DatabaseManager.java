package be.woutzah.chatbrawl.database;

import be.woutzah.chatbrawl.ChatBrawl;
import be.woutzah.chatbrawl.database.dao.LeaderboardUserDAO;
import be.woutzah.chatbrawl.settings.GeneralSetting;
import be.woutzah.chatbrawl.settings.SettingManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private final ChatBrawl plugin;
    private final SettingManager settingManager;
    private final LeaderboardUserDAO leaderboardUserDAO;

    public DatabaseManager(ChatBrawl plugin) {
        this.plugin = plugin;
        this.settingManager = plugin.getSettingManager();
        this.leaderboardUserDAO = new LeaderboardUserDAO(this);
    }

    private String generateUrl() {
        String host = settingManager.getString(GeneralSetting.MYSQL_HOST);
        String port = settingManager.getString(GeneralSetting.MYSQL_PORT);
        String database = settingManager.getString(GeneralSetting.MYSQL_DATABASE);
        return "jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&useSSL=false";
    }

    private String getDBUser() {
        return settingManager.getString(GeneralSetting.MYSQL_USERNAME);
    }

    private String getDBPassword() {
        return settingManager.getString(GeneralSetting.MYSQL_PASSWORD);
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(generateUrl(), getDBUser(), getDBPassword());
    }

    public LeaderboardUserDAO getLeaderboardUserDAO() {
        return leaderboardUserDAO;
    }
}

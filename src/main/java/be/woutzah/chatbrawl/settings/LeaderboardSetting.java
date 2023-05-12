package be.woutzah.chatbrawl.settings;

import be.woutzah.chatbrawl.files.ConfigType;

public enum LeaderboardSetting implements Setting {

    TOTAL_HEADER("leaderboard-total.header"),
    TOTAL_ENTRY("leaderboard-total.entry"),
    TOTAL_FOOTER("leaderboard-total.footer"),
    TOTALRACE_HEADER("leaderboard-total-for-race.header"),
    TOTALRACE_ENTRY("leaderboard-total-for-race.entry"),
    TOTALRACE_FOOTER("leaderboard-total-for-race.footer"),
    FASTESTRACE_HEADER("leaderboard-fastest-for-race.header"),
    FASTESTRACE_ENTRY("leaderboard-fastest-for-race.entry"),
    FASTESTRACE_FOOTER("leaderboard-fastest-for-race.footer"),
    TOTALUSER_HEADER("leaderboard-total-for-user.header"),
    TOTALUSER_ENTRY("leaderboard-total-for-user.entry"),
    TOTALUSER_FOOTER("leaderboard-total-for-user.footer"),
    FASTESTUSER_HEADER("leaderboard-fastest-for-user.header"),
    FASTESTUSER_ENTRY("leaderboard-fastest-for-user.entry"),
    FASTESTUSER_FOOTER("leaderboard-fastest-for-user.footer");


    private final String path;
    private final ConfigType configType;

    LeaderboardSetting(String path) {
        this.path = path;
        this.configType = ConfigType.LEADERBOARD;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public ConfigType getConfigType() {
        return configType;
    }
}

package be.woutzah.chatbrawl.settings;

import be.woutzah.chatbrawl.files.ConfigType;

public enum LanguageSetting implements Setting {
    LANG("lang"),
    HELPMENU("general.helpmenu"),
    STARTED_CREATING("general.started-creating"),
    NO_PERMISSION("commands.no-permission"),
    SUBCOMMAND_NOT_EXIST("commands.subcommand-not-exist"),
    STOPPED_RACE("commands.stopped-race"),
    START_RACE_USAGE("commands.start-race-usage"),
    STARTED_RACE("commands.started-race"),
    RACETYPE_NOT_EXIST("commands.racetype-not-exist"),
    RACE_STILL_RUNNING("commands.race-still-running"),
    NO_RACE_RUNNING("commands.no-race-running"),
    DISABLED("commands.disabled"),
    ALREADY_DISABLED("commands.already-disabled"),
    ENABLED("commands.enabled"),
    ALREADY_ENABLED("commands.already-enabled"),
    LEADERBOARD_TYPE("leaderboards.specify-type"),
    LEADERBOARD_VALID_TYPE("leaderboards.specify-valid-type"),
    LEADERBOARD_SUBTYPE("leaderboards.specify-subtype"),
    LEADERBOARD_VALID_SUBTYPE("leaderboards.specify-valid-subtype"),
    LEADERBOARD_RACE("leaderboards.specify-race"),
    LEADERBOARD_VALID_RACE("leaderboards.specify-valid-race"),
    LEADERBOARD_USER("leaderboards.specify-user"),
    LEADERBOARD_NOT_PLAYED_BEFORE("leaderboards.not-played-before");

    private final String path;
    private final ConfigType configType;

    LanguageSetting(String path) {
        this.path = path;
        this.configType = ConfigType.LANGUAGE;
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

package be.woutzah.chatbrawl.settings;

import be.woutzah.chatbrawl.files.ConfigType;

public enum GeneralSetting implements Setting{
    PLUGIN_PREFIX("plugin-prefix"),
    AUTO_CREATE_RACES("auto-create-races"),
    RACE_DELAY("race-delay"),
    NOTIFY_ON_LOGIN("notify-on-login"),
    MINIMUM_PLAYERS("minimum-players"),
    ALLOW_CREATIVE("allow-creative"),
    IGNORED_COMMANDS("ignored-commands"),
    DISABLED_WORLDS("disabled-worlds"),
    MYSQL_ENABLED("mysql.enabled"),
    MYSQL_HOST("mysql.host"),
    MYSQL_USERNAME("mysql.username"),
    MYSQL_PASSWORD("mysql.password"),
    MYSQL_PORT("mysql.port"),
    MYSQL_DATABASE("mysql.database");

    private final String path;
    private final ConfigType configType;

    GeneralSetting(String path) {
        this.path = path;
        this.configType = ConfigType.MAIN;
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

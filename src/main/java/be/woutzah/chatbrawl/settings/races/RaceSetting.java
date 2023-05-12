package be.woutzah.chatbrawl.settings.races;

import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.settings.Setting;

public enum RaceSetting implements Setting {
    ENABLED("enabled"),
    CHANCE("chance"),
    DURATION("duration"),
    ENABLE_SOUND("sound.enabled"),
    BEGINSOUND("sound.begin-race"),
    ENDSOUND("sound.end-race"),
    ENABLE_FIREWORK("enable-firework"),
    ENABLE_BOSSBAR("enable-bossbar"),
    BOSSBAR_COLOR("bossbar-color"),
    BOSSBAR_STYLE("bossbar-style"),
    ENABLE_ACTIONBAR("enable-actionbar"),
    BROADCAST_CENTER_MESSAGES("broadcast.center-messages"),
    BROADCAST_ENABLE_START("broadcast.enable-start"),
    BROADCAST_ENABLE_END("broadcast.enable-end"),
    LANGUAGE_BOSSBAR("language.bossbar"),
    LANGUAGE_ACTIONBAR("language.actionbar"),
    LANGUAGE_START("language.start"),
    LANGUAGE_ENDED("language.ended"),
    LANGUAGE_WINNER("language.winner"),
    LANGUAGE_WINNER_PERSONAL("language.winner-personal");

    private final String path;
    private final ConfigType configType;

    RaceSetting(String path) {
        this.path = path;
        this.configType = null;
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

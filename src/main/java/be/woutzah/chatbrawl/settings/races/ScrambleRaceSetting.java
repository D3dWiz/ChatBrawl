package be.woutzah.chatbrawl.settings.races;

import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.settings.Setting;

public enum ScrambleRaceSetting implements Setting {
    WORDS("words");

    private final String path;
    private final ConfigType configType;

    ScrambleRaceSetting(String path) {
        this.path = path;
        this.configType = ConfigType.SCRAMBLERACE;
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

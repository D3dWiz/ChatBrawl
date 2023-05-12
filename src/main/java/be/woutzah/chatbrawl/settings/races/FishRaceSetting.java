package be.woutzah.chatbrawl.settings.races;

import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.settings.Setting;

public enum FishRaceSetting implements Setting {
    FISH("fish");

    private final String path;
    private final ConfigType configType;

    FishRaceSetting(String path) {
        this.path = path;
        this.configType = ConfigType.FISHRACE;
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

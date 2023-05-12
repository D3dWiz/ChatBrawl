package be.woutzah.chatbrawl.settings.races;

import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.settings.Setting;

public enum CraftRaceSetting implements Setting {

    ITEMS("items");

    private final String path;
    private final ConfigType configType;

    CraftRaceSetting(String path) {
        this.path = path;
        this.configType = ConfigType.CRAFTRACE;
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
